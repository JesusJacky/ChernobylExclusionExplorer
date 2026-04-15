-- ==========================================================
-- 1. NIVELES DE PELIGRO (No dependen de nadie)
-- ==========================================================
INSERT INTO nivel_peligro (nivel, nivel_roengents, tiempo_maximo_exposicion) 
VALUES ('BAJO', 0.5, 480); -- ID 1

INSERT INTO nivel_peligro (nivel, nivel_roengents, tiempo_maximo_exposicion) 
VALUES ('MEDIO', 2.5, 120); -- ID 2

INSERT INTO nivel_peligro (nivel, nivel_roengents, tiempo_maximo_exposicion) 
VALUES ('ALTO', 5.0, 30); -- ID 3


-- ==========================================================
-- 2. PAQUETES DE VIAJE (Dependen de nivel_peligro)
-- ==========================================================
INSERT INTO paquete_viaje (nombre, descripcion, precio_persona, nivelpeligro_id, lugares_incluidos)
VALUES ('Tour Zona Exterior', 'Recorrido por zonas seguras alrededor de la zona de exclusión', 120.0, 1, 'Checkpoint Dytyatky, Monumento a los liquidadores');

INSERT INTO paquete_viaje (nombre, descripcion, precio_persona, nivelpeligro_id, lugares_incluidos)
VALUES ('Exploracion Pripyat', 'Visita guiada a la ciudad abandonada de Pripyat', 250.0, 2, 'Pripyat, Parque de atracciones, Hospital abandonado');

INSERT INTO paquete_viaje (nombre, descripcion, precio_persona, nivelpeligro_id, lugares_incluidos)
VALUES ('Zona Roja Reactor 4', 'Tour extremo cerca del reactor y zonas altamente contaminadas', 500.0, 3, 'Reactor 4, Bosque Rojo, Sarcofago');


-- ==========================================================
-- 3. RESERVAS (Dependen de paquete_viaje)
-- ==========================================================
-- Reserva para el Tour Exterior (ID 1)
-- Reservas variadas para pruebas de filtrado y ordenación
INSERT INTO reserva (fecha_viaje, email_contacto, precio_total, confirmacion_mayor_edad, estado, localizador_cliente, observaciones, telefono, tipo_paquete_id) VALUES
('2026-01-10', 'cliente1@test.com', 150.0, 1, 'CONFIRMADA', 'LOC-001', 'Madrugador', '600000001', 1),
('2026-08-05', 'cliente2@test.com', 300.0, 1, 'PENDIENTE', 'LOC-002', 'Sin observaciones', '600000002', 2),
('2026-03-12', 'cliente3@test.com', 120.0, 1, 'CANCELADA', 'LOC-003', 'Cancelado por clima', '600000003', 1),
('2026-02-20', 'cliente4@test.com', 450.0, 1, 'CONFIRMADA', 'LOC-004', 'VIP', '600000004', 3),
('2026-12-25', 'cliente5@test.com', 200.0, 1, 'PENDIENTE', 'LOC-005', 'Reserva navideña', '600000005', 2),
('2026-05-15', 'cliente6@test.com', 180.0, 1, 'CONFIRMADA', 'LOC-006', NULL, '600000006', 1),
('2026-04-01', 'cliente7@test.com', 550.0, 1, 'CANCELADA', 'LOC-007', 'Error en pago', '600000007', 3),
('2026-07-20', 'cliente8@test.com', 210.0, 1, 'CONFIRMADA', 'LOC-008', 'Grupo grande', '600000008', 2),
('2026-06-30', 'cliente9@test.com', 130.0, 1, 'PENDIENTE', 'LOC-009', 'Pendiente de validar DNI', '600000009', 1),
('2026-01-05', 'cliente10@test.com', 600.0, 1, 'CONFIRMADA', 'LOC-010', 'La más antigua', '600000010', 3),
('2026-11-11', 'cliente11@test.com', 250.0, 1, 'CANCELADA', 'LOC-011', 'Causa mayor', '600000011', 2),
('2026-09-09', 'cliente12@test.com', 175.0, 1, 'PENDIENTE', 'LOC-012', NULL, '600000012', 1),
('2026-03-25', 'cliente13@test.com', 400.0, 1, 'CONFIRMADA', 'LOC-013', 'Desea guía en inglés', '600000013', 3),
('2026-10-10', 'cliente14@test.com', 220.0, 1, 'PENDIENTE', 'LOC-014', 'Interesado en fotos', '600000014', 2),
('2026-05-01', 'cliente15@test.com', 190.0, 1, 'CONFIRMADA', 'LOC-015', 'Repetidor', '600000015', 1);


-- ==========================================================
-- 4. CLIENTES / VIAJEROS (Dependen de reserva)
-- ==========================================================
-- Viajeros de la Reserva 1 (ID_RESERVA_CLIENTE = 1)
INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Victor', 'Fernandez', 'Gomez', '12345678A', '1990-05-15', 'Española', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Elena', 'Martinez', NULL, '87654321B', '1992-08-20', 'Española', 1, null);

-- Viajero de la Reserva 2 (ID_RESERVA_CLIENTE = 2)
INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Alex', 'Kravchenko', NULL, '11223344C', '1985-03-10', 'Ucraniana', 1, null);
-- MAYORES DE EDAD (Diferentes épocas)
INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Antonio', 'Serrano', 'Vila', '11111111Q', '1955-11-03', 'Española', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Beatriz', 'Lugo', NULL, '22222222W', '1968-04-25', 'Mexicana', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Igor', 'Volkov', NULL, '33333333E', '1972-09-12', 'Ucraniana', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Carmen', 'Jiménez', 'Ruiz', '44444444R', '1984-01-30', 'Española', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Sven', 'Lund', NULL, '55555555T', '1998-12-05', 'Sueca', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Yuki', 'Sato', NULL, '66666666Y', '2001-07-20', 'Japonesa', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Mateo', 'Ricci', NULL, '77777777U', '2005-03-15', 'Italiana', 1, null);

-- CASOS LÍMITE (Alrededor de los 18 años en 2026)
INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Paula', 'Ortiz', 'Girona', '88888888I', '2008-01-10', 'Española', 1, null); -- 18 años (Debería salir)

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Adrián', 'Méndez', NULL, '99999999O', '2009-06-14', 'Española', 1, null); -- 16/17 años (NO debería salir)

-- MENORES DE EDAD (Niños y adolescentes)
INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Daniel', 'Kuznetsov', NULL, '12312312P', '2010-10-10', 'Ucraniana', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Leo', 'García', 'Sanz', '23423423A', '2012-05-05', 'Española', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Emma', 'Watson', NULL, '34534534S', '2014-08-19', 'Británica', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Hiroki', 'Tanaka', NULL, '45645645D', '2016-02-28', 'Japonesa', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Sara', 'Müller', NULL, '56756756F', '2018-11-11', 'Alemana', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Noah', 'Dubois', NULL, '67867867G', '2021-03-01', 'Francesa', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Sofía', 'Pereira', NULL, '78978978H', '2023-12-25', 'Portuguesa', 1, null);

-- MÁS NACIONALIDADES Y EDADES ALEATORIAS
INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Liam', 'O Connor', NULL, '89089089J', '1962-07-07', 'Irlandesa', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Fatima', 'Zahra', NULL, '90190190K', '1995-02-14', 'Marroquí', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Andrés', 'Bello', 'Paz', '01201201L', '1989-10-31', 'Chilena', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Clara', 'Zetkin', NULL, '12341234M', '1950-06-20', 'Alemana', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Biel', 'Vila', 'Junyent', '54325432N', '2007-09-09', 'Española', 1, null); -- 18 años (Debería salir)

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Dimitri', 'Sokolov', NULL, '98769876B', '1980-05-20', 'Ucraniana', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Valentina', 'Gómez', NULL, '65436543V', '2019-04-04', 'Mexicana', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Oliver', 'Smith', NULL, '11112222Z', '1991-01-01', 'Británica', 1, null);

INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente)
VALUES ('Marta', 'Krawczyk', NULL, '33334444X', '2015-12-12', 'Polaca', 1, null);

