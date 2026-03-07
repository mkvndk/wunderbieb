import { parseJwtPayload } from './jwt';

const TOKEN_STORAGE_KEY = 'wunderbieb.token-set';
const PKCE_VERIFIER_KEY = 'wunderbieb.pkce.verifier';
const OIDC_STATE_KEY = 'wunderbieb.oidc.state';

const realm = envOrDefault(import.meta.env.VITE_OIDC_REALM, 'wunderbieb-dev');
const clientId = envOrDefault(import.meta.env.VITE_OIDC_CLIENT_ID, 'wunderbieb-ui');
const oidcBasePath = normalizeBasePath(
  envOrDefault(import.meta.env.VITE_OIDC_BASE_PATH, `/realms/${realm}/protocol/openid-connect`)
);

export type TokenSet = {
  accessToken: string;
  refreshToken: string | null;
  idToken: string | null;
  expiresAt: number;
};

export function loadStoredTokenSet(): TokenSet | null {
  const raw = window.sessionStorage.getItem(TOKEN_STORAGE_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw) as TokenSet;
  } catch {
    clearStoredTokenSet();
    return null;
  }
}

export function clearStoredTokenSet() {
  window.sessionStorage.removeItem(TOKEN_STORAGE_KEY);
}

export async function beginLoginRedirect() {
  const verifier = generateRandomString(96);
  const challenge = await createCodeChallenge(verifier);
  const state = generateRandomString(32);
  window.sessionStorage.setItem(PKCE_VERIFIER_KEY, verifier);
  window.sessionStorage.setItem(OIDC_STATE_KEY, state);

  const authorizeUrl = new URL(`${window.location.origin}${oidcBasePath}/auth`);
  authorizeUrl.searchParams.set('client_id', clientId);
  authorizeUrl.searchParams.set('redirect_uri', getRedirectUri());
  authorizeUrl.searchParams.set('response_type', 'code');
  authorizeUrl.searchParams.set('scope', 'openid');
  authorizeUrl.searchParams.set('code_challenge', challenge);
  authorizeUrl.searchParams.set('code_challenge_method', 'S256');
  authorizeUrl.searchParams.set('state', state);
  window.location.assign(authorizeUrl.toString());
}

export async function completeLoginFromCallback(currentUrl: URL): Promise<TokenSet> {
  const code = currentUrl.searchParams.get('code');
  const state = currentUrl.searchParams.get('state');
  const storedState = window.sessionStorage.getItem(OIDC_STATE_KEY);
  const verifier = window.sessionStorage.getItem(PKCE_VERIFIER_KEY);

  if (!code || !state || !storedState || !verifier) {
    throw new Error('De OIDC callback bevat niet alle vereiste parameters.');
  }
  if (state !== storedState) {
    throw new Error('De OIDC state komt niet overeen.');
  }

  const response = await fetch(`${oidcBasePath}/token`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: new URLSearchParams({
      client_id: clientId,
      grant_type: 'authorization_code',
      code,
      redirect_uri: getRedirectUri(),
      code_verifier: verifier
    })
  });

  if (!response.ok) {
    throw new Error('Het ophalen van OIDC tokens is mislukt.');
  }

  const tokenResponse = await response.json() as {
    access_token: string;
    refresh_token?: string;
    id_token?: string;
  };

  window.sessionStorage.removeItem(OIDC_STATE_KEY);
  window.sessionStorage.removeItem(PKCE_VERIFIER_KEY);

  return storeTokenSet(tokenResponse.access_token, tokenResponse.refresh_token ?? null, tokenResponse.id_token ?? null);
}

export async function refreshTokenSet(tokenSet: TokenSet): Promise<TokenSet> {
  if (!tokenSet.refreshToken) {
    return tokenSet;
  }

  const response = await fetch(`${oidcBasePath}/token`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: new URLSearchParams({
      client_id: clientId,
      grant_type: 'refresh_token',
      refresh_token: tokenSet.refreshToken
    })
  });

  if (!response.ok) {
    clearStoredTokenSet();
    throw new Error('Het vernieuwen van de OIDC-sessie is mislukt.');
  }

  const tokenResponse = await response.json() as {
    access_token: string;
    refresh_token?: string;
    id_token?: string;
  };

  return storeTokenSet(
    tokenResponse.access_token,
    tokenResponse.refresh_token ?? tokenSet.refreshToken,
    tokenResponse.id_token ?? tokenSet.idToken
  );
}

export function isExpiringSoon(tokenSet: TokenSet, withinSeconds = 30) {
  return tokenSet.expiresAt - Date.now() <= withinSeconds * 1000;
}

export function clearAuthCallbackParameters() {
  const cleanUrl = new URL(window.location.href);
  cleanUrl.searchParams.delete('code');
  cleanUrl.searchParams.delete('state');
  cleanUrl.searchParams.delete('session_state');
  cleanUrl.searchParams.delete('iss');
  window.history.replaceState({}, document.title, cleanUrl.toString());
}

export function beginLogoutRedirect(idTokenHint: string | null) {
  clearStoredTokenSet();
  const logoutUrl = new URL(`${window.location.origin}${oidcBasePath}/logout`);
  logoutUrl.searchParams.set('client_id', clientId);
  logoutUrl.searchParams.set('post_logout_redirect_uri', getRedirectUri());
  if (idTokenHint) {
    logoutUrl.searchParams.set('id_token_hint', idTokenHint);
  }
  window.location.assign(logoutUrl.toString());
}

function storeTokenSet(accessToken: string, refreshToken: string | null, idToken: string | null): TokenSet {
  const payload = parseJwtPayload(accessToken);
  const expiresAt = typeof payload.exp === 'number' ? payload.exp * 1000 : Date.now() + 5 * 60 * 1000;
  const tokenSet = { accessToken, refreshToken, idToken, expiresAt };
  window.sessionStorage.setItem(TOKEN_STORAGE_KEY, JSON.stringify(tokenSet));
  return tokenSet;
}

function getRedirectUri() {
  return `${window.location.origin}${window.location.pathname}`;
}

function generateRandomString(length: number) {
  const bytes = new Uint8Array(length);
  window.crypto.getRandomValues(bytes);
  return base64UrlEncode(bytes);
}

async function createCodeChallenge(verifier: string) {
  const bytes = new TextEncoder().encode(verifier);
  const digest = await window.crypto.subtle.digest('SHA-256', bytes);
  return base64UrlEncode(new Uint8Array(digest));
}

function base64UrlEncode(bytes: Uint8Array) {
  const binary = Array.from(bytes, (byte) => String.fromCharCode(byte)).join('');
  return window.btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '');
}

function envOrDefault(value: string | undefined, fallback: string) {
  if (!value) {
    return fallback;
  }
  const normalized = value.trim();
  return normalized === '' ? fallback : normalized;
}

function normalizeBasePath(path: string) {
  const withLeadingSlash = path.startsWith('/') ? path : `/${path}`;
  return withLeadingSlash.replace(/\/+/g, '/').replace(/\/$/, '');
}
