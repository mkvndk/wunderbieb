# 🧭 KMS – Rollen, Document Lifecycle & Fasegebaseerd Kwaliteitsmodel

## 1. Organisatiestructuur

Het KMS kent twee organisatielagen:

- **Bestuur**
- **Scholen**

Gebruikers behoren tot één van deze lagen en hebben rechten afhankelijk van hun rol.

---

# 2. Rollen & Rechten

## 2.1 Schoolniveau

### 🏫 Directie
- Documenten aanmaken
- Documenten bewerken
- Documenten verwijderen
- Documentstatus wijzigen
- Documenten goedkeuren op schoolniveau
- Documenten inzien
- Toegang tot dashboard

### 🧩 Kwaliteitscoördinator (school)
- Documenten aanmaken
- Documenten bewerken
- Documenten goedkeuren op schoolniveau
- Documenten inzien
- Toegang tot dashboard

### 👩‍🏫 Teamleden
- Documenten inzien
- Toegang tot dashboard
- Geen beoordelingsrechten

### 👥 MR-leden
- Documenten inzien
- Toegang tot dashboard
- Optioneel beoordelingsrecht (nader te bepalen)

---

## 2.2 Bestuursniveau

### 🏢 Kwaliteitscoördinator (bestuur)
- Documenten inzien
- Documenten beoordelen op bestuursniveau
- Toegang tot dashboard

### 👔 Bestuurders
- Documenten inzien
- Toegang tot dashboard
- Geen operationele beoordelingsrol

---

# 3. Document Lifecycle

Documenten kennen een lifecycle-status:

- `CONCEPT` – In opmaak  
- `REVIEW` – In behandeling  
- `ACTIVE` – Vastgesteld  
- `EXPIRED` – Verlopen  

Deze status bepaalt de formele fase van het document binnen de organisatie.

---

# 4. Fasegebaseerd Kwaliteitsmodel

Het kwaliteitsmodel bestaat uit **5 vaste fases** per document.  
Elke fase wordt visueel weergegeven als een blokje in een vaste volgorde.

## Fases

1. **Status**  
   (Is het document aangemaakt en wat is de huidige documentstatus?)

2. **Schoolcontrole**  
   Beoordeling door kwaliteitscoördinator of directie van de school.

3. **Bestuurscontrole**  
   Beoordeling door kwaliteitscoördinator van het bestuur.

4. **Interne controle**  
   Interne audit of aanvullende interne kwaliteitscontrole.

5. **Externe controle**  
   Beoordeling door externe adviseur of auditor.

---

# 5. Beoordelingsopties per Controlefase

Voor elke controlefase (fase 2 t/m 5) zijn vier mogelijke statussen:

- ✅ **Goedkeuren**
- 🟡 **Goedkeuren met verbeterpunten**
- ❌ **Afkeuren met aantekeningen**
- ⚪ **Nog niet gecontroleerd**

Elke beoordeling:
- Kan een toelichting bevatten
- Overschrijft een eerdere beoordeling (laatste setter wint)
- Wordt opgeslagen per fase

---

# 6. Puntensysteem per Fase

## Fase 1 – Status (Document aangemaakt)

| Status | Punten |
|--------|--------|
| Document aangemaakt | +3 |
| Niet aangemaakt | 0 |

---

## Fase 2 – Schoolcontrole

| Beoordeling | Punten |
|-------------|--------|
| Voldoende | +3 |
| Voldoende met verbeterpunten | +2 |
| Onvoldoende | +1 |
| Nog niet gecontroleerd | 0 |

---

## Fase 3 – Bestuurscontrole

| Beoordeling | Punten |
|-------------|--------|
| Voldoende | +2 |
| Voldoende met verbeterpunten | +1 |
| Onvoldoende | -2 |
| Nog niet gecontroleerd | 0 |

---

## Fase 4 – Interne controle

| Beoordeling | Punten |
|-------------|--------|
| Voldoende | +2 |
| Voldoende met verbeterpunten | +1 |
| Onvoldoende | -2 |
| Nog niet gecontroleerd | 0 |

---

## Fase 5 – Externe controle

| Beoordeling | Punten |
|-------------|--------|
| Voldoende | +3 |
| Voldoende met verbeterpunten | +2 |
| Onvoldoende | -2 |
| Nog niet gecontroleerd | 0 |

---

# 7. Visuele Representatie

Elk document wordt weergegeven als een vaste reeks van 5 blokjes:

```
[ Status ] [ School ] [ Bestuur ] [ Intern ] [ Extern ]
```

De kleur van elk blokje wordt bepaald door de beoordelingsstatus.

Op dashboardniveau kunnen meerdere documenten worden samengevoegd tot een verticale of horizontale kwaliteitsbalk.

---

# 8. Aggregatie & Dashboard

Voor dashboards kunnen:

- Fase-scores per document worden opgeteld
- Scores per school worden geaggregeerd
- Onderliggende documenten worden weergegeven als segmenten in een kwaliteitskaart
- Trends per jaar of cluster worden gevisualiseerd

---

# 9. Ontwerpprincipes

- Fases zijn altijd vast in volgorde
- Beoordelingen zijn overschrijfbaar (laatste setter wint)
- Elke fase is afzonderlijk inzichtelijk
- Het model ondersteunt zowel detailniveau als bestuurlijk overzicht
- Uitbreiding naar extra fases blijft mogelijk
