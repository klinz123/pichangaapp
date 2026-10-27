-- =====================================================================
-- PichangaApp - Inicializacion de base de datos (desarrollo)
--
-- Crea la base PichangaApp, el login de la aplicacion y sus permisos.
-- Es idempotente: se puede ejecutar varias veces sin efectos secundarios.
-- Las tablas NO se crean aqui: las crea schema.sql cuando arranca la app.
--
-- Uso manual:
--   sqlcmd -S localhost -U sa -P "TU_PASSWORD" -C ^
--          -v DB_USER="pichanga_app" -v DB_PASSWORD="TuPassword" ^
--          -i init-db.sql
-- =====================================================================

IF DB_ID('PichangaApp') IS NULL
BEGIN
    PRINT 'Creando base de datos PichangaApp...';
    EXEC('CREATE DATABASE PichangaApp');
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = '$(DB_USER)')
BEGIN
    PRINT 'Creando login $(DB_USER)...';
    EXEC('CREATE LOGIN [$(DB_USER)] WITH PASSWORD = ''$(DB_PASSWORD)'', CHECK_POLICY = OFF');
END
GO

USE PichangaApp;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = '$(DB_USER)')
BEGIN
    PRINT 'Creando usuario $(DB_USER) en PichangaApp...';
    CREATE USER [$(DB_USER)] FOR LOGIN [$(DB_USER)];
    ALTER ROLE db_owner ADD MEMBER [$(DB_USER)];
END
GO

PRINT 'Base de datos PichangaApp lista.';
GO
