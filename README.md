# wunderbieb

Repository voor het KMS-platform van Wunderbieb.

De bron van waarheid voor projectdocumentatie staat in [`docs/`](./docs/index.md). Gebruik die map als startpunt voor architectuur, productafspraken, backend, frontend, toegankelijkheid en scoring.

## Vastgestelde stack

- Backend: Java 25, Spring Boot 3.5.x
- Database: PostgreSQL met Row Level Security
- Object storage: MinIO (S3-compatibel)
- Frontend: React
- Authenticatie: OIDC

## Huidige status

Dit project zit nog in de opstartfase, maar bevat inmiddels wel een eerste werkende codebasis naast de documentatie.

Afgerond:

- documentatie onder `docs/` als bron van waarheid
- backend multi-module Maven bootstrap
- eerste admin-API's voor rollen, capabilities, assignments, taxonomie en scoreconfiguratie
- PostgreSQL/Flyway/JPA-basis met seeddata voor beheerreferenties
- audit-opslag via database
- React beheershell in `ui/`

Nog open:

- echte OIDC claim-mapping naar assignments en capabilities
- PostgreSQL en MinIO in een lokale runtime-setup
- documentmodule, onboardingopslag, zoekfunctie en insights-aggregaties
- React koppelen aan live API's in plaats van mockdata

Zie [`docs/status.md`](./docs/status.md) voor de actuele voortgang per onderdeel.

## Kernprincipes

- Server-side autorisatie is leidend.
- Scope-filtering geldt op applicatie- en databaseniveau.
- Externe rollen hebben een eigen scopetype en eigen datafilter.
- Alle productteksten zijn Nederlands.
- Webinterfaces voldoen minimaal aan WCAG 2.2 AA / EN 301 549.

## Startpunt

Open [`docs/index.md`](./docs/index.md) en lees daarna ten minste:

1. `docs/architecture/overview.md`
2. `docs/architecture/authz-and-scope.md`
3. `docs/product/document-types-and-workflows.md`
4. `docs/product/scoring-and-inspection-model.md`
5. `docs/frontend/accessibility-and-language.md`
