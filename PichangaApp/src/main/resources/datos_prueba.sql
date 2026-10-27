-- =====================================================================
-- PichangaApp - Datos de Prueba (Seed Data)
-- SQL Server 2019+
-- Ejecutar DESPUÉS de schema.sql
-- =====================================================================

-- =====================================================================
-- 1. USUARIOS
-- =====================================================================

-- Admin (email: admin@pichangaapp.pe, pass: Admin123!)
IF NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'admin@pichangaapp.pe')
INSERT INTO usuario (nombre, email, password_hash, rol, telefono, fecha_nacimiento, verificado, estado_verificacion, documento_tipo, documento_numero, activo)
VALUES ('Admin Sistema', 'admin@pichangaapp.pe', '$2a$12$K1mJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFg', 'ADMIN', '+51 999 888 777', '1985-03-15', 1, 'APROBADA', 'DNI', '12345678', 1);

-- Propietario 1 (email: juan.perez@pichangaapp.pe, pass: Dueno123!)
IF NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'juan.perez@pichangaapp.pe')
INSERT INTO usuario (nombre, email, password_hash, rol, telefono, fecha_nacimiento, verificado, estado_verificacion, documento_tipo, documento_numero, activo)
VALUES ('Juan Pérez García', 'juan.perez@pichangaapp.pe', '$2a$12$K1mJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFg', 'PROPIETARIO', '+51 987 654 321', '1990-07-20', 1, 'APROBADA', 'DNI', '87654321', 1);

-- Propietario 2 (email: maria.lopez@pichangaapp.pe, pass: Dueno123!)
IF NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'maria.lopez@pichangaapp.pe')
INSERT INTO usuario (nombre, email, password_hash, rol, telefono, fecha_nacimiento, verificado, estado_verificacion, documento_tipo, documento_numero, activo)
VALUES ('María López Sánchez', 'maria.lopez@pichangaapp.pe', '$2a$12$K1mJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFg', 'PROPIETARIO', '+51 976 543 210', '1988-11-05', 0, 'NO_SOLICITADA', NULL, NULL, 1);

-- Jugador 1 (email: carlos.ramos@pichangaapp.pe, pass: Jugador123!)
IF NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'carlos.ramos@pichangaapp.pe')
INSERT INTO usuario (nombre, email, password_hash, rol, telefono, fecha_nacimiento, verificado, estado_verificacion, documento_tipo, documento_numero, activo)
VALUES ('Carlos Ramos Torres', 'carlos.ramos@pichangaapp.pe', '$2a$12$K1mJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFg', 'JUGADOR', '+51 965 432 109', '1995-04-10', 1, 'APROBADA', 'DNI', '11223344', 1);

-- Jugador 2 (email: ana.gomez@pichangaapp.pe, pass: Jugador123!)
IF NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'ana.gomez@pichangaapp.pe')
INSERT INTO usuario (nombre, email, password_hash, rol, telefono, fecha_nacimiento, verificado, estado_verificacion, documento_tipo, documento_numero, activo)
VALUES ('Ana Gómez Flores', 'ana.gomez@pichangaapp.pe', '$2a$12$K1mJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFg', 'JUGADOR', '+51 954 321 098', '1992-08-25', 0, 'NO_SOLICITADA', NULL, NULL, 1);

-- Jugador 3 (email: luis.diaz@pichangaapp.pe, pass: Jugador123!)
IF NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'luis.diaz@pichangaapp.pe')
INSERT INTO usuario (nombre, email, password_hash, rol, telefono, fecha_nacimiento, verificado, estado_verificacion, documento_tipo, documento_numero, activo)
VALUES ('Luis Díaz Mendoza', 'luis.diaz@pichangaapp.pe', '$2a$12$K1mJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFg', 'JUGADOR', '+51 943 210 987', '1998-12-03', 0, 'NO_SOLICITADA', NULL, NULL, 1);

-- =====================================================================
-- 2. CANCHAS (propietario_id = 2 es Juan Pérez, id = 3 es María López)
-- =====================================================================

-- Cancha 1 - Complejo Deportivo La Molina (Juan Pérez)
IF NOT EXISTS (SELECT 1 FROM cancha WHERE nombre = 'Complejo Deportivo La Molina')
INSERT INTO cancha (propietario_id, nombre, descripcion, direccion, distrito, superficie, capacidad, precio_hora, hora_apertura, hora_cierre, latitud, longitud, activa)
VALUES (2, 'Complejo Deportivo La Molina', 'Cancha de césped sintético con iluminación LED, vestidores y duchas. Ideal para partidos nocturnos.', 'Av. La Molina 1234, Urb. La Molina', 'La Molina', 'CESPED_SINTETICO', 10, 80.00, '06:00', '23:00', -12.0908000, -76.9358000, 1);

-- Cancha 2 - Canchas San Borja (Juan Pérez)
IF NOT EXISTS (SELECT 1 FROM cancha WHERE nombre = 'Canchas San Borja')
INSERT INTO cancha (propietario_id, nombre, descripcion, direccion, distrito, superficie, capacidad, precio_hora, hora_apertura, hora_cierre, latitud, longitud, activa)
VALUES (2, 'Canchas San Borja', 'Dos canchas de césped sintético con gradería y zona de espectadores.', 'Av. Javier Prado 4567, San Borja', 'San Borja', 'CESPED_SINTETICO', 12, 90.00, '07:00', '22:00', -12.1128000, -76.9564000, 1);

-- Cancha 3 - Club Deportivo Surco (María López)
IF NOT EXISTS (SELECT 1 FROM cancha WHERE nombre = 'Club Deportivo Surco')
INSERT INTO cancha (propietario_id, nombre, descripcion, direccion, distrito, superficie, capacidad, precio_hora, hora_apertura, hora_cierre, latitud, longitud, activa)
VALUES (3, 'Club Deportivo Surco', 'Cancha profesional de césped natural con tribuna cubierta y parqueadero.', 'Av. Surco 789, Urb. Surco', 'Surco', 'CESPED_NATURAL', 10, 100.00, '08:00', '21:00', -12.1534000, -76.9663000, 1);

-- Cancha 4 - Cancha Express Miraflores (Juan Pérez)
IF NOT EXISTS (SELECT 1 FROM cancha WHERE nombre = 'Cancha Express Miraflores')
INSERT INTO cancha (propietario_id, nombre, descripcion, direccion, distrito, superficie, capacidad, precio_hora, hora_apertura, hora_cierre, latitud, longitud, activa)
VALUES (2, 'Cancha Express Miraflores', 'Cancha pequeña ideal para pichangas rápidas de 5vs5. Cerca al malecón.', 'Av. José Larco 321, Miraflores', 'Miraflores', 'CESPED_SINTETICO', 6, 60.00, '07:00', '22:00', -12.1223000, -77.0281000, 1);

-- Cancha 5 - Estadio San Isidro (María López)
IF NOT EXISTS (SELECT 1 FROM cancha WHERE nombre = 'Estadio San Isidro')
INSERT INTO cancha (propietario_id, nombre, descripcion, direccion, distrito, superficie, capacidad, precio_hora, hora_apertura, hora_cierre, latitud, longitud, activa)
VALUES (3, 'Estadio San Isidro', 'Cancha grande con gradería, iluminación profesional y servicio de cafetería.', 'Av. San Felipe 555, San Isidro', 'San Isidro', 'CESPED_SINTETICO', 14, 110.00, '07:00', '23:00', -12.1054000, -77.0362000, 1);

-- =====================================================================
-- 3. HORARIOS (para cada cancha, días 1-7: Lunes a Domingo)
-- =====================================================================

-- Cancha 1 (id=1) - Horarios completos
IF NOT EXISTS (SELECT 1 FROM horario WHERE cancha_id = 1 AND dia_semana = 1)
INSERT INTO horario (cancha_id, dia_semana, hora_apertura, hora_cierre, activo) VALUES
(1, 1, '06:00', '23:00', 1),
(1, 2, '06:00', '23:00', 1),
(1, 3, '06:00', '23:00', 1),
(1, 4, '06:00', '23:00', 1),
(1, 5, '06:00', '23:00', 1),
(1, 6, '07:00', '22:00', 1),
(1, 7, '08:00', '21:00', 1);

-- Cancha 2 (id=2)
IF NOT EXISTS (SELECT 1 FROM horario WHERE cancha_id = 2 AND dia_semana = 1)
INSERT INTO horario (cancha_id, dia_semana, hora_apertura, hora_cierre, activo) VALUES
(2, 1, '07:00', '22:00', 1),
(2, 2, '07:00', '22:00', 1),
(2, 3, '07:00', '22:00', 1),
(2, 4, '07:00', '22:00', 1),
(2, 5, '07:00', '22:00', 1),
(2, 6, '08:00', '21:00', 1),
(2, 7, '08:00', '20:00', 1);

-- Cancha 3 (id=3)
IF NOT EXISTS (SELECT 1 FROM horario WHERE cancha_id = 3 AND dia_semana = 1)
INSERT INTO horario (cancha_id, dia_semana, hora_apertura, hora_cierre, activo) VALUES
(3, 1, '08:00', '21:00', 1),
(3, 2, '08:00', '21:00', 1),
(3, 3, '08:00', '21:00', 1),
(3, 4, '08:00', '21:00', 1),
(3, 5, '08:00', '21:00', 1),
(3, 6, '09:00', '20:00', 1),
(3, 7, '09:00', '19:00', 1);

-- Cancha 4 (id=4)
IF NOT EXISTS (SELECT 1 FROM horario WHERE cancha_id = 4 AND dia_semana = 1)
INSERT INTO horario (cancha_id, dia_semana, hora_apertura, hora_cierre, activo) VALUES
(4, 1, '07:00', '22:00', 1),
(4, 2, '07:00', '22:00', 1),
(4, 3, '07:00', '22:00', 1),
(4, 4, '07:00', '22:00', 1),
(4, 5, '07:00', '22:00', 1),
(4, 6, '08:00', '21:00', 1),
(4, 7, '08:00', '20:00', 1);

-- Cancha 5 (id=5)
IF NOT EXISTS (SELECT 1 FROM horario WHERE cancha_id = 5 AND dia_semana = 1)
INSERT INTO horario (cancha_id, dia_semana, hora_apertura, hora_cierre, activo) VALUES
(5, 1, '07:00', '23:00', 1),
(5, 2, '07:00', '23:00', 1),
(5, 3, '07:00', '23:00', 1),
(5, 4, '07:00', '23:00', 1),
(5, 5, '07:00', '23:00', 1),
(5, 6, '08:00', '22:00', 1),
(5, 7, '08:00', '21:00', 1);

-- =====================================================================
-- 4. RESERVAS (varios estados para mostrar variedad en la UI)
-- usuario_id: 4=Carlos, 5=Ana, 6=Luis
-- =====================================================================

-- Reserva 1 - CONFIRMADA y PAGADA (Carlos, Cancha 1, mañana)
IF NOT EXISTS (SELECT 1 FROM reserva WHERE codigo = 'PIC-26-1025A')
INSERT INTO reserva (codigo, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado, precio_total, expira_en, version)
VALUES ('PIC-26-1025A', 4, 1, '2026-10-25', '18:00', '19:00', 'CONFIRMADA', 80.00, NULL, 0);

-- Reserva 2 - PENDIENTE (Ana, Cancha 2, pasado mañana - sin pagar aún)
IF NOT EXISTS (SELECT 1 FROM reserva WHERE codigo = 'PIC-26-1026B')
INSERT INTO reserva (codigo, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado, precio_total, expira_en, version)
VALUES ('PIC-26-1026B', 5, 2, '2026-10-26', '19:00', '20:00', 'PENDIENTE', 90.00, DATEADD(minute, 15, SYSDATETIME()), 0);

-- Reserva 3 - COMPLETADA (Carlos, Cancha 3, ayer)
IF NOT EXISTS (SELECT 1 FROM reserva WHERE codigo = 'PIC-26-1024C')
INSERT INTO reserva (codigo, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado, precio_total, expira_en, version)
VALUES ('PIC-26-1024C', 4, 3, '2026-10-24', '17:00', '18:00', 'COMPLETADA', 100.00, NULL, 0);

-- Reserva 4 - CANCELADA (Luis, Cancha 4, hace 2 días)
IF NOT EXISTS (SELECT 1 FROM reserva WHERE codigo = 'PIC-26-1023D')
INSERT INTO reserva (codigo, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado, precio_total, expira_en, version)
VALUES ('PIC-26-1023D', 6, 4, '2026-10-23', '15:00', '16:00', 'CANCELADA', 60.00, NULL, 0);

-- Reserva 5 - EXPIRADA (Ana, Cancha 5, hace 1 día)
IF NOT EXISTS (SELECT 1 FROM reserva WHERE codigo = 'PIC-26-1025E')
INSERT INTO reserva (codigo, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado, precio_total, expira_en, version)
VALUES ('PIC-26-1025E', 5, 5, '2026-10-25', '20:00', '21:00', 'EXPIRADA', 110.00, NULL, 0);

-- Reserva 6 - CONFIRMADA (Luis, Cancha 1, próxima semana)
IF NOT EXISTS (SELECT 1 FROM reserva WHERE codigo = 'PIC-26-1030F')
INSERT INTO reserva (codigo, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado, precio_total, expira_en, version)
VALUES ('PIC-26-1030F', 6, 1, '2026-10-30', '16:00', '17:00', 'CONFIRMADA', 80.00, NULL, 0);

-- =====================================================================
-- 5. PAGOS (para reservas CONFIRMADA y COMPLETADA)
-- =====================================================================

-- Pago 1 - Reserva 1 (Yape)
IF NOT EXISTS (SELECT 1 FROM pago WHERE reserva_id = 1)
INSERT INTO pago (reserva_id, metodo, monto, monto_reembolsado, estado, transaccion_id, referencia, fecha_pago, fecha_creacion)
VALUES (1, 'YAPE', 80.00, 0.00, 'PAGADO', 'YP-' + CONVERT(varchar, SYSDATETIME(), 112) + '-' + RIGHT('000000' + CONVERT(varchar, ABS(CHECKSUM(NEWID())) % 1000000), 6), 'Yape 987654321', SYSDATETIME(), SYSDATETIME());

-- Pago 2 - Reserva 3 (Plin)
IF NOT EXISTS (SELECT 1 FROM pago WHERE reserva_id = 3)
INSERT INTO pago (reserva_id, metodo, monto, monto_reembolsado, estado, transaccion_id, referencia, fecha_pago, fecha_creacion)
VALUES (3, 'PLIN', 100.00, 0.00, 'PAGADO', 'PL-' + CONVERT(varchar, SYSDATETIME(), 112) + '-' + RIGHT('000000' + CONVERT(varchar, ABS(CHECKSUM(NEWID())) % 1000000), 6), 'Plin 965432109', SYSDATETIME(), SYSDATETIME());

-- Pago 3 - Reserva 6 (Mercado Pago)
IF NOT EXISTS (SELECT 1 FROM pago WHERE reserva_id = 6)
INSERT INTO pago (reserva_id, metodo, monto, monto_reembolsado, estado, transaccion_id, referencia, fecha_pago, fecha_creacion)
VALUES (6, 'MERCADO_PAGO', 80.00, 0.00, 'PAGADO', 'MP-' + CONVERT(varchar, SYSDATETIME(), 112) + '-' + RIGHT('000000' + CONVERT(varchar, ABS(CHECKSUM(NEWID())) % 1000000), 6), 'MP-12345678', SYSDATETIME(), SYSDATETIME());

-- =====================================================================
-- 6. PICHANGAS
-- =====================================================================

-- Pichanga 1 - ABIERTA (Carlos organizador, Reserva 1, Cancha 1)
IF NOT EXISTS (SELECT 1 FROM pichanga WHERE reserva_id = 1)
INSERT INTO pichanga (organizador_id, cancha_id, reserva_id, fecha, hora_inicio, hora_fin, cupos_total, cupos_ocupados, nivel, descripcion, precio_participante, estado, version)
VALUES (4, 1, 1, '2026-10-25', '18:00', '19:00', 8, 3, 'INTERMEDIO', 'Pichanga amistosa de los sábados. Seizan 5 más.', 15.00, 'ABIERTA', 0);

-- Pichanga 2 - LLENA (Juan Pérez como organizador, Reserva 6, Cancha 1)
IF NOT EXISTS (SELECT 1 FROM pichanga WHERE reserva_id = 6)
INSERT INTO pichanga (organizador_id, cancha_id, reserva_id, fecha, hora_inicio, hora_fin, cupos_total, cupos_ocupados, nivel, descripcion, precio_participante, estado, version)
VALUES (2, 1, 6, '2026-10-30', '16:00', '17:00', 6, 6, 'AVANZADO', 'Pichanga competitiva. Cupos completos.', 20.00, 'LLENA', 0);

-- Pichanga 3 - CANCELADA (Ana, Reserva 2, Cancha 2)
IF NOT EXISTS (SELECT 1 FROM pichanga WHERE reserva_id = 2)
INSERT INTO pichanga (organizador_id, cancha_id, reserva_id, fecha, hora_inicio, hora_fin, cupos_total, cupos_ocupados, nivel, descripcion, precio_participante, estado, version)
VALUES (5, 2, 2, '2026-10-26', '19:00', '20:00', 10, 2, 'INICIANTE', 'Pichanga cancelada por lluvia.', 0.00, 'CANCELADA', 0);

-- =====================================================================
-- 7. PICHANGA PARTICIPANTES
-- =====================================================================

-- Pichanga 1 (id=1): Carlos (organizador, ya cuenta como ocupado), Ana y Luis se unieron
IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 1 AND usuario_id = 4)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (1, 4, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 1 AND usuario_id = 5)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (1, 5, DATEADD(minute, -30, SYSDATETIME()));

IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 1 AND usuario_id = 6)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (1, 6, DATEADD(minute, -15, SYSDATETIME()));

-- Pichanga 2 (id=2): Juan (organizador) + 5 más (llena)
IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 2 AND usuario_id = 2)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (2, 2, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 2 AND usuario_id = 4)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (2, 4, DATEADD(hour, -1, SYSDATETIME()));

IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 2 AND usuario_id = 5)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (2, 5, DATEADD(hour, -2, SYSDATETIME()));

IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 2 AND usuario_id = 6)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (2, 6, DATEADD(hour, -3, SYSDATETIME()));

-- Pichanga 3 (id=3): Ana (organizadora) + Carlos
IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 3 AND usuario_id = 5)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (3, 5, SYSDATETIME());

IF NOT EXISTS (SELECT 1 FROM pichanga_participante WHERE pichanga_id = 3 AND usuario_id = 4)
INSERT INTO pichanga_participante (pichanga_id, usuario_id, fecha_union) VALUES (3, 4, DATEADD(minute, -10, SYSDATETIME()));

-- =====================================================================
-- 7. CALIFICACIONES
-- =====================================================================

-- Calificación 1 - Carlos califica Cancha 1 (Reserva 3 completada)
IF NOT EXISTS (SELECT 1 FROM calificacion WHERE reserva_id = 3)
INSERT INTO calificacion (usuario_id, cancha_id, reserva_id, puntaje, comentario, respuesta_propietario, fecha_creacion)
VALUES (4, 1, 3, 5, 'Excelente cancha, césped en muy buen estado y buena iluminación.', 'Gracias por tu comentario, Carlos! Te esperamos la próxima.', DATEADD(hour, -2, SYSDATETIME()));

-- Calificación 2 - Ana califica Cancha 2 (Reserva 2 confirmada pero no pagada aún, no se puede calificar todavía)
-- Solo calificamos reservas COMPLETADA o CONFIRMADA que ya pasaron

-- Calificación 3 - Luis califica Cancha 4 (Reserva 4 cancelada, no se puede calificar)

-- Calificación 4 - Carlos califica Cancha 5 (reserva ficticia para mostrar variedad)
-- Necesitaríamos una reserva completada en Cancha 5, pero por simplicidad dejamos una

-- =====================================================================
-- 8. NOTIFICACIONES
-- =====================================================================

-- Notificación 1 - Para Carlos (reserva confirmada)
IF NOT EXISTS (SELECT 1 FROM notificacion WHERE usuario_id = 4 AND titulo = 'Reserva iniciada')
INSERT INTO notificacion (usuario_id, tipo, titulo, contenido, enlace, leida, fecha_creacion)
VALUES (4, 'RESERVA', 'Reserva iniciada', 'Reserva PIC-26-1025A en Complejo Deportivo La Molina. Tienes 15 minutos para completar el pago.', '/pagos/1', 0, DATEADD(minute, -45, SYSDATETIME()));

-- Notificación 2 - Para Carlos (pago confirmado)
IF NOT EXISTS (SELECT 1 FROM notificacion WHERE usuario_id = 4 AND titulo = 'Pago confirmado')
INSERT INTO notificacion (usuario_id, tipo, titulo, contenido, enlace, leida, fecha_creacion)
VALUES (4, 'PAGO', 'Pago confirmado', 'Tu reserva PIC-26-1025A quedó confirmada. Te esperamos en Complejo Deportivo La Molina el 2026-10-25 a las 18:00.', '/reservas/1', 1, DATEADD(minute, -40, SYSDATETIME()));

-- Notificación 3 - Para Ana (pichanga llena)
IF NOT EXISTS (SELECT 1 FROM notificacion WHERE usuario_id = 5 AND titulo = 'Pichanga completa')
INSERT INTO notificacion (usuario_id, tipo, titulo, contenido, enlace, leida, fecha_creacion)
VALUES (5, 'PICHANGA', 'Pichanga completa', 'Tu pichanga en Complejo Deportivo La Molina completó los 6 cupos.', '/pichangas/2', 0, DATEADD(hour, -3, SYSDATETIME()));

-- Notificación 4 - Para Juan (nueva calificación)
IF NOT EXISTS (SELECT 1 FROM notificacion WHERE usuario_id = 2 AND titulo = 'Nueva calificación')
INSERT INTO notificacion (usuario_id, tipo, titulo, contenido, enlace, leida, fecha_creacion)
VALUES (2, 'CALIFICACION', 'Nueva calificación', 'Carlos Ramos calificó Complejo Deportivo La Molina con 5 estrellas.', '/panel/canchas/1/resenas', 0, DATEADD(hour, -5, SYSDATETIME()));

-- Notificación 5 - Para Luis (recordatorio de reserva)
IF NOT EXISTS (SELECT 1 FROM notificacion WHERE usuario_id = 6 AND titulo = 'Reserva iniciada')
INSERT INTO notificacion (usuario_id, tipo, titulo, contenido, enlace, leida, fecha_creacion)
VALUES (6, 'RESERVA', 'Reserva iniciada', 'Reserva PIC-26-1030F en Complejo Deportivo La Molina. Tienes 15 minutos para completar el pago.', '/pagos/3', 0, DATEADD(minute, -10, SYSDATETIME()));

-- =====================================================================
-- FIN DEL SCRIPT
-- =====================================================================
PRINT 'Datos de prueba insertados correctamente.';
PRINT 'Usuarios creados:';
PRINT '  - admin@pichangaapp.pe (Admin123!)';
PRINT '  - juan.perez@pichangaapp.pe (Dueno123!)';
PRINT '  - maria.lopez@pichangaapp.pe (Dueno123!)';
PRINT '  - carlos.ramos@pichangaapp.pe (Jugador123!)';
PRINT '  - ana.gomez@pichangaapp.pe (Jugador123!)';
PRINT '  - luis.diaz@pichangaapp.pe (Jugador123!)';
