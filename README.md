⸻

KMS – Rollen, Document Lifecycle & Scoring Model (MVP)

1. Doel van dit document

Dit document beschrijft het functionele en technische model voor:
	•	Rollen en permissies binnen het KMS
	•	Document lifecycle statussen
	•	Het scoringmodel (stoplichten)
	•	Gewichten per scorebron
	•	De “laatste setter wint” logica

Dit betreft de MVP-opzet en is bewust eenvoudig gehouden.

⸻

2. Rollen en Permissies

2.1 school_admin (schooldirecteur)

Permissies:
	•	Documenten maken
	•	Documenten wijzigen
	•	Documenten verwijderen
	•	Documentstatus wijzigen
	•	SELF score zetten

⸻

2.2 quality_manager_school (interne kwaliteitsmanager)

Permissies:
	•	Documenten maken
	•	Documenten wijzigen
	•	Documenten verwijderen
	•	Documentstatus wijzigen
	•	SELF score zetten

⸻

2.3 quality_manager_cluster (cluster kwaliteitsmanager)

Permissies:
	•	Documenten maken
	•	Documenten wijzigen
	•	Documenten verwijderen
	•	Documentstatus wijzigen
	•	CLUSTER score zetten

⸻

2.4 board_member (bestuurder)

Permissies:
	•	Documenten maken
	•	Documenten wijzigen
	•	Documenten verwijderen
	•	Documentstatus wijzigen
	•	CLUSTER score zetten

⸻

2.5 external_advisor (externe adviseur)

Permissies:
	•	Alle documenten inzien (read-only)
	•	EXTERNAL score zetten

Niet toegestaan:
	•	Documenten maken
	•	Documenten wijzigen
	•	Documenten verwijderen
	•	Documentstatus wijzigen

⸻

3. Document Lifecycle

Documenten doorlopen de volgende statussen:

Technische waarde	Label (UI)
CONCEPT	In opmaak
REVIEW	In behandeling
ACTIVE	Vastgesteld
EXPIRED	Verlopen

Lifecycle intentie
	•	CONCEPT → document wordt opgesteld of aangepast
	•	REVIEW → document is in beoordeling
	•	ACTIVE → document is vastgesteld en geldig
	•	EXPIRED → document is verlopen of vervangen

⸻

4. Scoring Model (Stoplichten)

4.1 Overzicht

Per documentversie bestaan maximaal 3 stoplichten:

Stoplicht	score_source	Gewicht
Intern	SELF	1
Cluster	CLUSTER	2
Extern	EXTERNAL	3

Elke score bevat:
	•	document_id
	•	source
	•	value_enum
	•	weight
	•	value_numeric

⸻

4.2 Scorewaarden (stoplicht)

Enum: score_value_enum
	•	ZWAK
	•	VOLDOENDE
	•	GOED
	•	NULL = geen beoordeling (stoplicht uit)

⸻

4.3 Wie mag welke score zetten?

SELF (weight = 1)

Mag gezet worden door:
	•	school_admin
	•	quality_manager_school

Gedrag:

Laatste setter wint

⸻

CLUSTER (weight = 2)

Mag gezet worden door:
	•	board_member
	•	quality_manager_cluster

Gedrag:

Laatste setter wint

⸻

EXTERNAL (weight = 3)

Mag gezet worden door:
	•	external_advisor

Gedrag:

Enige setter

⸻

5. “Laatste setter wint” principe

Per documentversie is er:

maximaal 1 score per (document_id, source)

Database constraint:

UNIQUE(document_id, source)

Als een bevoegde gebruiker opnieuw scoort:
	•	De bestaande score wordt geüpdatet
	•	Er wordt geen nieuwe rij aangemaakt
	•	De laatst opgeslagen waarde is leidend

Er wordt in de MVP geen auditgeschiedenis bijgehouden.

⸻

6. Gewogen Score Logica

Gewicht wordt automatisch bepaald door score_source:

source	weight
SELF	1
CLUSTER	2
EXTERNAL	3

Voor berekeningen kan een gewogen score worden bepaald via:

weighted_score = value_numeric × weight

Waarbij:

value_enum	value_numeric
ZWAK	1
VOLDOENDE	2
GOED	3


⸻

7. Database Structuur (MVP)

Tabel: scores

Belangrijke velden:
	•	id
	•	document_id
	•	source
	•	value_enum
	•	value_numeric
	•	weight
	•	created_at
	•	created_by

Unieke constraint:

UNIQUE(document_id, source)


⸻

8. Architectuurkeuzes (bewust)

Waarom 3 stoplichten?
	•	Duidelijke governance-lagen
	•	Intern / cluster / extern onderscheid
	•	Eenvoudig dashboarden

Waarom “laatste setter wint”?
	•	Minimale complexiteit
	•	Geen auditmodel nodig in MVP
	•	Sluit aan bij praktische governance

Waarom gewichten?
	•	Externe beoordeling weegt zwaarder
	•	Clusterbeoordeling middelzwaar
	•	Interne beoordeling basis

⸻

9. Toekomstige uitbreidingen (optioneel)

Mogelijke toekomstige uitbreidingen:
	•	Audittrail per score
	•	Meerdere actieve beoordelaars per source
	•	Fijnmazige rol-permissies
	•	Score-reset bij nieuwe documentversie
	•	Automatische statusovergang bij bepaalde scores
	•	Dashboard met aggregaties per documenttype

⸻

10. Samenvatting

De MVP van het KMS bevat:
	•	Duidelijke rolstructuur
	•	Heldere document lifecycle
	•	3-stoplichtenmodel
	•	Gewogen scoring
	•	Eenvoudige en stabiele database-opzet
	•	“Laatste setter wint” mechanisme

Dit model is eenvoudig, robuust en uitbreidbaar.

⸻

Als je wilt, kan ik ook nog een tweede Markdown-bestand maken met alleen de technische implementatie (SQL + RLS + triggers) als aparte documentatie voor developers.
