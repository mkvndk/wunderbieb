export type DocumentTypeProfile = {
  code: string;
  title: string;
  summary: string;
  placeholder: string;
  templateHtml: string;
};

export const DOCUMENT_TYPE_PROFILES: DocumentTypeProfile[] = [
  {
    code: "BELEIDSPLAN",
    title: "Beleidsplan",
    summary: "Strategische keuzes, doelen en onderbouwing.",
    placeholder: "Beschrijf de visie, doelen en uitvoering van het beleidsplan.",
    templateHtml:
      "<h2>Samenvatting</h2><p>Beschrijf kort de kern van dit beleidsplan.</p><h2>Doelen</h2><ul><li>Doel 1</li><li>Doel 2</li></ul><h2>Aanpak</h2><p>Werk hier de aanpak uit.</p>"
  },
  {
    code: "KWALITEITSRAPPORTAGE",
    title: "Kwaliteitsrapportage",
    summary: "Analyse van resultaten, risico's en verbeterpunten.",
    placeholder: "Leg bevindingen en meetresultaten vast.",
    templateHtml:
      "<h2>Observaties</h2><p>Vat de belangrijkste observaties samen.</p><h2>Risico's</h2><ul><li>Risico 1</li><li>Risico 2</li></ul><h2>Acties</h2><p>Welke acties volgen hieruit?</p>"
  },
  {
    code: "ONDERBOUWINGSDOSSIER",
    title: "Onderbouwingsdossier",
    summary: "Bronnen, argumentatie en bewijsstukken in een redacteerbaar dossier.",
    placeholder: "Leg argumentatie en bewijs gestructureerd vast.",
    templateHtml:
      "<h2>Context</h2><p>Omschrijf de context van dit dossier.</p><h2>Bewijs</h2><ol><li>Bron 1</li><li>Bron 2</li></ol><h2>Conclusie</h2><p>Formuleer de conclusie op basis van de bronnen.</p>"
  }
];
