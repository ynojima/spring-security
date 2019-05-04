INSERT INTO m_group VALUES (1, 'Group A');
INSERT INTO m_group VALUES (2, 'Group B');
INSERT INTO m_group VALUES (3, 'Group C');

INSERT INTO m_authority VALUES (1, 'ROLE_ADMIN');
INSERT INTO m_authority VALUES (2, 'ROLE_ACTUATOR');

INSERT INTO r_group_authority VALUES (1, 1);

COMMIT;
