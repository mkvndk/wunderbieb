type Role = {
  code: string;
  naam: string;
  scope: string;
  rechten: string[];
};

type Capability = {
  code: string;
  naam: string;
  toelichting: string;
};

type Assignment = {
  gebruiker: string;
  rol: string;
  scope: string;
  context: string;
};

type Score = {
  code: string;
  waarde: number;
  label: string;
  status: string;
};

const rollen: Role[] = [
  { code: 'PLATFORM_ADMIN', naam: 'Super admin', scope: 'PLATFORM', rechten: ['Organisatie beheren', 'Gebruikers beheren', 'Scoreconfiguratie beheren'] },
  { code: 'DIRECTEUR', naam: 'Directeur', scope: 'SCHOOL', rechten: ['Gebruikers beheren', 'Documenten goedkeuren'] },
  { code: 'ONDERWIJSADVISEUR', naam: 'Onderwijsadviseur', scope: 'EXTERN', rechten: ['Externe schoolscope'] }
];

const capabilities: Capability[] = [
  { code: 'MANAGE_USERS', naam: 'Gebruikers beheren', toelichting: 'Beheer van gebruikers en assignments' },
  { code: 'MANAGE_TAXONOMY', naam: 'Taxonomie beheren', toelichting: 'Domeinen, onderwerpen en documenttypen aanpassen' },
  { code: 'MANAGE_SCORE_CONFIGURATION', naam: 'Scoreconfiguratie beheren', toelichting: 'Voorlopige scorewaarden beheren' }
];

const assignments: Assignment[] = [
  { gebruiker: 'super-admin@wunderbieb.nl', rol: 'Super admin', scope: 'PLATFORM', context: 'Alle besturen en scholen' },
  { gebruiker: 'directeur@voorbeeldschool.nl', rol: 'Directeur', scope: 'SCHOOL', context: 'Voorbeeldschool' },
  { gebruiker: 'adviseur@partner.nl', rol: 'Onderwijsadviseur', scope: 'EXTERN', context: 'Voorbeeldschool' }
];

const scores: Score[] = [
  { code: 'HIGH', waarde: 9, label: 'Sterk op orde', status: 'Actief' },
  { code: 'MEDIUM', waarde: 6, label: 'Basis op orde', status: 'Actief' },
  { code: 'LOW', waarde: 3, label: 'Onvoldoende op orde', status: 'Actief' }
];

function App() {
  return (
    <div className="app-shell">
      <a className="skip-link" href="#hoofdinhoud">Direct naar inhoud</a>
      <header className="page-header">
        <p className="eyebrow">Wunderbieb</p>
        <h1>Super-admin beheer</h1>
        <p className="intro">
          Deze shell laat de eerste beheeronderdelen zien voor rollen, capabilities, assignments en scoreconfiguratie.
        </p>
      </header>

      <main id="hoofdinhoud" className="page-main">
        <section aria-labelledby="overzicht-titel" className="hero-card">
          <div>
            <h2 id="overzicht-titel">Beheerstatus</h2>
            <p>
              De huidige setup gebruikt lokale mockdata en is bedoeld als toegankelijke Nederlandstalige beheerbasis.
            </p>
          </div>
          <dl className="stats-grid">
            <div>
              <dt>Rollen</dt>
              <dd>{rollen.length}</dd>
            </div>
            <div>
              <dt>Capabilities</dt>
              <dd>{capabilities.length}</dd>
            </div>
            <div>
              <dt>Assignments</dt>
              <dd>{assignments.length}</dd>
            </div>
            <div>
              <dt>Scorewaarden</dt>
              <dd>{scores.length}</dd>
            </div>
          </dl>
        </section>

        <section aria-labelledby="rollen-titel" className="panel">
          <div className="panel-header">
            <h2 id="rollen-titel">Rollen</h2>
            <button type="button">Nieuwe rol toevoegen</button>
          </div>
          <ul className="card-grid">
            {rollen.map((rol) => (
              <li key={rol.code} className="card">
                <h3>{rol.naam}</h3>
                <p className="muted">{rol.code} · {rol.scope}</p>
                <ul>
                  {rol.rechten.map((recht) => <li key={recht}>{recht}</li>)}
                </ul>
              </li>
            ))}
          </ul>
        </section>

        <section aria-labelledby="capabilities-titel" className="panel">
          <div className="panel-header">
            <h2 id="capabilities-titel">Capabilities</h2>
            <button type="button">Capability beheren</button>
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
              {capabilities.map((capability) => (
                <tr key={capability.code}>
                  <td>{capability.code}</td>
                  <td>{capability.naam}</td>
                  <td>{capability.toelichting}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        <section aria-labelledby="assignments-titel" className="panel">
          <div className="panel-header">
            <h2 id="assignments-titel">Assignments</h2>
            <button type="button">Assignment toevoegen</button>
          </div>
          <table>
            <caption className="sr-only">Overzicht van gebruikersassignments</caption>
            <thead>
              <tr>
                <th scope="col">Gebruiker</th>
                <th scope="col">Rol</th>
                <th scope="col">Scope</th>
                <th scope="col">Context</th>
              </tr>
            </thead>
            <tbody>
              {assignments.map((assignment) => (
                <tr key={`${assignment.gebruiker}-${assignment.rol}`}>
                  <td>{assignment.gebruiker}</td>
                  <td>{assignment.rol}</td>
                  <td>{assignment.scope}</td>
                  <td>{assignment.context}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        <section aria-labelledby="score-titel" className="panel">
          <div className="panel-header">
            <h2 id="score-titel">Scoreconfiguratie</h2>
            <button type="button">Scorewaarde aanpassen</button>
          </div>
          <ul className="score-list">
            {scores.map((score) => (
              <li key={score.code} className="score-row">
                <div>
                  <h3>{score.label}</h3>
                  <p className="muted">{score.code}</p>
                </div>
                <div className="score-value">{score.waarde}</div>
                <span className="status-chip">{score.status}</span>
              </li>
            ))}
          </ul>
        </section>
      </main>
    </div>
  );
}

export default App;
