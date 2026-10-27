-- =====================================================================
-- PichangaApp - Esquema de base de datos (SQL Server 2019+)
-- Script idempotente: se ejecuta al iniciar la aplicacion (spring.sql.init)
-- y tambien puede aplicarse manualmente con sqlcmd.
-- =====================================================================

IF OBJECT_ID('usuario', 'U') IS NULL
CREATE TABLE usuario (
    id                    BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre                NVARCHAR(120)  NOT NULL,
    email                 NVARCHAR(150)  NOT NULL,
    password_hash         NVARCHAR(100)  NOT NULL,
    rol                   NVARCHAR(20)   NOT NULL,
    telefono              NVARCHAR(20)   NULL,
    fecha_nacimiento      DATE           NULL,
    verificado            BIT            NOT NULL CONSTRAINT df_usuario_verificado DEFAULT 0,
    estado_verificacion   NVARCHAR(20)   NOT NULL CONSTRAINT df_usuario_estado_verif DEFAULT 'NO_SOLICITADA',
    documento_tipo        NVARCHAR(30)   NULL,
    documento_numero      NVARCHAR(30)   NULL,
    intentos_fallidos     INT            NOT NULL CONSTRAINT df_usuario_intentos DEFAULT 0,
    bloqueado_hasta       DATETIME2      NULL,
    activo                BIT            NOT NULL CONSTRAINT df_usuario_activo DEFAULT 1,
    fecha_registro        DATETIME2      NOT NULL CONSTRAINT df_usuario_registro DEFAULT SYSDATETIME()
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_usuario_email' AND object_id = OBJECT_ID('usuario'))
CREATE UNIQUE INDEX ux_usuario_email ON usuario(email);

IF OBJECT_ID('cancha', 'U') IS NULL
CREATE TABLE cancha (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    propietario_id    BIGINT         NOT NULL,
    nombre            NVARCHAR(120)  NOT NULL,
    descripcion       NVARCHAR(1000) NULL,
    direccion         NVARCHAR(200)  NOT NULL,
    distrito          NVARCHAR(80)   NOT NULL,
    superficie        NVARCHAR(30)   NOT NULL,
    capacidad         INT            NOT NULL CONSTRAINT df_cancha_capacidad DEFAULT 10,
    precio_hora       DECIMAL(10,2)  NOT NULL,
    hora_apertura     TIME           NOT NULL,
    hora_cierre       TIME           NOT NULL,
    latitud           DECIMAL(10,7)  NULL,
    longitud          DECIMAL(10,7)  NULL,
    activa            BIT            NOT NULL CONSTRAINT df_cancha_activa DEFAULT 1,
    fecha_creacion    DATETIME2      NOT NULL CONSTRAINT df_cancha_creacion DEFAULT SYSDATETIME(),
    CONSTRAINT fk_cancha_propietario FOREIGN KEY (propietario_id) REFERENCES usuario(id)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_cancha_distrito' AND object_id = OBJECT_ID('cancha'))
CREATE INDEX ix_cancha_distrito ON cancha(distrito, activa);

IF OBJECT_ID('horario', 'U') IS NULL
CREATE TABLE horario (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    cancha_id      BIGINT NOT NULL,
    dia_semana     INT    NOT NULL,
    hora_apertura  TIME   NOT NULL,
    hora_cierre    TIME   NOT NULL,
    activo         BIT    NOT NULL CONSTRAINT df_horario_activo DEFAULT 1,
    CONSTRAINT fk_horario_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id),
    CONSTRAINT ck_horario_dia CHECK (dia_semana BETWEEN 1 AND 7)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_horario_cancha_dia' AND object_id = OBJECT_ID('horario'))
CREATE UNIQUE INDEX ux_horario_cancha_dia ON horario(cancha_id, dia_semana);

IF OBJECT_ID('bloqueo_horario', 'U') IS NULL
CREATE TABLE bloqueo_horario (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    cancha_id      BIGINT        NOT NULL,
    fecha          DATE          NOT NULL,
    hora_inicio    TIME          NOT NULL,
    hora_fin       TIME          NOT NULL,
    motivo         NVARCHAR(200) NULL,
    fecha_creacion DATETIME2     NOT NULL CONSTRAINT df_bloqueo_creacion DEFAULT SYSDATETIME(),
    CONSTRAINT fk_bloqueo_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_bloqueo_slot' AND object_id = OBJECT_ID('bloqueo_horario'))
CREATE UNIQUE INDEX ux_bloqueo_slot ON bloqueo_horario(cancha_id, fecha, hora_inicio);

IF OBJECT_ID('reserva', 'U') IS NULL
CREATE TABLE reserva (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    codigo         NVARCHAR(20)   NOT NULL,
    usuario_id     BIGINT         NOT NULL,
    cancha_id      BIGINT         NOT NULL,
    fecha          DATE           NOT NULL,
    hora_inicio    TIME           NOT NULL,
    hora_fin       TIME           NOT NULL,
    estado         NVARCHAR(20)   NOT NULL,
    precio_total   DECIMAL(10,2)  NOT NULL,
    fecha_creacion DATETIME2      NOT NULL CONSTRAINT df_reserva_creacion DEFAULT SYSDATETIME(),
    expira_en      DATETIME2      NULL,
    version        BIGINT         NOT NULL CONSTRAINT df_reserva_version DEFAULT 0,
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_reserva_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_reserva_codigo' AND object_id = OBJECT_ID('reserva'))
CREATE UNIQUE INDEX ux_reserva_codigo ON reserva(codigo);

-- R02: un bloque horario solo puede tener una reserva vigente (pendiente o confirmada).
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_reserva_slot_vigente' AND object_id = OBJECT_ID('reserva'))
CREATE UNIQUE INDEX ux_reserva_slot_vigente ON reserva(cancha_id, fecha, hora_inicio)
    WHERE estado IN ('PENDIENTE', 'CONFIRMADA');

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_reserva_usuario' AND object_id = OBJECT_ID('reserva'))
CREATE INDEX ix_reserva_usuario ON reserva(usuario_id, fecha);

IF OBJECT_ID('pago', 'U') IS NULL
CREATE TABLE pago (
    id                   BIGINT IDENTITY(1,1) PRIMARY KEY,
    reserva_id           BIGINT        NOT NULL,
    metodo               NVARCHAR(20)  NOT NULL,
    monto                DECIMAL(10,2) NOT NULL,
    monto_reembolsado    DECIMAL(10,2) NOT NULL CONSTRAINT df_pago_reembolso DEFAULT 0,
    estado               NVARCHAR(20)  NOT NULL,
    transaccion_id       NVARCHAR(60)  NULL,
    referencia           NVARCHAR(120) NULL,
    fecha_pago           DATETIME2     NULL,
    fecha_creacion       DATETIME2     NOT NULL CONSTRAINT df_pago_creacion DEFAULT SYSDATETIME(),
    CONSTRAINT fk_pago_reserva FOREIGN KEY (reserva_id) REFERENCES reserva(id)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_pago_reserva' AND object_id = OBJECT_ID('pago'))
CREATE UNIQUE INDEX ux_pago_reserva ON pago(reserva_id);

-- R09: el identificador de la pasarela no puede repetirse.
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_pago_transaccion' AND object_id = OBJECT_ID('pago'))
CREATE UNIQUE INDEX ux_pago_transaccion ON pago(transaccion_id) WHERE transaccion_id IS NOT NULL;

IF OBJECT_ID('pichanga', 'U') IS NULL
CREATE TABLE pichanga (
    id                    BIGINT IDENTITY(1,1) PRIMARY KEY,
    organizador_id        BIGINT        NOT NULL,
    cancha_id             BIGINT        NOT NULL,
    reserva_id            BIGINT        NOT NULL,
    fecha                 DATE          NOT NULL,
    hora_inicio           TIME          NOT NULL,
    hora_fin              TIME          NOT NULL,
    cupos_total           INT           NOT NULL,
    cupos_ocupados        INT           NOT NULL CONSTRAINT df_pichanga_ocupados DEFAULT 0,
    nivel                 NVARCHAR(30)  NOT NULL,
    descripcion           NVARCHAR(800) NULL,
    precio_participante   DECIMAL(10,2) NOT NULL CONSTRAINT df_pichanga_precio DEFAULT 0,
    estado                NVARCHAR(20)  NOT NULL,
    fecha_creacion        DATETIME2     NOT NULL CONSTRAINT df_pichanga_creacion DEFAULT SYSDATETIME(),
    version               BIGINT        NOT NULL CONSTRAINT df_pichanga_version DEFAULT 0,
    CONSTRAINT fk_pichanga_organizador FOREIGN KEY (organizador_id) REFERENCES usuario(id),
    CONSTRAINT fk_pichanga_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id),
    CONSTRAINT fk_pichanga_reserva FOREIGN KEY (reserva_id) REFERENCES reserva(id)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_pichanga_reserva' AND object_id = OBJECT_ID('pichanga'))
CREATE UNIQUE INDEX ux_pichanga_reserva ON pichanga(reserva_id);

IF OBJECT_ID('pichanga_participante', 'U') IS NULL
CREATE TABLE pichanga_participante (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    pichanga_id   BIGINT    NOT NULL,
    usuario_id    BIGINT    NOT NULL,
    fecha_union   DATETIME2 NOT NULL CONSTRAINT df_participante_union DEFAULT SYSDATETIME(),
    CONSTRAINT fk_participante_pichanga FOREIGN KEY (pichanga_id) REFERENCES pichanga(id),
    CONSTRAINT fk_participante_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_participante' AND object_id = OBJECT_ID('pichanga_participante'))
CREATE UNIQUE INDEX ux_participante ON pichanga_participante(pichanga_id, usuario_id);

IF OBJECT_ID('calificacion', 'U') IS NULL
CREATE TABLE calificacion (
    id                      BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuario_id              BIGINT        NOT NULL,
    cancha_id               BIGINT        NOT NULL,
    reserva_id              BIGINT        NOT NULL,
    puntaje                 INT           NOT NULL,
    comentario              NVARCHAR(800) NULL,
    respuesta_propietario   NVARCHAR(800) NULL,
    fecha_creacion          DATETIME2     NOT NULL CONSTRAINT df_calificacion_creacion DEFAULT SYSDATETIME(),
    fecha_respuesta         DATETIME2     NULL,
    CONSTRAINT fk_calificacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_calificacion_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id),
    CONSTRAINT fk_calificacion_reserva FOREIGN KEY (reserva_id) REFERENCES reserva(id),
    CONSTRAINT ck_calificacion_puntaje CHECK (puntaje BETWEEN 1 AND 5)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_calificacion_reserva' AND object_id = OBJECT_ID('calificacion'))
CREATE UNIQUE INDEX ux_calificacion_reserva ON calificacion(reserva_id);

IF OBJECT_ID('notificacion', 'U') IS NULL
CREATE TABLE notificacion (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuario_id     BIGINT        NOT NULL,
    tipo           NVARCHAR(30)  NOT NULL,
    titulo         NVARCHAR(120) NOT NULL,
    contenido      NVARCHAR(500) NOT NULL,
    enlace         NVARCHAR(200) NULL,
    leida          BIT           NOT NULL CONSTRAINT df_notificacion_leida DEFAULT 0,
    fecha_creacion DATETIME2     NOT NULL CONSTRAINT df_notificacion_creacion DEFAULT SYSDATETIME(),
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_notificacion_usuario' AND object_id = OBJECT_ID('notificacion'))
CREATE INDEX ix_notificacion_usuario ON notificacion(usuario_id, leida);
