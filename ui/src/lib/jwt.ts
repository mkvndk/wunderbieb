export type JwtPayload = Record<string, unknown> & {
  preferred_username?: string;
  name?: string;
  email?: string;
  exp?: number;
};

export function parseJwtPayload(token: string): JwtPayload {
  const payload = token.split('.')[1];
  if (!payload) {
    throw new Error('JWT payload ontbreekt.');
  }
  const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
  const padded = normalized.padEnd(normalized.length + ((4 - normalized.length % 4) % 4), '=');
  return JSON.parse(window.atob(padded)) as JwtPayload;
}
