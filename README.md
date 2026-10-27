# PichangaApp

[![CI](../../actions/workflows/build.yml/badge.svg)](../../actions/workflows/build.yml)

Marketplace peruano para **reservar canchas deportivas por hora** y **organizar pichangas** (partidos amateur con cupos abiertos). Centraliza búsqueda, disponibilidad, reserva, pago y calificaciones en un solo lugar, en vez de coordinar todo por WhatsApp, llamadas y mensajes sueltos.

Proyecto académico de Ingeniería de Software construido con **Spring Boot 3 + Thymeleaf + SQL Server**: renderizado en servidor con MVC clásico, sin API REST ni framework de frontend.

## Funcionalidades

**Público**
- Home con canchas destacadas y buscador por distrito, superficie y precio
- Catálogo con filtros y detalle de cancha (fotos, horarios, calificaciones)
- Registro e inicio de sesión con validación de formularios

**Jugador**
- Reserva por franja horaria, con control de disponibilidad y expiración a los 15 minutos
- Pago simulado con estados (pendiente, pagado, reembolsado) y comprobante
- Pichangas: crear, unirse, salir, cancelar y consultar cupos
- Calificar la cancha después de la reserva y responder calificaciones
- Notificaciones, perfil y cambio de contraseña

**Propietario**
- Panel con métricas y reporte de ingresos por cancha
- CRUD de canchas, horarios semanales y bloqueos puntuales
- Gestión de reservas recibidas y cancelaciones

**Administrador**
- Dashboard global, gestión de usuarios y control de estado de canchas

## Stack

| Capa | Tecnología |
| --- | --- |
| Backend | Java 21, Spring Boot 3.3.5 (MVC, Security 6, Data JPA, Validation) |
| Vistas | Thymeleaf, Bootstrap 5 y Bootstrap Icons (vendorizados), Chart.js, CSS propio |
| Persistencia | SQL Server 2019+, Hibernate, `schema.sql` idempotente |
| Build | Maven Wrapper 3.10 (no requiere Maven instalado) |
| Pruebas | JUnit 5, Mockito y MockMvc (25 pruebas) |
| Automatización | GitHub Actions (build + pruebas), Dependabot |

## Requisitos

- **JDK 21**
- **SQL Server 2019 o superior**, o Docker para levantar una instancia local
- No hace falta instalar Maven: el repositorio incluye el wrapper

## Puesta en marcha

### 1. Base de datos

**Opción A — Docker (recomendada)**

```bash
docker compose up -d
```

Levanta SQL Server 2022 y ejecuta `sqlserver-init`, que crea la base `PichangaApp`, el login `pichanga_app` y sus permisos. Para confirmar que terminó:

```bash
docker compose logs sqlserver-init
```

Las credenciales por defecto son `sa` / `Pichanga#2026!Root` y `pichanga_app` / `Pichanga#2026`; se pueden cambiar con las variables `MSSQL_SA_PASSWORD`, `DB_USER` y `DB_PASSWORD`. Si cambias la contraseña de `sa` con el volumen ya creado, hay que reiniciar los datos con `docker compose down -v`.

**Opción B — SQL Server local**

Ejecuta `PichangaApp/database/init-db.sql` con sqlcmd o SSMS, o crea la base a mano:

```sql
CREATE DATABASE PichangaApp;
GO
CREATE LOGIN pichanga_app WITH PASSWORD = 'TuPasswordSegura', CHECK_POLICY = OFF;
GO
USE PichangaApp;
CREATE USER pichanga_app FOR LOGIN pichanga_app;
ALTER ROLE db_owner ADD MEMBER pichanga_app;
```

Las tablas no hay que crearlas: `schema.sql` se aplica solo al arrancar la aplicación, y `DataSeeder` carga los datos de ejemplo.

### 2. Configuración local

```bash
cd PichangaApp
cp application-local.yml.example application-local.yml   # en Windows: copy
```

Edita `application-local.yml` con el usuario y la contraseña de tu base de datos. Ese archivo está excluido por `.gitignore`, así que tus credenciales no se suben al repositorio.

### 3. Ejecutar

```bat
cd PichangaApp
mvnw.cmd spring-boot:run
```

```bash
cd PichangaApp
./mvnw spring-boot:run
```

La aplicación queda en <http://localhost:8081>.

### 4. Credenciales de demostración

| Rol | Correo | Contraseña |
| --- | --- | --- |
| Administrador | `admin@pichangaapp.pe` | `Admin123!` |
| Propietario | `juan.perez@pichangaapp.pe` | `Dueno123!` |
| Propietario | `maria.rodriguez@pichangaapp.pe` | `Dueno123!` |
| Jugador | `carlos@pichangaapp.pe` | `Jugador123!` |
| Jugador | `luis@pichangaapp.pe` | `Jugador123!` |
| Jugador | `andrea@pichangaapp.pe` | `Jugador123!` |
| Jugador | `diego@pichangaapp.pe` | `Jugador123!` |

Son credenciales de demostración, pensadas para desarrollo local. En un entorno real hay que desactivar la carga de datos con `APP_DEMO_SEED=false` y definir contraseñas propias.

## Variables de entorno

| Variable | Por defecto | Descripción |
| --- | --- | --- |
| `DB_URL` | `jdbc:sqlserver://localhost:1433;databaseName=PichangaApp;...` | URL JDBC de la base de datos |
| `DB_USER` | `pichanga_app` | Usuario de base de datos |
| `DB_PASSWORD` | *(vacío)* | Contraseña de base de datos (en local se usa `application-local.yml`) |
| `PORT` | `8081` | Puerto HTTP |
| `APP_DEMO_SEED` | `true` | Carga usuarios, canchas y pichangas de ejemplo al iniciar |
| `APP_SEGURIDAD_REMEMBER_ME_KEY` | UUID aleatorio | Clave de firma de "recordarme"; fíjala en producción |

## Pruebas

```bat
cd PichangaApp
mvnw.cmd test
```

25 pruebas de unidad y de capa web (Mockito y `@WebMvcTest`). No necesitan base de datos ni levantar el contexto completo de la aplicación.

```bat
mvnw.cmd -DskipTests package
```

Genera `PichangaApp/target/pichangaapp.jar`, ejecutable con `java -jar`.

## Estructura del repositorio

```
.
├── PichangaApp/                     # proyecto Maven
│   ├── src/main/java/pe/pichangaapp/
│   │   ├── config/                  # seguridad, seed de datos, tareas programadas
│   │   ├── controller/              # controladores MVC por área
│   │   ├── dao/                     # repositorios Spring Data JPA y especificaciones
│   │   ├── domain/                  # entidades y enums del modelo
│   │   ├── dto/                     # formularios y view models
│   │   ├── security/                # UserDetails, listener y success handler
│   │   └── service/                 # lógica de negocio (interfaces + impl)
│   ├── src/main/resources/
│   │   ├── templates/               # vistas Thymeleaf por módulo
│   │   ├── static/                  # CSS, JS, imágenes y librerías vendorizadas
│   │   ├── schema.sql               # esquema idempotente de la base de datos
│   │   └── application.yml          # configuración base
│   ├── src/test/java/               # 25 pruebas
│   ├── database/init-db.sql         # creación de base, login y permisos
│   └── pom.xml
├── docker-compose.yml               # SQL Server 2022 para desarrollo
├── DESIGN.md                        # sistema de diseño (tokens, tipografía, color)
└── PRODUCT.md                       # contexto de producto y alcance
```

## Modelo de datos

Diez tablas: `usuario`, `cancha`, `horario`, `bloqueo_horario`, `reserva`, `pago`, `pichanga`, `pichanga_participante`, `calificacion` y `notificacion`.

## Licencia

Distribuido bajo licencia MIT. Consulta [LICENSE](LICENSE).
