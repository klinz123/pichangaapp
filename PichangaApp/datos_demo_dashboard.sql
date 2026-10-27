-- =====================================================================
-- PichangaApp - Datos de demostracion para los dashboards
-- Genera actividad realista: reservas pagadas de los ultimos 7 dias,
-- varias reservas para HOY (ocupacion y agenda) y reservas proximas.
-- Idempotente: si ya existen los codigos PIC-DEMO-10xx, no hace nada.
-- =====================================================================
SET NOCOUNT ON;
SET XACT_ABORT ON;
SET ANSI_NULLS ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET QUOTED_IDENTIFIER ON;
SET NUMERIC_ROUNDABORT OFF;

IF EXISTS (SELECT 1 FROM reserva WHERE codigo LIKE 'PIC-DEMO-10%')
BEGIN
    PRINT 'Los datos de demostracion ya existen. No se inserta nada.';
    RETURN;
END

BEGIN TRAN;

DECLARE @plan TABLE (
    codigo      NVARCHAR(20) PRIMARY KEY,
    usuario_id  BIGINT,
    cancha_id   BIGINT,
    fecha       DATE,
    hora_inicio TIME,
    hora_fin    TIME,
    estado      NVARCHAR(20),
    precio      DECIMAL(10,2),
    metodo      NVARCHAR(20)
);

-- Propietario Juan Perez (canchas 1, 2, 5, 7, 8, 10)
-- Dias pasados: COMPLETADA
INSERT INTO @plan VALUES
('PIC-DEMO-1001', 4,  1, '2026-10-20', '20:00', '21:00', 'COMPLETADA',  80.00, 'YAPE'),
('PIC-DEMO-1002', 5,  5, '2026-10-20', '21:00', '22:00', 'COMPLETADA', 110.00, 'PLIN'),
('PIC-DEMO-1003', 6,  2, '2026-10-21', '19:00', '20:00', 'COMPLETADA',  60.00, 'MERCADO_PAGO'),
('PIC-DEMO-1004', 7,  7, '2026-10-21', '18:00', '19:00', 'COMPLETADA',  80.00, 'YAPE'),
('PIC-DEMO-1005', 9,  8, '2026-10-22', '20:00', '21:00', 'COMPLETADA',  90.00, 'PLIN'),
('PIC-DEMO-1006', 10, 10, '2026-10-22', '19:00', '20:00', 'COMPLETADA', 60.00, 'YAPE'),
('PIC-DEMO-1007', 11, 1, '2026-10-22', '21:00', '22:00', 'COMPLETADA',  80.00, 'PAYPAL'),
('PIC-DEMO-1008', 4,  5, '2026-10-23', '18:00', '19:00', 'COMPLETADA', 110.00, 'MERCADO_PAGO'),
('PIC-DEMO-1009', 5,  2, '2026-10-23', '21:00', '22:00', 'COMPLETADA',  60.00, 'PLIN'),
('PIC-DEMO-1010', 6,  7, '2026-10-24', '19:00', '20:00', 'COMPLETADA',  80.00, 'YAPE'),
('PIC-DEMO-1011', 7, 10, '2026-10-24', '20:00', '21:00', 'COMPLETADA',  60.00, 'PLIN'),
('PIC-DEMO-1012', 9,  8, '2026-10-24', '21:00', '22:00', 'COMPLETADA',  90.00, 'MERCADO_PAGO'),
('PIC-DEMO-1013', 10, 1, '2026-10-25', '19:00', '20:00', 'COMPLETADA',  80.00, 'YAPE'),
('PIC-DEMO-1014', 11, 5, '2026-10-25', '19:00', '20:00', 'COMPLETADA', 110.00, 'PAYPAL'),
('PIC-DEMO-1015', 4,  2, '2026-10-25', '18:00', '19:00', 'COMPLETADA',  60.00, 'PLIN'),

-- HOY (26/10): CONFIRMADA y pagadas
('PIC-DEMO-1016', 5,  1, '2026-10-26', '16:00', '17:00', 'CONFIRMADA',  80.00, 'YAPE'),
('PIC-DEMO-1017', 6,  2, '2026-10-26', '17:00', '18:00', 'CONFIRMADA',  60.00, 'MERCADO_PAGO'),
('PIC-DEMO-1018', 7,  5, '2026-10-26', '18:00', '19:00', 'CONFIRMADA', 110.00, 'PLIN'),
('PIC-DEMO-1019', 9,  7, '2026-10-26', '19:00', '20:00', 'CONFIRMADA',  80.00, 'YAPE'),
('PIC-DEMO-1020', 10, 8, '2026-10-26', '20:00', '21:00', 'CONFIRMADA',  90.00, 'MERCADO_PAGO'),
('PIC-DEMO-1021', 11, 10, '2026-10-26', '21:00', '22:00', 'CONFIRMADA', 60.00, 'PLIN'),
('PIC-DEMO-1022', 4,  1, '2026-10-26', '20:00', '21:00', 'CONFIRMADA',  80.00, 'PAYPAL'),
('PIC-DEMO-1023', 5,  2, '2026-10-26', '21:00', '22:00', 'CONFIRMADA',  60.00, 'YAPE'),

-- Proximas: CONFIRMADA y pagadas
('PIC-DEMO-1024', 6,  5, '2026-10-28', '19:00', '20:00', 'CONFIRMADA', 110.00, 'PLIN'),
('PIC-DEMO-1025', 7,  7, '2026-10-29', '20:00', '21:00', 'CONFIRMADA',  80.00, 'MERCADO_PAGO'),
('PIC-DEMO-1026', 9,  8, '2026-10-31', '18:00', '19:00', 'CONFIRMADA',  90.00, 'YAPE'),
('PIC-DEMO-1027', 10, 10, '2026-11-01', '19:00', '20:00', 'CONFIRMADA', 60.00, 'PLIN'),
('PIC-DEMO-1028', 11, 2, '2026-11-02', '20:00', '21:00', 'CONFIRMADA',  60.00, 'YAPE'),

-- Otras canchas (global del admin): canchas 3, 6, 9 y 11
('PIC-DEMO-1029', 4,  3, '2026-10-21', '20:00', '21:00', 'COMPLETADA',  95.00, 'MERCADO_PAGO'),
('PIC-DEMO-1030', 5,  6, '2026-10-23', '19:00', '20:00', 'COMPLETADA', 120.00, 'YAPE'),
('PIC-DEMO-1031', 6,  9, '2026-10-24', '18:00', '19:00', 'COMPLETADA', 100.00, 'PLIN'),
('PIC-DEMO-1032', 7,  3, '2026-10-26', '19:00', '20:00', 'CONFIRMADA',  95.00, 'MERCADO_PAGO'),
('PIC-DEMO-1033', 9, 11, '2026-10-26', '20:00', '21:00', 'CONFIRMADA', 110.00, 'YAPE'),
('PIC-DEMO-1034', 10, 6, '2026-10-26', '21:00', '22:00', 'CONFIRMADA', 120.00, 'PLIN'),
('PIC-DEMO-1035', 11, 3, '2026-10-27', '19:00', '20:00', 'CONFIRMADA',  95.00, 'PAYPAL'),
('PIC-DEMO-1036', 4,  9, '2026-10-29', '20:00', '21:00', 'CONFIRMADA', 100.00, 'MERCADO_PAGO');

DECLARE @nuevas TABLE (codigo NVARCHAR(20), reserva_id BIGINT);

INSERT INTO reserva (codigo, usuario_id, cancha_id, fecha, hora_inicio, hora_fin,
                     estado, precio_total, fecha_creacion, expira_en, version)
OUTPUT inserted.codigo, inserted.id INTO @nuevas
SELECT codigo, usuario_id, cancha_id, fecha, hora_inicio, hora_fin,
       estado, precio,
       DATEADD(day, -2, CAST(fecha AS DATETIME2)), NULL, 0
FROM @plan;

INSERT INTO pago (reserva_id, metodo, monto, monto_reembolsado, estado,
                  transaccion_id, referencia, fecha_pago, fecha_creacion)
SELECT n.reserva_id,
       p.metodo,
       p.precio,
       0,
       'PAGADO',
       CONCAT(p.metodo, '-DEMO-', n.reserva_id),
       CONCAT('Demo ', p.codigo),
       DATEADD(hour, 10, DATEADD(day, -1, CAST(p.fecha AS DATETIME2))),
       DATEADD(hour, 10, DATEADD(day, -2, CAST(p.fecha AS DATETIME2)))
FROM @plan p
JOIN @nuevas n ON n.codigo = p.codigo;

COMMIT;

SELECT 'RESERVAS_INSERTADAS' AS dato, COUNT(*) AS total FROM @nuevas;
SELECT 'PAGOS_TOTALES' AS dato, COUNT(*) AS total FROM pago;
