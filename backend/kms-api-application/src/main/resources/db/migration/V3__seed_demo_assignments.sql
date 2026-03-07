insert into user_assignments (id, user_id, role_code, scope_type, board_id, school_id, permission_level, active, valid_from, valid_to) values
  (3, 12, 'BESTUURDER', 'BOARD', 12, null, 'READ', true, TIMESTAMP WITH TIME ZONE '2026-03-05 00:00:00+00', null),
  (4, 13, 'BESTUURS_KC', 'BOARD', 12, null, 'WRITE', true, TIMESTAMP WITH TIME ZONE '2026-03-05 00:00:00+00', null),
  (5, 43, 'TEAMLID', 'SCHOOL', 12, 42, 'READ', true, TIMESTAMP WITH TIME ZONE '2026-03-05 00:00:00+00', null),
  (6, 84, 'ONDERWIJSADVISEUR', 'EXTERN', null, 42, 'WRITE', true, TIMESTAMP WITH TIME ZONE '2026-03-05 00:00:00+00', null);

alter table user_assignments alter column id restart with 100;
