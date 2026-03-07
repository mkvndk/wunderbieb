import { useEffect, useEffectEvent, useMemo, useState } from 'react';
import { parseJwtPayload } from '../lib/jwt';
import {
  beginLoginRedirect,
  beginLogoutRedirect,
  clearAuthCallbackParameters,
  clearStoredTokenSet,
  completeLoginFromCallback,
  isExpiringSoon,
  loadStoredTokenSet,
  refreshTokenSet,
  type TokenSet
} from '../lib/oidc';
import type { CurrentSession } from '../types';

type AuthState =
  | { status: 'checking' }
  | { status: 'signed_out' }
  | { status: 'signed_in'; tokenSet: TokenSet }
  | { status: 'error'; message: string };

export function useAuth() {
  const [state, setState] = useState<AuthState>({ status: 'checking' });

  const applyTokenSet = useEffectEvent((tokenSet: TokenSet) => {
    setState({ status: 'signed_in', tokenSet });
  });

  const failAuthentication = useEffectEvent((message: string) => {
    clearStoredTokenSet();
    setState({ status: 'error', message });
  });

  const refreshInBackground = useEffectEvent(async () => {
    if (state.status !== 'signed_in') {
      return;
    }
    try {
      const refreshed = await refreshTokenSet(state.tokenSet);
      applyTokenSet(refreshed);
    } catch (error) {
      failAuthentication(getErrorMessage(error));
    }
  });

  useEffect(() => {
    let cancelled = false;

    async function bootstrap() {
      try {
        const currentUrl = new URL(window.location.href);
        const callbackError = currentUrl.searchParams.get('error');
        if (callbackError) {
          const description = currentUrl.searchParams.get('error_description');
          const message = description ? `${callbackError}: ${description}` : callbackError;
          clearAuthCallbackParameters();
          throw new Error(`OIDC callback-fout: ${message}`);
        }
        if (currentUrl.searchParams.has('code')) {
          const tokenSet = await completeLoginFromCallback(currentUrl);
          clearAuthCallbackParameters();
          if (!cancelled) {
            applyTokenSet(tokenSet);
          }
          return;
        }

        const storedTokenSet = loadStoredTokenSet();
        if (!storedTokenSet) {
          if (!cancelled) {
            setState({ status: 'signed_out' });
          }
          return;
        }

        const nextTokenSet = isExpiringSoon(storedTokenSet) ? await refreshTokenSet(storedTokenSet) : storedTokenSet;
        if (!cancelled) {
          applyTokenSet(nextTokenSet);
        }
      } catch (error) {
        if (!cancelled) {
          failAuthentication(getErrorMessage(error));
        }
      }
    }

    void bootstrap();

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (state.status !== 'signed_in') {
      return undefined;
    }
    const timeout = window.setTimeout(() => {
      void refreshInBackground();
    }, Math.max(state.tokenSet.expiresAt - Date.now() - 60_000, 5_000));
    return () => window.clearTimeout(timeout);
  }, [state, refreshInBackground]);

  const session = useMemo<CurrentSession | null>(() => {
    if (state.status !== 'signed_in') {
      return null;
    }
    const payload = parseJwtPayload(state.tokenSet.accessToken);
    return {
      userId: parseNumericClaim(payload.user_id),
      preferredUsername: getString(payload.preferred_username),
      fullName: getString(payload.name),
      email: getString(payload.email),
      roleCode: getString(payload.role_code) ?? 'ONBEKEND',
      scopeType: getString(payload.scope_type) ?? 'ONBEKEND',
      permissionLevel: getString(payload.permissionLevel) ?? 'ONBEKEND',
      capabilities: Array.isArray(payload.scope) ? [] : [],
      boardId: parseNumericClaim(payload.board_id),
      schoolId: parseNumericClaim(payload.school_id)
    };
  }, [state]);

  return {
    state,
    accessToken: state.status === 'signed_in' ? state.tokenSet.accessToken : null,
    session,
    login: () => {
      setState({ status: 'checking' });
      void beginLoginRedirect();
    },
    logout: () => {
      if (state.status === 'signed_in') {
        beginLogoutRedirect(state.tokenSet.idToken);
      } else {
        clearStoredTokenSet();
        setState({ status: 'signed_out' });
      }
    }
  };
}

function getString(value: unknown) {
  return typeof value === 'string' ? value : null;
}

function parseNumericClaim(value: unknown) {
  if (typeof value === 'number') {
    return value;
  }
  if (typeof value === 'string' && value !== '') {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
  return null;
}

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : 'Er is een onbekende authenticatiefout opgetreden.';
}
