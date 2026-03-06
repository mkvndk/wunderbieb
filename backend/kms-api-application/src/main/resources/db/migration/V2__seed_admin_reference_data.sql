insert into capabilities (id, code, display_name_nl, description_nl, active) values
  (1, 'MANAGE_USERS', 'Gebruikers beheren', 'Mag gebruikers en rollen beheren', true),
  (2, 'MANAGE_ORG', 'Organisatie beheren', 'Mag besturen en scholen beheren', true),
  (3, 'MANAGE_TAXONOMY', 'Taxonomie beheren', 'Mag domeinen, onderwerpen en documenttypen beheren', true),
  (4, 'APPROVE_DOCUMENT', 'Document goedkeuren', 'Mag documentstatus goedkeuren', true),
  (5, 'EXPORT_DATA', 'Data exporteren', 'Mag exports starten', true),
  (6, 'MANAGE_SCORE_CONFIGURATION', 'Scoreconfiguratie beheren', 'Mag scores en regels aanpassen', true);

insert into roles (id, code, display_name_nl, description_nl, scope_type, permission_level, system_role, active) values
  (1, 'PLATFORM_ADMIN', 'Super admin', 'Platformbeheerder met volledige beheerrechten', 'PLATFORM', 'WRITE', true, true),
  (2, 'BESTUURDER', 'Bestuurder', 'Lezen op bestuursniveau', 'BOARD', 'READ', true, true),
  (3, 'BESTUURS_KC', 'Bestuurs kwaliteitscoordinator', 'Schrijven op bestuursniveau', 'BOARD', 'WRITE', true, true),
  (4, 'DIRECTEUR', 'Directeur', 'Schrijven op schoolniveau', 'SCHOOL', 'WRITE', true, true),
  (5, 'KWALITEITSCOORDINATOR', 'Kwaliteitscoordinator', 'Schrijven op schoolniveau', 'SCHOOL', 'WRITE', true, true),
  (6, 'TEAMLID', 'Teamlid', 'Lezen op schoolniveau', 'SCHOOL', 'READ', true, true),
  (7, 'MR_LID', 'MR-lid', 'Lezen op schoolniveau', 'SCHOOL', 'READ', true, true),
  (8, 'ONDERWIJSADVISEUR', 'Onderwijsadviseur', 'Externe adviseur met eigen scope', 'EXTERN', 'WRITE', true, true);

insert into role_capability_codes (role_id, capability_code) values
  (1, 'MANAGE_USERS'),
  (1, 'MANAGE_ORG'),
  (1, 'MANAGE_TAXONOMY'),
  (1, 'MANAGE_SCORE_CONFIGURATION'),
  (1, 'EXPORT_DATA'),
  (3, 'APPROVE_DOCUMENT'),
  (4, 'MANAGE_USERS'),
  (4, 'APPROVE_DOCUMENT');

insert into user_assignments (id, user_id, role_code, scope_type, board_id, school_id, permission_level, active, valid_from, valid_to) values
  (1, 1, 'PLATFORM_ADMIN', 'PLATFORM', null, null, 'WRITE', true, TIMESTAMP WITH TIME ZONE '2026-03-05 00:00:00+00', null),
  (2, 42, 'DIRECTEUR', 'SCHOOL', 12, 42, 'WRITE', true, TIMESTAMP WITH TIME ZONE '2026-03-05 00:00:00+00', null);

insert into score_configurations (id, code, numeric_value, display_label_nl, description_nl, sort_order, active) values
  (1, 'HIGH', 9, 'Sterk op orde', 'Voorlopige hoge score', 1, true),
  (2, 'MEDIUM', 6, 'Basis op orde', 'Voorlopige middenscore', 2, true),
  (3, 'LOW', 3, 'Onvoldoende op orde', 'Voorlopige lage score', 3, true);

insert into inspection_domains (id, code, display_name_nl, description_nl, sort_order, active) values
  (1, 'ONZE_SCHOOL', 'Onze school', 'Identiteit en cultuur', 1, true),
  (2, 'OP', 'Onderwijsproces', 'Onderwijsproces', 2, true),
  (3, 'VS', 'Veiligheid en schoolklimaat', 'Veiligheid en schoolklimaat', 3, true),
  (4, 'OR', 'Onderwijsresultaten', 'Onderwijsresultaten', 4, true),
  (5, 'SKA', 'Sturen, kwaliteitszorg en ambitie', 'Sturen, kwaliteitszorg en ambitie', 5, true);

insert into inspection_topics (id, domain_id, code, display_name_nl, description_nl, sort_order, active) values
  (1, 5, 'SKA1', 'Visie, ambities en doelen', 'Strategische koers en doelstelling', 1, true),
  (2, 5, 'SKA2', 'Uitvoering en kwaliteitscultuur', 'Dagelijkse uitvoering en cultuur', 2, true),
  (3, 2, 'OP1', 'Aanbod', 'Onderwijsaanbod en dekking', 1, true),
  (4, 2, 'OP2', 'Zicht op ontwikkeling', 'Signalering en begeleiding', 2, true);

insert into document_type_definitions (id, code, display_name_nl, description_nl, active, required_for_onboarding) values
  (1, 'VISION_DOCUMENT', 'Visiedocument', 'Documenttype voor visie en koers', true, true),
  (2, 'QUALITY_CARD', 'Kwaliteitskaart', 'Documenttype voor uitvoeringskwaliteit', true, true),
  (3, 'AMBITION_DOCUMENT', 'Ambitiedocument', 'Documenttype voor ambities en doelen', true, false);

alter table capabilities alter column id restart with 100;
alter table roles alter column id restart with 100;
alter table user_assignments alter column id restart with 100;
alter table score_configurations alter column id restart with 100;
alter table inspection_domains alter column id restart with 100;
alter table inspection_topics alter column id restart with 100;
alter table document_type_definitions alter column id restart with 100;
alter table audit_events alter column id restart with 100;
