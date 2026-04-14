-- import.sql: initial data for SmartQuark
-- Inserts 20 persons and ~28 phone numbers. Persons are inserted without explicit IDs
-- and phones refer to persons via a subselect, so DB sequences remain consistent.

-- NOTE: explicit numeric IDs are used below. Sequence creation/alteration removed per request.

-- Persons
INSERT INTO person (id, name, surname, age) VALUES (1, 'Alessio', 'Saltarin', 50);
INSERT INTO person (id, name, surname, age) VALUES (2, 'Renzo', 'Piano', 99);
INSERT INTO person (id, name, surname, age) VALUES (3, 'Elena', 'Zambrelli', 25);
INSERT INTO person (id, name, surname, age) VALUES (4, 'Marco', 'Rossi', 34);
INSERT INTO person (id, name, surname, age) VALUES (5, 'Luca', 'Bianchi', 28);
INSERT INTO person (id, name, surname, age) VALUES (6, 'Giulia', 'Verdi', 31);
INSERT INTO person (id, name, surname, age) VALUES (7, 'Francesca', 'Neri', 45);
INSERT INTO person (id, name, surname, age) VALUES (8, 'Paolo', 'Gallo', 52);
INSERT INTO person (id, name, surname, age) VALUES (9, 'Sara', 'Ferrari', 22);
INSERT INTO person (id, name, surname, age) VALUES (10, 'Davide', 'Moretti', 40);
INSERT INTO person (id, name, surname, age) VALUES (11, 'Anna', 'Costa', 37);
INSERT INTO person (id, name, surname, age) VALUES (12, 'Matteo', 'Giordano', 29);
INSERT INTO person (id, name, surname, age) VALUES (13, 'Laura', 'Rinaldi', 26);
INSERT INTO person (id, name, surname, age) VALUES (14, 'Stefano', 'Romano', 48);
INSERT INTO person (id, name, surname, age) VALUES (15, 'Valentina', 'Lombardi', 33);
INSERT INTO person (id, name, surname, age) VALUES (16, 'Roberto', 'Marini', 55);
INSERT INTO person (id, name, surname, age) VALUES (17, 'Elisa', 'Silvestri', 24);
INSERT INTO person (id, name, surname, age) VALUES (18, 'Giorgio', 'Riva', 60);
INSERT INTO person (id, name, surname, age) VALUES (19, 'Chiara', 'Esposito', 27);
INSERT INTO person (id, name, surname, age) VALUES (20, 'Federico', 'Sarti', 35);

-- Phones: use subselect to associate to the correct person by name/surname
INSERT INTO phone (id, prefix, number, person_id) VALUES (1, '348', '39290022', 1);
INSERT INTO phone (id, prefix, number, person_id) VALUES (2, '333', '32233232', 1);
INSERT INTO phone (id, prefix, number, person_id) VALUES (3, '348', '12809128', 2);
INSERT INTO phone (id, prefix, number, person_id) VALUES (4, '349', '23223323', 3);
INSERT INTO phone (id, prefix, number, person_id) VALUES (5, '334', '32332232', 3);

INSERT INTO phone (id, prefix, number, person_id) VALUES (6, '347', '11111111', 4);
INSERT INTO phone (id, prefix, number, person_id) VALUES (7, '346', '22222222', 5);
INSERT INTO phone (id, prefix, number, person_id) VALUES (8, '345', '33333333', 6);
INSERT INTO phone (id, prefix, number, person_id) VALUES (9, '339', '44444444', 7);
INSERT INTO phone (id, prefix, number, person_id) VALUES (10, '338', '55555555', 8);
INSERT INTO phone (id, prefix, number, person_id) VALUES (11, '337', '66666666', 9);
INSERT INTO phone (id, prefix, number, person_id) VALUES (12, '336', '77777777', 10);

INSERT INTO phone (id, prefix, number, person_id) VALUES (13, '335', '88888888', 11);
INSERT INTO phone (id, prefix, number, person_id) VALUES (14, '334', '99999999', 12);
INSERT INTO phone (id, prefix, number, person_id) VALUES (15, '333', '10101010', 13);
INSERT INTO phone (id, prefix, number, person_id) VALUES (16, '332', '20202020', 14);
INSERT INTO phone (id, prefix, number, person_id) VALUES (17, '331', '30303030', 15);
INSERT INTO phone (id, prefix, number, person_id) VALUES (18, '330', '40404040', 16);
INSERT INTO phone (id, prefix, number, person_id) VALUES (19, '329', '50505050', 17);
INSERT INTO phone (id, prefix, number, person_id) VALUES (20, '328', '60606060', 18);
INSERT INTO phone (id, prefix, number, person_id) VALUES (21, '327', '70707070', 19);
INSERT INTO phone (id, prefix, number, person_id) VALUES (22, '326', '80808080', 20);

-- Add some additional numbers
INSERT INTO phone (id, prefix, number, person_id) VALUES (23, '348', '12121212', 4);
INSERT INTO phone (id, prefix, number, person_id) VALUES (24, '349', '13131313', 5);
INSERT INTO phone (id, prefix, number, person_id) VALUES (25, '333', '14141414', 6);
INSERT INTO phone (id, prefix, number, person_id) VALUES (26, '334', '15151515', 7);
INSERT INTO phone (id, prefix, number, person_id) VALUES (27, '335', '16161616', 8);
INSERT INTO phone (id, prefix, number, person_id) VALUES (28, '336', '17171717', 9);

COMMIT;

-- After inserting explicit IDs, we need to reset the sequences to avoid conflicts with future inserts
SELECT setval(pg_get_serial_sequence('person','id'),
              COALESCE((SELECT MAX(id) FROM person), 0));
SELECT setval(pg_get_serial_sequence('phone','id'),
              COALESCE((SELECT MAX(id) FROM phone), 0));