import { lazy, Suspense } from 'react';
import { useAdminData } from './hooks/useAdminData';
import { useAuth } from './hooks/useAuth';
import type { AdminSnapshot, Capability } from './types';

const TiptapDocumentEditor = lazy(async () => import('./editor/TiptapDocumentEditor').then((module) => ({ default: module.TiptapDocumentEditor })));

function App() {
  const auth = useAuth();
  const adminData = useAdminData(auth.accessToken);
  const activeSession = adminData.snapshot?.session ?? auth.session;

  return (
    <div className="app-shell">
      <a className="skip-link" href="#hoofdinhoud">Direct naar inhoud</a>
      <header className="page-header">
        <div>
          <p className="eyebrow">Wunderbieb</p>
          <h1>Beheerconsole</h1>
          <p className="intro">
            Deze omgeving leest nu live uit de admin-API en gebruikt OIDC login via Keycloak.
          </p>
        </div>
        <div className="header-actions">
          {auth.state.status === 'signed_in' ? (
            <button type="button" onClick={auth.logout}>Uitloggen</button>
          ) : (
            <button type="button" onClick={auth.login}>Inloggen met Keycloak</button>
          )}
        </div>
      </header>

      <main id="hoofdinhoud" className="page-main">
        {auth.state.status === 'checking' && (
          <section className="hero-card" aria-live="polite">
            <h2>Sessie opbouwen</h2>
            <p>De browser controleert de OIDC-sessie en haalt daarna de admingegevens op.</p>
          </section>
        )}

        {auth.state.status === 'signed_out' && (
          <section className="hero-card">
            <h2>Nog niet ingelogd</h2>
            <p>
              Gebruik een lokaal Keycloak-account zoals <strong>super-admin</strong> om de live beheerdata te bekijken.
            </p>
            <button type="button" onClick={auth.login}>Inloggen</button>
          </section>
        )}

        {auth.state.status === 'error' && (
          <section className="hero-card error-card" aria-live="assertive">
            <h2>Authenticatie mislukt</h2>
            <p>{auth.state.message}</p>
            <button type="button" onClick={auth.login}>Opnieuw inloggen</button>
          </section>
        )}

        {auth.state.status === 'signed_in' && activeSession && (
          <>
            <section aria-labelledby="sessie-titel" className="hero-card">
              <div>
                <h2 id="sessie-titel">Actieve sessie</h2>
                <p className="muted">
                  Ingelogd als {activeSession.fullName ?? activeSession.preferredUsername ?? 'onbekende gebruiker'}.
                </p>
              </div>
              <dl className="stats-grid">
                <div>
                  <dt>Gebruiker</dt>
                  <dd>{activeSession.preferredUsername ?? 'onbekend'}</dd>
                </div>
                <div>
                  <dt>Rol</dt>
                  <dd>{activeSession.roleCode}</dd>
                </div>
                <div>
                  <dt>Scope</dt>
                  <dd>{activeSession.scopeType}</dd>
                </div>
                <div>
                  <dt>Rechten</dt>
                  <dd>{activeSession.capabilities.length}</dd>
                </div>
              </dl>
            </section>

            {adminData.status === 'loading' && (
              <section className="panel" aria-live="polite">
                <h2>Gegevens laden</h2>
                <p>De beheergegevens worden opgehaald uit de backend.</p>
              </section>
            )}

            {adminData.status === 'error' && (
              <section className="panel error-card" aria-live="assertive">
                <h2>Gegevens laden mislukt</h2>
                <p>{adminData.message}</p>
                <button type="button" onClick={adminData.reload}>Opnieuw laden</button>
              </section>
            )}

            {adminData.status === 'ready' && adminData.snapshot && (
              <>
                <section aria-labelledby="overzicht-titel" className="hero-card">
                  <div>
                    <h2 id="overzicht-titel">Beheerstatus</h2>
                    <p>
                      Dit overzicht komt live uit de admin-API. De beschikbare blokken volgen de capabilities van de
                      ingelogde gebruiker.
                    </p>
                  </div>
                  <dl className="stats-grid">
                    <div>
                      <dt>Rollen</dt>
                      <dd>{adminData.snapshot.roles.length}</dd>
                    </div>
                    <div>
                      <dt>Capabilities</dt>
                      <dd>{adminData.snapshot.capabilities.length}</dd>
                    </div>
                    <div>
                      <dt>Assignments</dt>
                      <dd>{adminData.snapshot.assignments.length}</dd>
                    </div>
                    <div>
                      <dt>Scorewaarden</dt>
                      <dd>{adminData.snapshot.scoreConfigurations.length}</dd>
                    </div>
                  </dl>
                </section>

                {activeSession.capabilities.includes('MANAGE_USERS') && (
                  <>
                    <section aria-labelledby="rollen-titel" className="panel">
                      <div className="panel-header">
                        <h2 id="rollen-titel">Rollen</h2>
                        <button type="button" disabled aria-disabled="true">Nieuwe rol toevoegen</button>
                      </div>
                      <ul className="card-grid">
                        {adminData.snapshot.roles.map((role) => (
                          <li key={role.code} className="card">
                            <h3>{role.displayNameNl}</h3>
                            <p className="muted">
                              {role.code} · {role.scopeType} · {role.permissionLevel}
                            </p>
                            <p>{role.descriptionNl}</p>
                            <CapabilityList capabilityCodes={role.capabilityCodes} capabilities={adminData.snapshot.capabilities} />
                          </li>
                        ))}
                      </ul>
                    </section>

                    <section aria-labelledby="capabilities-titel" className="panel">
                      <div className="panel-header">
                        <h2 id="capabilities-titel">Capabilities</h2>
                        <button type="button" disabled aria-disabled="true">Capability beheren</button>
                      </div>
                      <table>
                        <caption className="sr-only">Overzicht van capabilities</caption>
                        <thead>
                          <tr>
                            <th scope="col">Code</th>
                            <th scope="col">Naam</th>
                            <th scope="col">Toelichting</th>
                          </tr>
                        </thead>
                        <tbody>
                          {adminData.snapshot.capabilities.map((capability) => (
                            <tr key={capability.code}>
                              <td>{capability.code}</td>
                              <td>{capability.displayNameNl}</td>
                              <td>{capability.descriptionNl}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </section>

                    <section aria-labelledby="assignments-titel" className="panel">
                      <div className="panel-header">
                        <h2 id="assignments-titel">Assignments van ingelogde gebruiker</h2>
                        <button type="button" disabled aria-disabled="true">Assignment toevoegen</button>
                      </div>
                      <table>
                        <caption className="sr-only">Overzicht van gebruikersassignments</caption>
                        <thead>
                          <tr>
                            <th scope="col">Rol</th>
                            <th scope="col">Scope</th>
                            <th scope="col">Bestuur</th>
                            <th scope="col">School</th>
                            <th scope="col">Actief</th>
                          </tr>
                        </thead>
                        <tbody>
                          {adminData.snapshot.assignments.map((assignment) => (
                            <tr key={assignment.id}>
                              <td>{assignment.roleCode}</td>
                              <td>{assignment.scopeType}</td>
                              <td>{assignment.boardId ?? '-'}</td>
                              <td>{assignment.schoolId ?? '-'}</td>
                              <td>{assignment.active ? 'Ja' : 'Nee'}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </section>
                  </>
                )}

                {activeSession.capabilities.includes('MANAGE_TAXONOMY') && (
                  <>
                    <section aria-labelledby="domeinen-titel" className="panel">
                      <div className="panel-header">
                        <h2 id="domeinen-titel">Inspectiedomeinen en onderwerpen</h2>
                        <button type="button" disabled aria-disabled="true">Domein beheren</button>
                      </div>
                      <div className="taxonomy-grid">
                        {adminData.snapshot.inspectionDomains.map((domain) => (
                          <article key={domain.code} className="card">
                            <h3>{domain.displayNameNl}</h3>
                            <p className="muted">{domain.code}</p>
                            <p>{domain.descriptionNl}</p>
                            <ul>
                              {adminData.snapshot.inspectionTopics
                                .filter((topic) => topic.domainId === domain.id)
                                .map((topic) => (
                                  <li key={topic.code}>
                                    <strong>{topic.displayNameNl}</strong>
                                    <span className="topic-code"> {topic.code}</span>
                                  </li>
                                ))}
                            </ul>
                          </article>
                        ))}
                      </div>
                    </section>

                    <section aria-labelledby="documenttypen-titel" className="panel">
                      <div className="panel-header">
                        <h2 id="documenttypen-titel">Documenttypen</h2>
                        <button type="button" disabled aria-disabled="true">Documenttype toevoegen</button>
                      </div>
                      <table>
                        <caption className="sr-only">Overzicht van documenttypen</caption>
                        <thead>
                          <tr>
                            <th scope="col">Code</th>
                            <th scope="col">Naam</th>
                            <th scope="col">Omschrijving</th>
                            <th scope="col">Onboarding</th>
                          </tr>
                        </thead>
                        <tbody>
                          {adminData.snapshot.documentTypes.map((documentType) => (
                            <tr key={documentType.code}>
                              <td>{documentType.code}</td>
                              <td>{documentType.displayNameNl}</td>
                              <td>{documentType.descriptionNl}</td>
                              <td>{documentType.requiredForOnboarding ? 'Ja' : 'Nee'}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </section>
                  </>
                )}

                {activeSession.capabilities.includes('MANAGE_SCORE_CONFIGURATION') && (
                  <section aria-labelledby="score-titel" className="panel">
                    <div className="panel-header">
                      <h2 id="score-titel">Scoreconfiguratie</h2>
                      <button type="button" disabled aria-disabled="true">Scorewaarde aanpassen</button>
                    </div>
                    <ul className="score-list">
                      {adminData.snapshot.scoreConfigurations.map((score) => (
                        <li key={score.code} className="score-row">
                          <div>
                            <h3>{score.displayLabelNl}</h3>
                            <p className="muted">{score.code}</p>
                            <p>{score.descriptionNl}</p>
                          </div>
                          <div className="score-value">{score.numericValue}</div>
                          <span className="status-chip">{score.active ? 'Actief' : 'Inactief'}</span>
                        </li>
                      ))}
                    </ul>
                  </section>
                )}

                <section aria-labelledby="technisch-titel" className="panel">
                  <div className="panel-header">
                    <h2 id="technisch-titel">Technische context</h2>
                    <button type="button" onClick={adminData.reload}>Ververs gegevens</button>
                  </div>
                  <p className="muted">
                    Deze UI draait met Authorization Code + PKCE en leest via dezelfde browser-sessie uit de lokale compose-stack.
                  </p>
                  <table>
                    <caption className="sr-only">Technische context van de sessie</caption>
                    <thead>
                      <tr>
                        <th scope="col">Eigenschap</th>
                        <th scope="col">Waarde</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>Gebruiker-ID</td>
                        <td>{activeSession.userId ?? '-'}</td>
                      </tr>
                      <tr>
                        <td>Bestuur-ID</td>
                        <td>{activeSession.boardId ?? '-'}</td>
                      </tr>
                      <tr>
                        <td>School-ID</td>
                        <td>{activeSession.schoolId ?? '-'}</td>
                      </tr>
                      <tr>
                        <td>Onderwerpen</td>
                        <td>{formatTopicSummary(adminData.snapshot)}</td>
                      </tr>
                    </tbody>
                  </table>
                </section>

                <Suspense fallback={<section className="panel"><h2>Editor laden</h2><p>TipTap editor wordt geladen.</p></section>}>
                  <TiptapDocumentEditor accessToken={auth.accessToken!} capabilities={activeSession.capabilities} />
                </Suspense>
              </>
            )}
          </>
        )}
      </main>
    </div>
  );
}

function CapabilityList({ capabilityCodes, capabilities }: { capabilityCodes: string[]; capabilities: Capability[] }) {
  const capabilityNamesByCode = new Map(capabilities.map((capability) => [capability.code, capability.displayNameNl]));
  return (
    <ul>
      {capabilityCodes.map((capabilityCode) => (
        <li key={capabilityCode}>{capabilityNamesByCode.get(capabilityCode) ?? capabilityCode}</li>
      ))}
    </ul>
  );
}

function formatTopicSummary(snapshot: AdminSnapshot) {
  const topicCountByDomain = new Map<number, number>();
  snapshot.inspectionTopics.forEach((topic) => {
    topicCountByDomain.set(topic.domainId, (topicCountByDomain.get(topic.domainId) ?? 0) + 1);
  });
  return snapshot.inspectionDomains
    .map((domain) => `${domain.displayNameNl}: ${topicCountByDomain.get(domain.id) ?? 0}`)
    .join(', ');
}

export default App;
