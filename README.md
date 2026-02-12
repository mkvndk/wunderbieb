# KMS – Rollen, Document Lifecycle & Scoring Model (MVP)

Dit document beschrijft de functionele en technische uitgangspunten van het KMS (Kwaliteitsmanagementsysteem) voor:

- Rollen en permissies
- Document lifecycle
- Stoplicht (scoring) model
- Gewichten en berekeningslogica
- Database-implicaties (MVP)

Deze opzet is bewust eenvoudig gehouden en gebaseerd op het principe:

> **“Laatste setter wint”**

---

# 1. Rollen & Permissies

## 1.1 school_admin (Schooldirecteur)

**Mag:**

- Documenten maken
- Documenten wijzigen
- Documenten verwijderen
- Documentstatus wijzigen
- SELF-score zetten

---

## 1.2 quality_manager_school (Interne kwaliteitsmanager)

**Mag:**

- Documenten maken
- Documenten wijzigen
- Documenten verwijderen
- Documentstatus wijzigen
- SELF-score zetten

---

## 1.3 quality_manager_cluster (Cluster kwaliteitsmanager)

**Mag:**

- Documenten maken
- Documenten wijzigen
- Documenten verwijderen
- Documentstatus wijzigen
- CLUSTER-score zetten

---

## 1.4 board_member (Bestuurder)

**Mag:**

- Documenten maken
- Documenten wijzigen
- Documenten verwijderen
- Documentstatus wijzigen
- CLUSTER-score zetten

---

## 1.5 external_advisor (Externe adviseur)

**Mag:**

- Alle documenten inzien (read-only)
- EXTERNAL-score zetten

**Mag niet:**

- Documenten maken
- Documenten wijzigen
- Documenten verwijderen
- Documentstatus wijzigen

---

# 2. Document Lifecycle

Documenten kennen de volgende statussen:

| Technische waarde | UI-label |
|------------------|----------|
| `CONCEPT`        | In opmaak |
| `REVIEW`         | In behandeling |
| `ACTIVE`         | Vastgesteld |
| `EXPIRED`        | Verlopen |

### Betekenis

- **CONCEPT** → Document wordt opgesteld of aangepast  
- **REVIEW** → Document is in beoordeling  
- **ACTIVE** → Document is vastgesteld en geldig  
- **EXPIRED** → Document is verlopen of vervangen  

---

# 3. Scoring Model (Stoplichten)

Per documentversie bestaan maximaal **3 stoplichten**.

## 3.1 Stoplichten en gewichten

| Stoplicht | score_source | Gewicht |
|------------|--------------|----------|
| Intern     | `SELF`       | 1 |
| Cluster    | `CLUSTER`    | 2 |
| Extern     | `EXTERNAL`   | 3 |

---

## 3.2 Scorewaarden (Enum)

`score_value_enum`

- `ZWAK`
- `VOLDOENDE`
- `GOED`
- `NULL` → geen beoordeling (stoplicht uit)

---

## 3.3 Wie mag welke score zetten?

### SELF (weight = 1)

Mag gezet worden door:

- school_admin  
- quality_manager_school  

Gedrag:
> Laatste setter wint

---

### CLUSTER (weight = 2)

Mag gezet worden door:

- board_member  
- quality_manager_cluster  

Gedrag:
> Laatste setter wint

---

### EXTERNAL (weight = 3)

Mag gezet worden door:

- external_advisor  

Gedrag:
> Enige setter

---

# 4. “Laatste Setter Wint” Principe

Per documentversie geldt:

