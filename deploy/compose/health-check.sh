#!/usr/bin/env bash
set -euo pipefail

ENV_FILE="${1:-deploy/compose/.env}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Env-bestand niet gevonden: $ENV_FILE" >&2
  exit 1
fi

set -a
source "$ENV_FILE"
set +a

API_BASE="http://localhost:${API_PORT}"
UI_BASE="http://localhost:${UI_PORT}"
KEYCLOAK_BASE="http://localhost:${KEYCLOAK_PORT}/realms/${KEYCLOAK_REALM}"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Vereist commando ontbreekt: $1" >&2
    exit 1
  fi
}

extract_json_value() {
  local key="$1"
  sed -n "s/.*\"${key}\":\"\\([^\"]*\\)\".*/\\1/p"
}

get_token() {
  local username="$1"
  local password="$2"
  curl -fsS -X POST "${KEYCLOAK_BASE}/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "client_id=wunderbieb-cli" \
    -d "username=${username}" \
    -d "password=${password}" \
    -d "grant_type=password"
}

require_command curl
require_command sed

echo "1. UI bereikbaar?"
curl -fsS "${UI_BASE}" >/dev/null
echo "   OK ${UI_BASE}"

echo "2. API health?"
curl -fsS "${API_BASE}/actuator/health"
echo

echo "3. Keycloak token voor super-admin?"
SUPER_ADMIN_TOKEN="$(get_token "super-admin" "Welkom123!" | extract_json_value "access_token")"
if [[ -z "${SUPER_ADMIN_TOKEN}" ]]; then
  echo "   Geen access_token ontvangen voor super-admin" >&2
  exit 1
fi
echo "   OK token ontvangen"

echo "4. Admin endpoint met super-admin token?"
curl -fsS "${API_BASE}/api/admin/roles" \
  -H "Authorization: Bearer ${SUPER_ADMIN_TOKEN}" >/dev/null
echo "   OK admin endpoint bereikbaar"

echo "5. Sessieresolutie via /api/session?"
curl -fsS "${API_BASE}/api/session" \
  -H "Authorization: Bearer ${SUPER_ADMIN_TOKEN}" >/dev/null
echo "   OK sessie endpoint bereikbaar"

echo "6. Deny-case zonder assignment?"
NO_ASSIGNMENT_TOKEN="$(get_token "zonder-assignment" "Welkom123!" | extract_json_value "access_token")"
if [[ -z "${NO_ASSIGNMENT_TOKEN}" ]]; then
  echo "   Geen access_token ontvangen voor zonder-assignment" >&2
  exit 1
fi

DENY_STATUS="$(
  curl -s -o /tmp/wunderbieb-deny-response.json -w "%{http_code}" \
    "${API_BASE}/api/admin/roles" \
    -H "Authorization: Bearer ${NO_ASSIGNMENT_TOKEN}"
)"

if [[ "${DENY_STATUS}" != "401" ]]; then
  echo "   Verwachtte 401 voor zonder-assignment, kreeg ${DENY_STATUS}" >&2
  cat /tmp/wunderbieb-deny-response.json >&2
  exit 1
fi
echo "   OK deny-case geeft 401"

echo "Alles geslaagd."
