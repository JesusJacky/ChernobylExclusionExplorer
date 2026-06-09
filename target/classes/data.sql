-- ==========================================================
-- 1. NIVELES DE PELIGRO Y LIMITES DOSIMÉTRICOS
-- ==========================================================
INSERT INTO nivel_peligro (nivel, nivel_msv, tiempo_maximo_exposicion) VALUES ('BAJO', 0.15, 720); 
INSERT INTO nivel_peligro (nivel, nivel_msv, tiempo_maximo_exposicion) VALUES ('MEDIO', 1.20, 240); 
INSERT INTO nivel_peligro (nivel, nivel_msv, tiempo_maximo_exposicion) VALUES ('ALTO', 4.50, 45);  

-- ==========================================================
-- 2. PAQUETES DE VIAJE (Tarifas Planas por Grupo hasta 10 Pax)
-- ==========================================================
-- NIVEL BAJO (1-3)
INSERT INTO paquete_viaje (nombre, descripcion, precio_paquete, nivelpeligro_id, lugares_incluidos, dosis_estimada_msv) VALUES 
('Reconocimiento Perimetral', 'Expedición intensiva de 1 jornada completa (10 horas) diseñada para grupos que buscan una inmersión documental sin exponerse a zonas de alta radiación. Con nuestra escolta militar, recorreremos decenas de kilómetros explorando los pueblos abandonados del perímetro, culminando con una visita exhaustiva a la base militar secreta del radar Duga-1.', 4500.0, 1, 'Punto de Control Dityatki, Exploración de Zalissya y Kopachi, Base Militar del Duga-1, Ciudad de Chernóbil, Monumento a los Liquidadores', 0.02),
('La Ruta de la Evacuación', 'Expedición inmersiva de 2 días completos (18 horas de actividad). Recrearemos de forma exacta la ruta logística que siguieron los 1.200 autobuses blindados en mayo de 1986. Nos adentraremos en el corazón de los Koljós (granjas colectivas) colapsados, el puerto fluvial, y pasaremos la noche en la Zona de Exclusión bajo supervisión.', 8000.0, 1, 'Rutas forestales de evacuación, Granjas colectivas (Koljós), Aldea de Opachychi, Puerto fluvial exterior', 0.06),
('Archivo y Memoria', 'Expedición técnica de 3 días (24 horas de actividad) orientada a historiadores. Abordaremos diariamente el tren blindado transfronterizo exclusivo. Accederemos a archivos administrativos gubernamentales, plantas de reparación de maquinaria pesada y complejos cívicos intactos. Incluye 2 noches en el Hotel Estatal de Slavutych.', 11500.0, 1, 'Ciudad de Slavutych, Tren Blindado Transfronterizo, Archivos Estatales de Chernóbil, Planta de reparación ribereña', 0.08);

-- NIVEL MEDIO (4-6)
INSERT INTO paquete_viaje (nombre, descripcion, precio_paquete, nivelpeligro_id, lugares_incluidos, dosis_estimada_msv) VALUES 
('Inmersión Urbana en Prípiat', 'Exploración táctica de 1 día (12 horas ininterrumpidas) centrada en el urbanismo soviético brutalista de Prípiat. Con equipos de protección radiológica activa, nos adentraremos en el interior de decenas de edificios emblemáticos, hospitales, bloques residenciales y centros de mando de la ciudad fantasma.', 6000.0, 2, 'Avenida Lenin, Palacio de Cultura Energetik, Parque de Atracciones, Interiores del Hospital 126, Complejo de Piscinas Azure, Bloques residenciales', 0.25),
('El Silencio del Átomo', 'Misión de 2 días (20 horas de exploración) en el corazón de la desolación. Enfocada en documentar los colosales cementerios de maquinaria radiactiva. Observaremos de cerca helicópteros Mi-8, tanques y camiones ZIL bajo estricta dosimetría. Pernocta en cuarteles de descontaminación de la ciudad de Prípiat.', 10500.0, 2, 'Cementerio de vehículos de Rassokha y Buriakivka, Estación de tren de Yanov, Café Pripyat, Hotel Polissya', 0.45),
('Sombras de la Guerra Fría', 'Expedición clasificada de 3 días (26 horas de campo) que explora los secretos militares del telón de acero ocultos bajo la ciudad. Ingresaremos con equipos autónomos de iluminación a la colosal Fábrica Júpiter, estaciones de policía, celdas de la KGB y a la infinita red de búnkeres tácticos subterráneos soviéticos.', 15000.0, 2, 'Instalaciones secretas Fábrica Júpiter, Subsuelo táctico de Prípiat, Red de búnkeres soviéticos, Jefatura de Policía, Base de bomberos', 0.70);

-- NIVEL ALTO (7-9)
INSERT INTO paquete_viaje (nombre, descripcion, precio_paquete, nivelpeligro_id, lugares_incluidos, dosis_estimada_msv) VALUES 
('La Huella del Liquidador', 'Expedición de riesgo extremo de 2 días (16 horas operativas). Cruce táctico por el interior del Bosque Rojo (el área forestal más radiactiva del planeta) hasta alcanzar el perímetro cero del Reactor 4. Exige excelente forma física para cargar con los equipos de Respiración Autónoma durante horas.', 22000.0, 3, 'Interior del Bosque Rojo, Perímetro externo Reactor 4, Laboratorios de mutación forestal, Puente de la Muerte', 1.80),
('Misión Científica (Sarcófago)', 'Inmersión de 5 días (40 horas operativas) colaborando con equipos de dosimetría oficiales. Ingresaremos a las entrañas del Nuevo Confinamiento Seguro (NSC), explorando el famoso "Corredor Dorado", las inmensas salas de turbinas de los reactores 1 y 2, y las torres de refrigeración inconclusas.', 38000.0, 3, 'Nuevo Confinamiento Seguro (NSC), Corredor Dorado, Salas de turbinas Reactores 1 y 2, Sala de control Reactor 3, Torres de refrigeración', 2.90),
('VIP: El Corazón del Desastre', 'La experiencia máxima gubernamental (7 días / 60 horas operativas). Pase estatal con escolta militar privada. Ingreso a los búnkeres de crisis subterráneos de la central y, mediante el uso de trajes Nivel A, acceso al epicentro de la radiación: la Sala de Control del Reactor 4.', 55000.0, 3, 'Sala de Control Reactor 4 (Botón AZ-5), Tapa del Reactor (Elena), Búnkeres de crisis de la central, Sala de bombas de agua', 4.50);

-- ==========================================================
-- 3. USUARIOS DEL SISTEMA (Contraseña para pruebas: clave123)
-- ==========================================================
INSERT INTO usuario (cuenta, clave, rol, ultima_conexion) VALUES 
-- EMPLEADOS (IDs 1 al 10)
('admin_sbu', 'clave123', 'ROLE_ADMIN', '2026-06-09T08:30:00'),
('j.alvarez', 'clave123', 'ROLE_SUPERVISOR', '2026-06-09T09:15:00'),
('m.perez', 'clave123', 'ROLE_SUPERVISOR', '2026-06-08T18:45:00'),
('o.kovalenko', 'clave123', 'ROLE_SUPERVISOR', '2026-06-09T10:00:00'),
('a.ivanov', 'clave123', 'ROLE_EMPLEADO', '2026-06-09T11:00:00'),
('l.gomez', 'clave123', 'ROLE_EMPLEADO', '2026-06-09T12:00:00'),
('s.romero', 'clave123', 'ROLE_EMPLEADO', '2026-06-09T13:00:00'),
('i.petrov', 'clave123', 'ROLE_EMPLEADO', '2026-06-09T14:00:00'),
('d.shevchenko', 'clave123', 'ROLE_EMPLEADO', '2026-06-09T15:00:00'),
('c.ruiz', 'clave123', 'ROLE_EMPLEADO', '2026-06-09T16:00:00'),

-- CLIENTES DE PRUEBA (IDs 11 al 50)
('victor.f', 'clave123', 'ROLE_CLIENTE', '2026-06-01T10:00:00'),
('elena.m', 'clave123', 'ROLE_CLIENTE', '2026-06-02T11:30:00'),
('alex.k', 'clave123', 'ROLE_CLIENTE', '2026-06-03T09:45:00'),
('antonio.s', 'clave123', 'ROLE_CLIENTE', '2026-06-04T12:00:00'),
('beatriz.l', 'clave123', 'ROLE_CLIENTE', '2026-05-20T16:20:00'),
('igor.v', 'clave123', 'ROLE_CLIENTE', '2026-05-21T14:10:00'),
('carmen.j', 'clave123', 'ROLE_CLIENTE', '2026-05-22T08:05:00'),
('sven.l', 'clave123', 'ROLE_CLIENTE', '2026-06-05T19:30:00'),
('yuki.s', 'clave123', 'ROLE_CLIENTE', '2026-06-06T20:15:00'),
('mateo.r', 'clave123', 'ROLE_CLIENTE', '2026-06-07T21:00:00'),
('paula.o', 'clave123', 'ROLE_CLIENTE', '2026-06-08T10:25:00'),
('adrian.m', 'clave123', 'ROLE_CLIENTE', '2026-06-09T11:40:00'),
('daniel.k', 'clave123', 'ROLE_CLIENTE', '2026-05-10T13:50:00'),
('leo.g', 'clave123', 'ROLE_CLIENTE', '2026-05-11T15:00:00'),
('emma.w', 'clave123', 'ROLE_CLIENTE', '2026-05-12T17:10:00'),
('hiroki.t', 'clave123', 'ROLE_CLIENTE', '2026-05-13T18:20:00'),
('sara.m', 'clave123', 'ROLE_CLIENTE', '2026-05-14T09:30:00'),
('noah.d', 'clave123', 'ROLE_CLIENTE', '2026-05-15T10:40:00'),
('sofia.p', 'clave123', 'ROLE_CLIENTE', '2026-05-16T11:50:00'),
('liam.o', 'clave123', 'ROLE_CLIENTE', '2026-05-17T12:00:00'),
('fatima.z', 'clave123', 'ROLE_CLIENTE', '2026-05-18T14:15:00'),
('andres.b', 'clave123', 'ROLE_CLIENTE', '2026-05-19T15:25:00'),
('clara.z', 'clave123', 'ROLE_CLIENTE', '2026-05-20T16:35:00'),
('biel.v', 'clave123', 'ROLE_CLIENTE', '2026-05-21T17:45:00'),
('dimitri.s', 'clave123', 'ROLE_CLIENTE', '2026-05-22T18:55:00'),
('valentina.g', 'clave123', 'ROLE_CLIENTE', '2026-05-23T08:10:00'),
('oliver.s', 'clave123', 'ROLE_CLIENTE', '2026-05-24T09:20:00'),
('marta.k', 'clave123', 'ROLE_CLIENTE', '2026-05-25T10:30:00'),
('lucas.m', 'clave123', 'ROLE_CLIENTE', '2026-05-26T11:40:00'),
('chloe.b', 'clave123', 'ROLE_CLIENTE', '2026-05-27T12:50:00'),
('nadia.p', 'clave123', 'ROLE_CLIENTE', '2026-06-01T10:00:00'),
('taro.y', 'clave123', 'ROLE_CLIENTE', '2026-06-02T11:00:00'),
('isabella.r', 'clave123', 'ROLE_CLIENTE', '2026-06-03T12:00:00'),
('miguel.t', 'clave123', 'ROLE_CLIENTE', '2026-06-04T13:00:00'),
('chen.w', 'clave123', 'ROLE_CLIENTE', '2026-06-05T14:00:00'),
('amelia.e', 'clave123', 'ROLE_CLIENTE', '2026-06-06T15:00:00'),
('hugo.s', 'clave123', 'ROLE_CLIENTE', '2026-06-07T16:00:00'),
('lars.j', 'clave123', 'ROLE_CLIENTE', '2026-06-08T17:00:00'),
('mia.j', 'clave123', 'ROLE_CLIENTE', '2026-06-09T18:00:00'),
('pablo.n', 'clave123', 'ROLE_CLIENTE', '2026-06-10T19:00:00');

-- ==========================================================
-- 4. RESERVAS (Con precios ajustados a los nuevos paquetes premium)
-- ==========================================================
INSERT INTO reserva (fecha_viaje, email_contacto, precio_total, confirmacion_mayor_edad, estado, localizador_cliente, observaciones, telefono, tipo_paquete_id) VALUES
('2026-07-10', 'proyecto.chernobyl.explorer@gmail.com', 4500.0, 1, 'CONFIRMADA', 'LOC-001', 'Madrugador', '+34600000001', 1),
('2026-08-05', 'proyecto.chernobyl.explorer@gmail.com', 8000.0, 1, 'PENDIENTE', 'LOC-002', 'Sin observaciones', '+34600000002', 2),
('2026-09-12', 'proyecto.chernobyl.explorer@gmail.com', 4500.0, 1, 'CANCELADA', 'LOC-003', 'Cancelado por clima', '+34600000003', 1),
('2026-07-20', 'proyecto.chernobyl.explorer@gmail.com', 22000.0, 1, 'CONFIRMADA', 'LOC-004', 'Cliente solicita revisión ERA', '+34600000004', 7),
('2026-12-25', 'proyecto.chernobyl.explorer@gmail.com', 6000.0, 1, 'PENDIENTE', 'LOC-005', 'Reserva navideña', '+34600000005', 4),
('2026-08-15', 'proyecto.chernobyl.explorer@gmail.com', 10500.0, 1, 'CONFIRMADA', 'LOC-006', NULL, '+34600000006', 5),
('2026-07-01', 'proyecto.chernobyl.explorer@gmail.com', 55000.0, 1, 'CANCELADA', 'LOC-007', 'Permisos de Estado denegados por SBU', '+34600000007', 9),
('2026-07-25', 'proyecto.chernobyl.explorer@gmail.com', 15000.0, 1, 'CONFIRMADA', 'LOC-008', 'Documentalistas', '+34600000008', 6),
('2026-08-30', 'proyecto.chernobyl.explorer@gmail.com', 11500.0, 1, 'PENDIENTE', 'LOC-009', 'Pendiente de validar DNI', '+34600000009', 3),
('2026-09-05', 'proyecto.chernobyl.explorer@gmail.com', 38000.0, 1, 'CONFIRMADA', 'LOC-010', 'Requiere dosimetría avanzada', '+34600000010', 8),
('2026-11-11', 'proyecto.chernobyl.explorer@gmail.com', 6000.0, 1, 'CANCELADA', 'LOC-011', 'Causa mayor militar', '+34600000011', 4),
('2026-09-09', 'proyecto.chernobyl.explorer@gmail.com', 8000.0, 1, 'PENDIENTE', 'LOC-012', NULL, '+34600000012', 2),
('2026-10-25', 'proyecto.chernobyl.explorer@gmail.com', 55000.0, 1, 'CONFIRMADA', 'LOC-013', 'Desea guía oficial en inglés', '+34600000013', 9),
('2026-10-10', 'proyecto.chernobyl.explorer@gmail.com', 10500.0, 1, 'PENDIENTE', 'LOC-014', 'Interesado en sesión fotográfica', '+34600000014', 5),
('2026-08-01', 'proyecto.chernobyl.explorer@gmail.com', 15000.0, 1, 'CONFIRMADA', 'LOC-015', 'Cliente repetidor', '+34600000015', 6);

-- ==========================================================
-- 5. CENSOS / FICHAS PERSONALES (Asociados secuencialmente al id_usuario)
-- ==========================================================
INSERT INTO cliente (nombre, apellido1, apellido2, dni, fecha_nacimiento, nacionalidad, consentimiento, id_reserva_cliente, email, telefono, id_usuario, activo, fecha_alta, fecha_baja) VALUES 
-- FICHAS EMPLEADOS (Mapeados a los IDs 1 al 10)
('Director', 'Sistema', NULL, '00000000A', '1970-01-01', 'Ucraniana', 1, NULL, 'admin@sbu.gov', '000000000', 1, true, '2025-01-01', NULL),
('José', 'Álvarez', 'Martín', '11111111S', '1980-01-01', 'Española', 1, NULL, 'jose@sbu.gov', '600000001', 2, true, '2025-01-01', NULL),
('Mario', 'Pérez', 'García', '22222222E', '1995-05-05', 'Española', 1, NULL, 'mario@sbu.gov', '600000002', 3, true, '2025-01-01', NULL),
('Oksana', 'Kovalenko', NULL, '44444444K', '1982-11-12', 'Ucraniana', 1, NULL, 'oksana@sbu.gov', '+380500000001', 4, true, '2025-01-01', NULL),
('Andriy', 'Ivanov', NULL, '55555555I', '1990-03-24', 'Ucraniana', 1, NULL, 'andriy@sbu.gov', '+380500000002', 5, true, '2025-01-01', NULL),
('Laura', 'Gómez', 'Sánchez', '66666666G', '1988-07-19', 'Española', 1, NULL, 'laura@sbu.gov', '600000006', 6, true, '2025-01-01', NULL),
('Sergio', 'Romero', 'Díaz', '77777777R', '1992-09-30', 'Española', 1, NULL, 'sergio@sbu.gov', '600000007', 7, true, '2025-01-01', NULL),
('Igor', 'Petrov', NULL, '88888888P', '1985-02-14', 'Ucraniana', 1, NULL, 'igor.p@sbu.gov', '+380500000003', 8, true, '2025-01-01', NULL),
('Dmytro', 'Shevchenko', NULL, '99999999S', '1993-06-08', 'Ucraniana', 1, NULL, 'dmytro@sbu.gov', '+380500000004', 9, true, '2025-01-01', NULL),
('Carlos', 'Ruiz', 'Alonso', '10101010C', '1987-12-05', 'Española', 1, NULL, 'carlos@sbu.gov', '600000010', 10, true, '2025-01-01', NULL),

-- FICHAS CLIENTES (Mapeados a los IDs 11 al 50)
('Victor', 'Fernandez', 'Gomez', '12345678A', '1990-05-15', 'Española', 1, NULL, 'victor@mail.com', '+34600111222', 11, true, '2025-12-01', NULL),
('Elena', 'Martinez', NULL, '87654321B', '1992-08-20', 'Española', 1, NULL, 'elena@mail.com', '+34600111223', 12, true, '2025-12-01', NULL),
('Alex', 'Kravchenko', NULL, '11223344C', '1985-03-10', 'Ucraniana', 1, NULL, 'alex@mail.com', '+34600111224', 13, true, '2025-12-01', NULL),
('Antonio', 'Serrano', 'Vila', '11111111Q', '1955-11-03', 'Española', 1, NULL, 'antonio@mail.com', '+34600111225', 14, true, '2025-12-01', NULL),
('Beatriz', 'Lugo', NULL, '22222222W', '1968-04-25', 'Mexicana', 1, NULL, 'beatriz@mail.com', '+34600111226', 15, true, '2025-12-01', NULL),
('Igor', 'Volkov', NULL, '33333333E', '1972-09-12', 'Ucraniana', 1, NULL, 'igor@mail.com', '+34600111227', 16, false, '2025-12-01', '2026-03-15'),
('Carmen', 'Jiménez', 'Ruiz', '44444444R', '1984-01-30', 'Española', 1, NULL, 'carmen@mail.com', '+34600111228', 17, false, '2025-12-01', '2026-04-20'),
('Sven', 'Lund', NULL, '55555555T', '1998-12-05', 'Sueca', 1, NULL, 'sven@mail.com', '+34600111229', 18, false, '2025-12-01', '2026-05-10'),
('Yuki', 'Sato', NULL, '66666666Y', '2010-07-20', 'Japonesa', 1, NULL, 'yuki_padres@mail.com', '+81900111230', 19, true, '2025-12-01', NULL),
('Mateo', 'Ricci', NULL, '77777777U', '2012-03-15', 'Italiana', 0, NULL, 'mateo_tutor@mail.com', '+39600111231', 20, true, '2025-12-01', NULL),
('Paula', 'Ortiz', 'Girona', '88888888I', '2009-01-10', 'Española', 0, NULL, 'paula_madre@mail.com', '+34600111232', 21, true, '2025-12-01', NULL),
('Adrián', 'Méndez', NULL, '99999999O', '2011-06-14', 'Española', 1, NULL, 'adrian_padre@mail.com', '+34600111233', 22, true, '2025-12-01', NULL),
('Daniel', 'Kuznetsov', NULL, '12312312P', '1990-10-10', 'Ucraniana', 1, NULL, 'daniel@mail.com', '+380600111234', 23, true, '2025-12-01', NULL),
('Leo', 'García', 'Sanz', '23423423A', '1992-05-05', 'Argentina', 1, NULL, 'leo@mail.com', '+549110111235', 24, true, '2025-12-01', NULL),
('Emma', 'Watson', NULL, '34534534S', '1994-08-19', 'Británica', 1, NULL, 'emma@mail.com', '+44700111236', 25, true, '2025-12-01', NULL),
('Hiroki', 'Tanaka', NULL, '45645645D', '1996-02-28', 'Japonesa', 1, NULL, 'hiroki@mail.com', '+81900111237', 26, true, '2025-12-01', NULL),
('Sara', 'Müller', NULL, '56756756F', '1998-11-11', 'Alemana', 1, NULL, 'sara@mail.com', '+49150111238', 27, true, '2025-12-01', NULL),
('Noah', 'Dubois', NULL, '67867867G', '1991-03-01', 'Francesa', 1, NULL, 'noah@mail.com', '+33600111239', 28, true, '2025-12-01', NULL),
('Sofía', 'Pereira', NULL, '78978978H', '1993-12-25', 'Portuguesa', 1, NULL, 'sofia@mail.com', '+351900111240', 29, true, '2025-12-01', NULL),
('Liam', 'O Connor', NULL, '89089089J', '1962-07-07', 'Irlandesa', 1, NULL, 'liam@mail.com', '+353800111241', 30, true, '2025-12-01', NULL),
('Fatima', 'Zahra', NULL, '90190190K', '1995-02-14', 'Marroquí', 1, NULL, 'fatima@mail.com', '+212600111242', 31, true, '2025-12-01', NULL),
('Andrés', 'Bello', 'Paz', '01201201L', '1989-10-31', 'Chilena', 1, NULL, 'andres@mail.com', '+56900111243', 32, true, '2025-12-01', NULL),
('Clara', 'Zetkin', NULL, '12341234M', '1950-06-20', 'Alemana', 1, NULL, 'clara@mail.com', '+49150111244', 33, false, '2025-12-01', '2026-01-10'),
('Biel', 'Vila', 'Junyent', '54325432N', '1997-09-09', 'Española', 1, NULL, 'biel@mail.com', '+34600111245', 34, true, '2025-12-01', NULL),
('Dimitri', 'Sokolov', NULL, '98769876B', '1980-05-20', 'Ucraniana', 1, NULL, 'dimitri@mail.com', '+380600111246', 35, false, '2025-12-01', '2026-02-28'),
('Valentina', 'Gómez', NULL, '65436543V', '1999-04-04', 'Mexicana', 1, NULL, 'valentina@mail.com', '+52550111247', 36, true, '2025-12-01', NULL),
('Oliver', 'Smith', NULL, '11112222Z', '1991-01-01', 'Británica', 1, NULL, 'oliver@mail.com', '+44700111248', 37, true, '2025-12-01', NULL),
('Marta', 'Krawczyk', NULL, '33334444X', '1995-12-12', 'Polaca', 1, NULL, 'marta@mail.com', '+48500111249', 38, true, '2025-12-01', NULL),
('Lucas', 'Moreno', 'Díaz', '44445555C', '2015-11-10', 'Española', 0, NULL, 'lucas_padres@mail.com', '+34600111250', 39, true, '2025-12-01', NULL),
('Chloe', 'Bourgeois', NULL, '55556666V', '1997-08-22', 'Francesa', 1, NULL, 'chloe@mail.com', '+33600111251', 40, false, '2025-12-01', '2026-06-01'),
('Nadia', 'Popova', NULL, '12121212N', '1996-04-11', 'Rusa', 1, NULL, 'nadia@mail.com', '+79001112233', 41, true, '2025-12-01', NULL),
('Taro', 'Yamamoto', NULL, '13131313T', '1989-10-22', 'Japonesa', 1, NULL, 'taro@mail.com', '+819011112244', 42, true, '2025-12-01', NULL),
('Isabella', 'Rossi', NULL, '14141414I', '2000-01-15', 'Italiana', 1, NULL, 'isabella@mail.com', '+393001112255', 43, true, '2025-12-01', NULL),
('Miguel', 'Torres', 'Cruz', '15151515M', '1975-08-08', 'Mexicana', 1, NULL, 'miguel@mail.com', '+525511112266', 44, true, '2025-12-01', NULL),
('Chen', 'Wei', NULL, '16161616C', '1994-11-30', 'China', 1, NULL, 'chen@mail.com', '+8613011112277', 45, true, '2025-12-01', NULL),
('Amelia', 'Evans', NULL, '17171717A', '1983-05-18', 'Británica', 1, NULL, 'amelia@mail.com', '+447700112288', 46, true, '2025-12-01', NULL),
('Hugo', 'Silva', 'Gomes', '18181818H', '1991-09-02', 'Brasileña', 1, NULL, 'hugo@mail.com', '+5511900112299', 47, true, '2025-12-01', NULL),
('Lars', 'Jensen', NULL, '19191919L', '1986-12-14', 'Danesa', 1, NULL, 'lars@mail.com', '+4520112233', 48, true, '2025-12-01', NULL),
('Mia', 'Johansson', NULL, '20202020M', '2005-02-27', 'Sueca', 1, NULL, 'mia@mail.com', '+46701112244', 49, true, '2025-12-01', NULL),
('Pablo', 'Navarro', 'Roca', '21212121P', '1998-07-07', 'Española', 1, NULL, 'pablo@mail.com', '+34600112255', 50, true, '2025-12-01', NULL);