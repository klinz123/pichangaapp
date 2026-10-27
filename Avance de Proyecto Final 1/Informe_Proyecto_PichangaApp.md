# PICHANGAAPP — INFORME TÉCNICO DEL PROYECTO

**CURSO:** Curso Integrador I: Sistemas Software
**ENTREGABLE:** Avance de Proyecto Final 1 — Documento Completo
**AÑO:** 2026

**INTEGRANTES**
- Marizza Guia Rodriguez
- Segundo Teofilo Reyes de la Cruz
- Juan Fabricio Guerra Pérez
- Erick Moran Quinde
- Jairo Sebastian Ore Tamara

**DOCENTE:** Martin Teobaldo Rey Romero

\newpage

# ÍNDICE

- INTRODUCCIÓN
- JUSTIFICACIÓN
- OBJETIVO GENERAL
- OBJETIVOS ESPECÍFICOS
- 1. ANÁLISIS DEL CONTEXTO DE LA EMPRESA
  - 1.1. Descripción de la empresa
  - 1.2. Descripción del problema
  - 1.3. Visión
  - 1.4. Misión
  - 1.5. Entorno de la empresa
  - 1.6. Estrategia de la empresa
  - 1.7. Planes de la empresa
  - 1.8. Gráfica de análisis del modelo de negocio
  - 1.9. Lista de requerimientos funcionales
  - 1.10. Lista de requerimientos no funcionales
- 2. PLANTEAMIENTO ALTERNATIVAS DE SOLUCIÓN
  - 2.1. Primera Alternativa
  - 2.2. Segunda Alternativa
  - 2.3. Tercera Alternativa
- 3. DISEÑO DE LA SOLUCIÓN
- 4. PROTOTIPOS
- 5. PANTALLAZOS WEB
- 6. DISEÑO ARQUITECTÓNICO
  - 6.1. Patrón MTV
  - 6.2. Patrón DAO
- 7. GESTIÓN DE DEPENDENCIAS
- 8. IMPLEMENTACIÓN DE SOFTWARE
- 9. CONTROL DE VERSIONES
- 10. INTEGRACIÓN DE CHATBOT CON IA
- 11. INTEGRACIÓN DE DASHBOARD
- 12. REGLAS DE NEGOCIO
- 13. POLÍTICAS DE LA EMPRESA
- ANEXO A: PRESUPUESTO DETALLADO

\newpage

# INTRODUCCIÓN

**PichangaApp** es una plataforma web tipo *marketplace* diseñada para facilitar la búsqueda, reserva y pago de canchas deportivas destinadas al fútbol amateur. La solución centraliza en un solo sistema la información relativa a canchas, horarios, reservas y actividades deportivas, eliminando los problemas de coordinación que actualmente ocurren mediante llamadas telefónicas, redes sociales y aplicaciones de mensajería.

El proyecto surge de la necesidad real detectada en el mercado peruano, donde los jugadores de fútbol amateur invierten gran cantidad de tiempo en coordinar espacios de juego, mientras los propietarios de canchas carecen de herramientas digitales para administrar eficientemente sus instalaciones. PichangaApp actúa como puente entre ambos actores, ofreciendo:

- **Para los jugadores:** un catálogo centralizado de canchas, disponibilidad en tiempo real, reservas con pago digital (Yape, Plin, Mercado Pago, PayPal) y la posibilidad de organizar o unirse a *pichangas*.
- **Para los propietarios:** un panel de administración que permite gestionar horarios, reservas, ingresos y estadísticas de uso.

El presente documento constituye el **Avance de Proyecto Final 1** y reúne el análisis del contexto de la empresa, el planteamiento de alternativas de solución, el diseño de la solución, los prototipos, la arquitectura técnica, el plan de implementación, la gestión de versiones, las integraciones de chatbot y dashboard, las reglas de negocio, las políticas de la empresa y un presupuesto detallado ajustado a la restricción económica del proyecto.

# JUSTIFICACIÓN

## Justificación social

El fútbol es el deporte más practicado en el Perú. Sin embargo, la organización de partidos amateurs presenta una marcada ineficiencia: los jugadores dependen de cadenas de mensajes, llamadas y redes sociales para conocer la disponibilidad de una cancha. Este modelo genera:

- **Pérdida de tiempo:** múltiples contactos para verificar un solo horario.
- **Información desactualizada:** horarios que aparecen libres pero ya fueron reservados.
- **Conflictos de horarios:** doble reserva del mismo bloque por falta de un registro único.
- **Desconfianza:** ausencia de un historial verificable de reservas y pagos.

## Justificación económica

- Para los **jugadores**, la plataforma reduce el tiempo de coordinación, que puede cuantificarse en horas semanales que pueden dedicarse a la actividad deportiva en sí.
- Para los **propietarios**, la digitalización permite optimizar la ocupación de sus canchas, aumentar los ingresos por reducción de huecos vacíos y acceder a un mercado más amplio.
- Para **PichangaApp**, el modelo de negocio genera ingresos a través de una comisión por transacción y planes de suscripción premium para propietarios.

## Justificación tecnológica

El proyecto aprovecha tecnologías modernas y accesibles: una aplicación web con **React 19** en el frontend, **Spring Boot 3** con **Hibernate** en el backend, y **SQL Server** como gestor de base de datos. Esta combinación ofrece:

- **Escalabilidad:** capacidad de crecimiento progresivo a 1,000+ usuarios concurrentes.
- **Mantenibilidad:** separación clara de capas (DAO, servicios, controladores, componentes).
- **Seguridad:** autenticación JWT, cifrado de contraseñas con BCrypt y cumplimiento de la Ley N° 29733 de Protección de Datos Personales.
- **Integración de pagos:** soporte para billeteras digitales locales (Yape, Plin) e internacionales (Mercado Pago, PayPal).

# OBJETIVO GENERAL

Desarrollar una plataforma web tipo *marketplace* que permita a los jugadores de fútbol amateur buscar, reservar y pagar canchas deportivas, así como organizar y participar en pichangas, proporcionando a los propietarios herramientas para administrar sus canchas, horarios y reservas de manera eficiente, dentro de un presupuesto máximo de **S/ 30,000** y un plazo de **16 semanas**.

# OBJETIVOS ESPECÍFICOS

| N° | Objetivo específico |
|----|---------------------|
| 1 | Implementar un sistema de **registro e inicio de sesión** para jugadores y propietarios, con autenticación segura (JWT + BCrypt) y opción de verificación de identidad. |
| 2 | Permitir la **búsqueda de canchas** según ubicación, fecha, horario, superficie y precio, con actualización de disponibilidad en tiempo real. |
| 3 | Facilitar la **reserva de canchas** y la gestión de los horarios disponibles, con confirmación automática y bloqueo de horarios ocupados. |
| 4 | Integrar **medios de pago digitales** (Yape, Plin, Mercado Pago, PayPal) para gestionar los pagos de las reservas de forma segura. |
| 5 | Permitir a los jugadores **publicar y unirse a pichangas** según los cupos disponibles, con control de conflictos de horario. |
| 6 | Implementar **notificaciones** (email, push, SMS) relacionadas con reservas y partidos programados. |
| 7 | Permitir la **calificación de las canchas** utilizadas por los jugadores mediante un sistema de estrellas y comentarios. |
| 8 | Proporcionar a los propietarios **herramientas de administración** para gestionar canchas, reservas e ingresos, incluyendo reportes exportables. |
| 9 | Mantener un **historial de reservas** de los usuarios, accesible desde su perfil. |
| 10 | Garantizar la **seguridad y protección de datos** de los usuarios, cumpliendo la normativa peruana e implementando respaldos automáticos diarios. |

\newpage

# 1. ANÁLISIS DEL CONTEXTO DE LA EMPRESA

## 1.1. Descripción de la empresa

**PichangaApp** es una empresa tecnológica enfocada en desarrollar soluciones digitales para el sector deportivo amateur. Su principal producto es una plataforma web tipo *marketplace* que conecta a jugadores de fútbol con propietarios de canchas deportivas.

| Atributo | Descripción |
|----------|-------------|
| **Razón social** | PichangaApp S.A.C. (proyección) |
| **Sector** | Tecnología / Deportes / Servicios digitales |
| **Mercado** | Perú (inicio: Lima) → Expansión: Latinoamérica |
| **Producto principal** | Plataforma web de reserva de canchas y organización de pichangas |
| **Modelo de ingresos** | Comisión por transacción + suscripción premium para propietarios |
| **Propuesta de valor** | Centralizar la búsqueda, reserva y pago de canchas deportivas en una única plataforma, ofreciendo disponibilidad en tiempo real y pagos digitales |

La empresa busca facilitar la búsqueda y reserva de espacios deportivos mediante una plataforma centralizada que permita consultar disponibilidad, realizar reservas, efectuar pagos digitales y participar en pichangas. Para los propietarios, PichangaApp ofrece herramientas destinadas a administrar sus canchas, horarios, reservas e ingresos.

## 1.2. Descripción del problema

Actualmente, los jugadores de fútbol amateur enfrentan dificultades para encontrar canchas disponibles y realizar reservas de manera rápida:

- **Información dispersa:** La disponibilidad se comunica por redes sociales, llamadas telefónicas o mensajes de texto, sin un registro centralizado.
- **Demoras en la coordinación:** Se necesitan múltiples contactos para confirmar un solo horario.
- **Errores de coordinación:** Es frecuente la doble reserva de un mismo bloque horario.
- **Conflictos de horarios:** La falta de actualización en tiempo real genera reservas superpuestas.
- **Pagos incómodos:** La mayoría de las reservas se pagan en efectivo en el lugar, sin comprobante digital ni historial.

Por otro lado, muchos propietarios de canchas carecen de herramientas digitales para:

- Gestionar su parrilla de horarios en tiempo real.
- Controlar la disponibilidad de sus instalaciones.
- Llevar un registro de reservas e ingresos.
- Visualizar estadísticas de ocupación.

PichangaApp busca solucionar esta problemática centralizando la información de las canchas deportivas en una única plataforma que permita consultar disponibilidad, realizar reservas y efectuar pagos digitales de forma segura y eficiente.

## 1.3. Visión

Ser la plataforma líder en reservas de canchas deportivas en el Perú, facilitando la conexión entre jugadores y propietarios mediante soluciones tecnológicas innovadoras que optimicen la experiencia deportiva.

## 1.4. Misión

Brindar una plataforma digital confiable, accesible y segura que permita a jugadores y propietarios gestionar reservas de canchas deportivas, optimizar tiempos de coordinación y realizar pagos electrónicos de manera sencilla.

## 1.5. Entorno de la empresa

### 1.5.1. Usuarios

| Usuario | Descripción |
|---------|-------------|
| Jugadores de fútbol amateur | Personas que buscan canchas para jugar de forma casual o regular. |
| Organizadores de pichangas | Jugadores que crean y administran eventos deportivos con cupos. |
| Propietarios de canchas deportivas | Dueños de complejos deportivos que ofrecen sus instalaciones. |
| Administrador del sistema | Personal interno que gestiona usuarios, canchas y supervisa el marketplace. |

### 1.5.2. Hardware

| Recurso | Descripción |
|---------|-------------|
| Smartphones Android | Acceso a la plataforma web desde dispositivos móviles Android. |
| Smartphones iOS | Acceso a la plataforma web desde dispositivos iOS. |
| Computadoras con Internet | Acceso desde equipos de escritorio y portátiles. |
| Servidores web | Alojamiento de la aplicación y la base de datos en producción. |

### 1.5.3. Software

| Recurso | Descripción |
|---------|-------------|
| Aplicación web PichangaApp | Plataforma principal desarrollada con React + Spring Boot. |
| Base de datos SQL Server | Gestor de base de datos relacional. |
| Integración con pasarelas de pago | Yape, Plin, Mercado Pago, PayPal. |
| Navegadores web modernos | Chrome, Firefox, Edge, Safari (últimas versiones). |

### 1.5.4. Restricciones del entorno

| Restricción | Detalle |
|-------------|---------|
| Desarrollo | Únicamente en entorno web (sin apps nativas en esta fase). |
| Presupuesto | Máximo **S/ 30,000**. |
| Tiempo | 16 semanas de desarrollo. |
| Conectividad | Dependencia de conexión a Internet para el funcionamiento completo. |

## 1.6. Estrategia de la empresa

La estrategia de PichangaApp se basa en diferenciarse mediante la **centralización** de los servicios relacionados con la reserva de canchas y la organización de pichangas en una única plataforma digital.

| Estrategia | Descripción |
|------------|-------------|
| **Facilidad de uso** | Ofrecer una plataforma intuitiva que permita encontrar y reservar una cancha en pocos pasos (máx. 3 clics). |
| **Disponibilidad actualizada** | Proporcionar información de horarios en tiempo real para reducir conflictos de reservas. |
| **Digitalización de pagos** | Facilitar las transacciones mediante Yape, Plin, Mercado Pago y PayPal. |
| **Interacción entre usuarios** | Permitir la creación y participación en pichangas para fomentar la formación de partidos. |
| **Beneficio para propietarios** | Herramientas para administrar horarios, reservas e ingresos. |
| **Experiencia del usuario** | Notificaciones, historial y calificaciones para mejorar la confianza y satisfacción. |

## 1.7. Planes de la empresa

| Plazo | Plan |
|-------|------|
| **Corto plazo** (Semanas 1–8) | Desarrollar e implementar la primera versión de la plataforma web: registro de usuarios, búsqueda de canchas, reservas, gestión de horarios y pagos digitales. |
| **Mediano plazo** (Semanas 9–16) | Incrementar la cantidad de canchas y propietarios registrados, mejorar las funcionalidades de pichangas y desarrollar herramientas avanzadas de administración de reservas e ingresos. |
| **Largo plazo** (Post‑lanzamiento) | Expandir PichangaApp a diferentes ciudades del Perú, incrementar usuarios y propietarios afiliados y consolidar la plataforma como alternativa líder para la reserva de canchas deportivas. |

## 1.8. Gráfica de análisis del modelo de negocio

> **Nota para el informe:** insertar aquí el diagrama del **Lean Canvas** o **Business Model Canvas** (BMC). Se recomienda generarlo con la herramienta *Canvanizer* o *Miro* e insertar la imagen.

### Resumen del Business Model Canvas (PichangaApp)

| Bloque | Descripción |
|--------|-------------|
| **Segmentos de clientes** | Jugadores de fútbol amateur, organizadores de pichangas, propietarios de canchas. |
| **Propuesta de valor** | Reserva rápida y segura de canchas, disponibilidad en tiempo real, pagos digitales, organización de pichangas. |
| **Canales** | Web (aplicación), redes sociales, alianzas con complejos deportivos. |
| **Relación con clientes** | Soporte automatizado (chatbot IA), notificaciones push, historial personalizado. |
| **Fuentes de ingresos** | Comisión por reserva (10%), suscripción premium para propietarios, publicidad segmentada. |
| **Actividades clave** | Desarrollo y mantenimiento de la plataforma, gestión de alianzas con canchas, soporte al usuario. |
| **Recursos clave** | Equipo de desarrollo (5 personas), servidores, base de datos SQL Server, integraciones de pago. |
| **Socios clave** | Propietarios de canchas, pasarelas de pago (Yape, Plin, Mercado Pago, PayPal), proveedores de hosting. |
| **Estructura de costos** | Desarrollo de software, hosting y servidores, licencias, marketing digital, soporte. |

## 1.9. Lista de requerimientos funcionales

| ID | Requisito funcional | Descripción |
|----|---------------------|-------------|
| RF01 | Registro de usuarios | El sistema permitirá a los usuarios registrarse mediante correo electrónico y contraseña. |
| RF02 | Inicio de sesión | El sistema permitirá a los usuarios acceder a la plataforma mediante credenciales válidas. |
| RF03 | Gestión de perfil | El usuario podrá visualizar y actualizar sus datos de cuenta. |
| RF04 | Búsqueda de canchas | El usuario podrá buscar canchas disponibles según ubicación, fecha y horario. |
| RF05 | Reserva de canchas | El usuario podrá reservar una cancha seleccionando fecha y hora disponible. |
| RF06 | Publicación de pichangas | El usuario podrá crear eventos deportivos o pichangas para invitar jugadores. |
| RF07 | Unión a pichangas | Los usuarios podrán inscribirse en pichangas publicadas por otros usuarios. |
| RF08 | Gestión de pagos | El sistema permitirá registrar y confirmar pagos de reservas (Yape, Plin, Mercado Pago, PayPal). |
| RF09 | Notificaciones | El sistema enviará recordatorios sobre reservas y partidos programados. |
| RF10 | Calificación de canchas | Los usuarios podrán valorar y comentar sobre las canchas utilizadas. |
| RF11 | Historial de reservas | El usuario podrá consultar las reservas realizadas anteriormente. |
| RF12 | Administración de canchas | El administrador/propietario podrá registrar, modificar y eliminar información de las canchas. |

## 1.10. Lista de requerimientos no funcionales

| ID | Requisito no funcional | Descripción | Métrica / Criterio de aceptación |
|----|------------------------|-------------|----------------------------------|
| RNF01 | Seguridad | Proteger la información de los usuarios. | Autenticación segura (JWT) y cifrado de contraseñas (BCrypt). |
| RNF02 | Disponibilidad | El sistema debe estar accesible para los usuarios. | Disponibilidad mínima del 99%. |
| RNF03 | Rendimiento | Las consultas deben ejecutarse rápidamente. | Tiempo de respuesta menor a 3 segundos. |
| RNF04 | Usabilidad | La aplicación debe ser intuitiva. | Los usuarios deben completar una reserva sin capacitación previa. |
| RNF05 | Compatibilidad | Funcionamiento en distintos dispositivos. | Compatible con Android, iOS y navegadores modernos. |
| RNF06 | Escalabilidad | Soportar crecimiento de usuarios. | Soportar al menos 1,000 usuarios concurrentes. |
| RNF07 | Mantenibilidad | Facilitar futuras mejoras y correcciones. | Código documentado y modular (cobertura de tests ≥ 80%). |
| RNF08 | Confiabilidad | Garantizar la integridad de los datos de reservas. | No perder información ante fallos del sistema. |
| RNF09 | Accesibilidad | Facilitar el acceso para diferentes tipos de usuarios. | Interfaz adaptable y legible en dispositivos móviles. |
| RNF10 | Respaldo de información | Mantener copias de seguridad. | Backups automáticos diarios de la base de datos (retención 30 días). |

\newpage

# 2. PLANTEAMIENTO ALTERNATIVAS DE SOLUCIÓN

Para el desarrollo de PichangaApp se evaluaron tres alternativas de solución, analizando criterios de coste, tiempo, rendimiento, mantenibilidad y experiencia de usuario.

## 2.1. Primera Alternativa: Aplicación híbrida (React Native + Capacitor)

### Descripción
Desarrollar una aplicación híbrida que se compila a aplicaciones nativas (Android/iOS) y también a una **Progressive Web App (PWA)**, compartiendo una base de código única en **React Native** con **Capacitor**.

### Ventajas
- Reutilización del código (~70% de lógica compartida).
- Despliegue simultáneo en tiendas de aplicaciones y web.
- Acceso a funcionalidades nativas (cámara, GPS) mediante plugins.
- Menor coste que desarrollar dos apps nativas separadas.

### Desventajas
- Rendimiento inferior a aplicaciones 100% nativas en casos gráficos intensivos.
- Dependencia del ecosistema de plugins para nuevas funcionalidades nativas.
- Mayor complejidad de configuración del entorno de compilación.

### Estimación
| Criterio | Valor |
|----------|-------|
| Coste estimado | S/ 45,000 – 55,000 |
| Tiempo | 20–22 semanas |
| Riesgo | Medio |
| **Fuera de presupuesto** | **Sí (supera los S/ 30,000)** |

## 2.2. Segunda Alternativa: Aplicación web responsive + API REST

### Descripción
Desarrollar una **aplicación web responsive** (React 19 + TypeScript) que consuma una **API REST** construida con **Spring Boot 3 + Hibernate**, con **SQL Server** como base de datos. Esta alternativa es la que mejor se alinea con el alcance y presupuesto del proyecto.

### Ventajas
- Desarrollo rápido y enfocado (cumple con las 16 semanas).
- Fácil escalabilidad horizontal en la nube.
- La API REST puede ser reutilizada por futuras apps móviles.
- Menor coste de infraestructura y mantenimiento.
- Compatibilidad universal con cualquier navegador y dispositivo.

### Desventajas
- Las funcionalidades offline requieren tecnología adicional (Service Workers).
- No accede directamente a APIs nativas (GPS avanzado, notificaciones push nativas requieren PWA o wrappers).

### Estimación
| Criterio | Valor |
|----------|-------|
| Coste estimado | S/ 24,000 – 28,000 |
| Tiempo | 14–16 semanas |
| Riesgo | Bajo |
| **Dentro de presupuesto** | **Sí (cumple con los S/ 30,000)** |

## 2.3. Tercera Alternativa: Aplicación nativa separada (Kotlin + Swift)

### Descripción
Desarrollar dos aplicaciones nativas independientes: una en **Kotlin** (Android) y otra en **Swift** (iOS), cada una con su propia interfaz y lógica.

### Ventajas
- Máximo rendimiento y uso de APIs específicas de cada sistema operativo.
- Experiencia de usuario diferenciada y optimizada por plataforma.
- Acceso directo a todas las funcionalidades nativas.

### Desventajas
- Duplicación de esfuerzos de desarrollo (≈ 2× el tiempo).
- Mayor coste de mantenimiento y actualizaciones simultáneas.
- No cumple con la restricción de tiempo de 16 semanas.

### Estimación
| Criterio | Valor |
|----------|-------|
| Coste estimado | S/ 65,000 – 80,000 |
| Tiempo | 28–32 semanas |
| Riesgo | Alto |
| **Fuera de presupuesto** | **Sí (supera ampliamente los S/ 30,000)** |

## Conclusión de la evaluación de alternativas

| Criterio | Alternativa 1 (Híbrida) | Alternativa 2 (Web + API) | Alternativa 3 (Nativa) |
|----------|------------------------|---------------------------|------------------------|
| Coste | S/ 45,000–55,000 ❌ | S/ 24,000–28,000 ✅ | S/ 65,000–80,000 ❌ |
| Tiempo | 20–22 sem ❌ | 14–16 sem ✅ | 28–32 sem ❌ |
| Rendimiento | Medio | Alto | Máximo |
| Mantenibilidad | Media | Alta | Baja (2 códigos) |
| Compatibilidad | Alta | Alta | Alta |
| **Veredicto** | Descartada | **ELEGIDA** | Descartada |

**La Segunda Alternativa (Aplicación web responsive + API REST) es la elegida**, pues cumple con todas las restricciones del proyecto (presupuesto S/ 30,000 y 16 semanas) y proporciona una base sólida y escalable para futuras expansiones.

\newpage

# 3. DISEÑO DE LA SOLUCIÓN

## 3.1. Arquitectura de alto nivel

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENTE                                      │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐            │
│  │ Navegador Web │  │ Smartphone    │  │ Tablet        │            │
│  │ (React 19)    │  │ (Android/iOS) │  │               │            │
│  └───────┬───────┘  └───────┬───────┘  └───────┬───────┘            │
└──────────┼──────────────────┼──────────────────┼────────────────────┘
           │        HTTPS / JSON (REST API)       │
           ▼──────────────────┼──────────────────▼
┌─────────────────────────────────────────────────────────────────────┐
│                         BACKEND (SPRING BOOT)                       │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐     │
│  │ Auth       │  │ Canchas    │  │ Reservas   │  │ Pichangas  │     │
│  │ Controller │  │ Controller │  │ Controller │  │ Controller │     │
│  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘     │
│  ┌─────┴───────────────┴───────────────┴───────────────┴─────────┐  │
│  │                     SERVICIOS (Business Logic)                │  │
│  └─────┬───────────────┬───────────────┬───────────────┬─────────┘  │
│  ┌─────┴──────┐  ┌─────┴──────┐  ┌─────┴──────┐  ┌─────┴──────┐    │
│  │ UsuarioDAO │  │ CanchaDAO  │  │ ReservaDAO │  │ PagoDAO    │    │
│  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘    │
└────────┼───────────────┼───────────────┼───────────────┼────────────┘
         ▼               ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    BASE DE DATOS SQL SERVER                          │
│  usuario │ cancha │ reserva │ pago │ pichanga │ notificacion        │
└─────────────────────────────────────────────────────────────────────┘
```

## 3.2. Modelo de datos (entidades principales)

| Entidad | Atributos principales | Relaciones |
|---------|----------------------|------------|
| `usuario` | id, nombre, email, password_hash, tipo (jugador/propietario/admin), telefono, verificado | 1:N reserva, 1:N cancha, 1:N pichanga |
| `cancha` | id, propietario_id, nombre, direccion, distrito, superficie, precio_hora, horario_apertura, horario_cierre, latitud, longitud | N:1 propietario, 1:N reserva, 1:N pichanga |
| `reserva` | id, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado, precio_total | N:1 usuario, N:1 cancha, 1:1 pago |
| `pago` | id, reserva_id, metodo (yape/plin/mercadopago/paypal), monto, estado, transaccion_id_externa | 1:1 reserva |
| `pichanga` | id, organizador_id, cancha_id, fecha, hora, cupos_total, cupos_ocupados, nivel, descripcion | N:1 organizador, N:1 cancha, 1:N participantes |
| `calificacion` | id, usuario_id, cancha_id, puntaje (1-5), comentario, fecha | N:1 usuario, N:1 cancha |
| `notificacion` | id, usuario_id, tipo, contenido, leida, fecha_creacion | N:1 usuario |

### Script SQL de ejemplo (SQL Server)

```sql
CREATE DATABASE PichangaApp;
GO

USE PichangaApp;
GO

CREATE TABLE usuario (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('jugador','propietario','admin')),
    telefono VARCHAR(20),
    verificado BIT DEFAULT 0,
    fecha_registro DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE cancha (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    propietario_id BIGINT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    direccion VARCHAR(200),
    distrito VARCHAR(80),
    superficie VARCHAR(30) CHECK (superficie IN ('cesped_sintetico','cesped_natural','otro')),
    precio_hora DECIMAL(10,2) NOT NULL,
    horario_apertura TIME,
    horario_cierre TIME,
    latitud DECIMAL(10,7),
    longitud DECIMAL(10,7),
    FOREIGN KEY (propietario_id) REFERENCES usuario(id)
);

CREATE TABLE reserva (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    cancha_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) DEFAULT 'pendiente' CHECK (estado IN ('pendiente','confirmada','pagada','cancelada')),
    precio_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);

CREATE TABLE pago (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    reserva_id BIGINT NOT NULL,
    metodo VARCHAR(20) NOT NULL CHECK (metodo IN ('yape','plin','mercadopago','paypal')),
    monto DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'pendiente' CHECK (estado IN ('pendiente','pagado','rechazado','reembolsado')),
    transaccion_id_externa VARCHAR(255),
    fecha_pago DATETIME2,
    FOREIGN KEY (reserva_id) REFERENCES reserva(id)
);

CREATE TABLE pichanga (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    organizador_id BIGINT NOT NULL,
    cancha_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    cupos_total INT NOT NULL,
    cupos_ocupados INT DEFAULT 0,
    nivel VARCHAR(30),
    descripcion TEXT,
    FOREIGN KEY (organizador_id) REFERENCES usuario(id),
    FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);
```

## 3.3. Seguridad del diseño

- **Autenticación:** JWT con access token (15 min) y refresh token (7 días).
- **Contraseñas:** cifradas con BCrypt (factor de trabajo 12).
- **Autorización:** control por roles (jugador, propietario, admin) con Spring Security.
- **Validación de datos:** Bean Validation (Jakarta Validation) en el backend.
- **Prevención de doble reserva:** bloqueo optimista (`@Version`) y restricción de unicidad en BD (`UNIQUE (cancha_id, fecha, hora_inicio)`).
- **Cumplimiento legal:** Ley N° 29733 (Perú), datos de pago nunca almacenados en texto plano.

\newpage

# 4. PROTOTIPOS

Se definen **tres prototipos** en Figma. Los dos primeros representan **soluciones simples** del proyecto; el tercero es el **prototipo definitivo** que se usará como base de la implementación y para el informe final.

## 4.1. Prototipo 1 — Solución simple: Registro + Búsqueda básica

> **Objetivo:** Validar las pantallas mínimas de entrada al sistema.

| Pantalla | Descripción | Componentes |
|----------|-------------|-------------|
| Login / Registro | Formulario de correo y contraseña, enlace "¿Olvidaste tu contraseña?". | `Input`, `Button`, `Link` |
| Catálogo de canchas | Listado simple con tarjetas: nombre, ubicación, precio/hora, foto. | `Card`, `Image`, `Text`, `Icon` |
| Filtro rápido | Dos selects: "Distrito" y "Superficie". | `Select`, `Option` |
| Detalle de cancha | Vista expandida con imagen, horarios y botón "Reservar". | `Modal`, `Button` |

**Flujo de usuario:**
1. El usuario entra → Login/Registro.
2. Accede al catálogo → aplica filtro (distrito + superficie).
3. Selecciona una tarjeta → ver detalle → clic en "Reservar".

**Componentes reutilizables:** `Header`, `Footer`, `SearchBar`.

## 4.2. Prototipo 2 — Solución simple: Listado de canchas + Gestión de horarios (propietario)

> **Objetivo:** Validar la vista del propietario y la administración de disponibilidad.

| Pantalla | Descripción | Componentes |
|----------|-------------|-------------|
| Dashboard propietario | Tarjetas: "Reservas hoy", "Ingresos mes", "Canchas activas". | `Card`, `StatisticBox` |
| Parrilla de horarios | Tabla interactiva: hora, estado (libre/reservado/ocupado), botón "Bloquear/Liberar". | `Table`, `Button`, `Badge` |
| Añadir/editar horarios | Formulario modal: hora inicio, hora fin, disponibilidad. | `Input (time)`, `Select`, `Modal` |
| Historial de reservas | Lista de reservas pasadas con filtro de fecha. | `Table`, `DatePicker` |

**Flujo de usuario (propietario):**
1. Inicia sesión → Dashboard.
2. Clic en "Gestionar horarios" → ver parrilla.
3. Selecciona un hueco → "Bloquear" → cambio de color y etiqueta.
4. Consulta historial → exportar CSV (simulación).

**Componentes reutilizables:** `Sidebar`, `StatCard`.

## 4.3. Prototipo 3 — Prototipo definitivo: Flujo completo de reserva y pago

> **Objetivo:** Validar el flujo integral de búsqueda → reserva → pago → confirmación → notificación. **Es el prototipo que se implementará.**

| Pantalla | Descripción | Componentes |
|----------|-------------|-------------|
| 1. Home | Logo, mensaje "Encuentra y reserva tu cancha". | `Hero`, `Button` |
| 2. Búsqueda avanzada | Mapa interactivo (Leaflet), filtros: distrito, fecha, hora, superficie, precio máx, valoración. | `Map`, `Select`, `RangeInput`, `StarRating` |
| 3. Resultado de canchas | Tarjetas con foto, datos clave, botón "Ver horarios". | `Card`, `Button` |
| 4. Horario y disponibilidad | Calendario (FullCalendar) + rejilla horaria con estado por celda. | `Calendar`, `TimeGrid`, `Cell` |
| 5. Resumen de reserva | Cancha, fecha+hora, precio total, método de pago. | `SummaryCard`, `RadioGroup` |
| 6. Confirmación de pago | Redirección a QR Yape/Plin o formulario Mercado Pago; "Procesando...". | `QRCode`, `Button`, `Spinner` |
| 7. Confirmación final | Número de reserva, botón "Ir a mi perfil". | `Alert success`, `Link` |
| 8. Notificación push | "Tu reserva está confirmada para mañana 18:00 h". | `Banner push` |

**Flujo de usuario completo:**
1. **Home** → Búsqueda avanzada (filtrar por ubicación y fecha).
2. Seleccionar cancha → Horario y disponibilidad (elegir hueco libre).
3. Resumen de reserva (revisar datos, elegir pago).
4. Pago (QR o formulario).
5. Confirmación (número de reserva, botón "Ver perfil").
6. Push de recordatorio 24 h antes del partido.

**Componentes reutilizables (biblioteca de componentes):**

| Componente | Uso |
|------------|-----|
| `NavBar` | Barra de navegación con logo y menú. |
| `Footer` | Enlaces legales, redes sociales. |
| `CardCancha` | Tarjeta de cancha (foto, nombre, ubicación, precio, rating). |
| `TimeSlot` | Celda de parrilla horaria (libre/ocupado). |
| `PaymentMethodSelector` | Selector de método de pago. |
| `SuccessAlert` | Mensaje de reserva confirmada. |

**Interacciones clave en Figma:**
- Clic en "Reservar" → apertura del modal con animación "slide-up".
- Drag-and-drop de hueco seleccionado → actualización en tiempo real del badge.
- Validación en tiempo real del pago QR (botón "Confirmar" se habilita al escanear).
- Transición a notificación push con efecto "fade-out".

> **Cómo crear estos prototipos en Figma:** Abrir archivo "PichangaApp-Prototipos", crear 3 páginas con los nombres indicados, importar la biblioteca de componentes y conectar los frames con *prototype links* + *Auto-Animate*. Exportar cada página como PNG (300 dpi) o PDF para el informe.

\newpage

# 5. PANTALLAZOS WEB

> **Propósito:** Mostrar capturas de pantalla de los principales flujos a los que tendrá acceso el usuario final. Estas imágenes se generarán a partir de los prototipos de Figma (Prototipo 3) o de la implementación final.

| Flujo | Pantalla | Descripción |
|-------|----------|-------------|
| Página de inicio | Home | Mensaje de bienvenida y CTA "Buscar cancha". |
| Registro | Formulario de registro | Campos de correo y contraseña. |
| Inicio de sesión | Login | Acceso con credenciales. |
| Búsqueda | Catálogo con filtros | Listado de canchas con filtros de distrito, fecha, horario. |
| Detalle de cancha | Vista detallada | Foto, horarios, precio, botón reservar. |
| Reserva | Confirmación de horario | Selección de bloque horario. |
| Pago | Pasarela de pago | QR Yape/Plin o formulario Mercado Pago. |
| Confirmación | Reserva exitosa | Número de reserva, botón ver perfil. |
| Panel propietario | Dashboard | Estadísticas, horarios, reservas. |
| Perfil de usuario | Historial | Historial de reservas y calificaciones. |

> **Nota:** insertar aquí las imágenes exportadas desde Figma (`Figma → Export → PNG 300 dpi`).

\newpage

# 6. DISEÑO ARQUITECTÓNICO

## 6.1. Patrón MTV (Model-Template-View)

El patrón **MTV** (variante del MVC propuesta por Django) se aplica para separar la lógica de datos, la presentación y la plantilla de renderizado. En el contexto de PichangaApp:

| Capa | Componentes | Responsabilidad |
|------|-------------|-----------------|
| **Model** | Entidades JPA (`Usuario`, `Cancha`, `Reserva`, `Pago`, `Pichanga`) + Repositorios Spring Data JPA | Representa los datos y la lógica de persistencia. |
| **Template** | Fragmentos de HTML/Thymeleaf o componentes React reutilizables | Define la estructura visual reutilizable. |
| **View** | Controladores REST + Componentes React | Gestiona la interacción y la respuesta al usuario. |

### Flujo de datos típico (MTV)

```
1. Frontend solicita GET /api/canchas?distrito=Lima&fecha=2026-10-10
2. Controlador (View) recibe la petición
3. Invoca al repositorio (Model) para obtener los datos
4. El Model consulta SQL Server y devuelve la lista
5. El Controlador serializa a JSON y responde
6. El Frontend renderiza la Template (CardCancha) con los datos
```

**Ejemplo de implementación:**

```java
// Controller (View)
@RestController
@RequestMapping("/api/canchas")
public class CanchaController {

    @Autowired
    private CanchaRepository canchaRepository;

    @GetMapping
    public List<Cancha> buscar(@RequestParam String distrito) {
        return canchaRepository.findByDistrito(distrito);
    }
}
```

```tsx
// Template + View (React)
function Catalogo() {
  const [canchas, setCanchas] = useState([]);
  useEffect(() => {
    fetch('/api/canchas?distrito=Lima')
      .then(r => r.json())
      .then(setCanchas);
  }, []);
  return (
    <div>
      {canchas.map(c => <CardCancha key={c.id} cancha={c} />)}
    </div>
  );
}
```

## 6.2. Patrón DAO (Data Access Object)

El patrón **DAO** aísla la lógica de acceso a datos de la lógica de negocio, proporcionando una interfaz limpia para las operaciones de persistencia.

### Clases DAO principales

| DAO | Operaciones clave |
|-----|-------------------|
| `UsuarioDAO` | Crear, buscar por email, validar credenciales, actualizar perfil. |
| `CanchaDAO` | Buscar por distrito/filtros, obtener horarios, gestionar disponibilidad. |
| `ReservaDAO` | Crear reserva, consultar historial, actualizar estado, verificar disponibilidad. |
| `PagoDAO` | Registrar transacción, actualizar estado, validar transaccion_id externa. |
| `PichangaDAO` | Crear pichanga, unirse (descontar cupo), consultar disponibles. |

### Ventajas del patrón DAO

- **Separación de responsabilidades:** la capa de negocio no conoce los detalles de JDBC/JPA.
- **Testabilidad:** se pueden mockear los DAO en pruebas unitarias.
- **Flexibilidad:** facilita el cambio de proveedor de BD (SQL Server → PostgreSQL) sin afectar servicios.
- **Reutilización:** los DAO pueden ser compartidos por múltiples servicios.

### Implementación con Spring Data JPA

```java
// Interfaz DAO
public interface UsuarioDAO extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}

// Implementación con consulta nativa (SQL Server)
public interface ReservaDAO extends JpaRepository<Reserva, Long> {

    @Query(value = "SELECT * FROM reserva WHERE cancha_id = ?1 AND fecha = ?2 AND estado IN ('pendiente','confirmada','pagada')", nativeQuery = true)
    List<Reserva> findOcupadas(Long canchaId, LocalDate fecha);
}
```

\newpage

# 7. GESTIÓN DE DEPENDENCIAS

## 7.1. Dependencias del backend (Maven `pom.xml`)

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| `spring-boot-starter-web` | 3.4.x | Creación de la API REST. |
| `spring-boot-starter-data-jpa` | 3.4.x | Integración con Hibernate y JPA. |
| `spring-boot-starter-security` | 3.4.x | Autenticación y autorización. |
| `spring-boot-starter-validation` | 3.4.x | Validación de datos de entrada. |
| `spring-boot-starter-mail` | 3.4.x | Envío de notificaciones por email. |
| `hibernate-core` | 6.x | ORM (incluido por Spring Data JPA). |
| `mssql-jdbc` | 12.x | Driver JDBC de SQL Server. |
| `jjwt-api` / `jjwt-impl` / `jjwt-jackson` | 0.12.x | Generación y validación de JWT. |
| `springdoc-openapi-starter-webmvc-ui` | 2.6.x | Documentación automática de la API (Swagger UI). |
| `lombok` | 1.18.x | Reducción de código boilerplate. |
| `spring-boot-starter-test` | 3.4.x | Testing unitario y de integración. |
| `spring-security-test` | 3.4.x | Testing de seguridad. |

**Fragmento de `pom.xml`:**

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.6.1.jre11</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
```

## 7.2. Dependencias del frontend (React)

| Dependencia | Versión | Propósito |
|-------------|---------|-----------|
| `react` / `react-dom` | 19.x | Biblioteca principal de UI. |
| `react-router-dom` | 6.x / 7.x | Enrutamiento de la SPA. |
| `redux-toolkit` | 2.x | Gestión del estado global. |
| `axios` | 1.x | Cliente HTTP para la API. |
| `@mui/material` | 6.x | Componentes de interfaz. |
| `leaflet` | 1.9.x | Mapas interactivos. |
| `fullcalendar` | 6.x | Calendario de disponibilidad. |
| `react-hook-form` | 7.x | Gestión de formularios. |
| `yup` | 1.x | Validación de formularios. |
| `react-qr-code` | 2.x | Generación de QR para pagos. |
| `chart.js` | 4.x | Gráficos del dashboard. |

## 7.3. Infraestructura

| Recurso | Tecnología | Justificación |
|---------|------------|---------------|
| Base de datos | SQL Server 2019/2022 | Sistema gestor elegido para el proyecto. |
| Hosting frontend | Vercel / Netlify | Deploy rápido de SPA estática. |
| Hosting backend | Azure App Service / AWS Elastic Beanstalk | Escalabilidad y soporte de SQL Server. |
| CI/CD | GitHub Actions | Pipeline automático de build y test. |
| Monitoreo | Prometheus + Grafana | Métricas de rendimiento y disponibilidad. |

\newpage

# 8. IMPLEMENTACIÓN DE SOFTWARE

## 8.1. Fases de implementación

| Fase | Semanas | Actividades | Entregable |
|------|---------|-------------|------------|
| F1. Análisis | 1–4 | Lean Canvas, toma de requerimientos, casos de uso, prototipos. | Documento de requisitos + prototipos Figma. |
| F2. Diseño | 5–7 | Modelado BPM, diseño de BD, UX/UI, arquitectura. | Diagramas, script SQL, diseño Figma. |
| F3. Backend | 8–11 | Autenticación, módulo de canchas, reservas, pagos, pichangas. | API REST funcional (Spring Boot). |
| F4. Frontend | 11–12 | Implementación de pantallas del Prototipo 3. | Aplicación web React funcional. |
| F5. Integración | 12 | Conexión frontend-backend, integración de pagos (sandbox). | MVP integrado. |
| F6. QA | 13–14 | Pruebas funcionales, de seguridad, de rendimiento. | Reporte de calidad. |
| F7. Despliegue | 15 | Publicación en producción, configuración de dominio, SSL. | Plataforma en línea. |
| F8. Cierre | 16 | Monitoreo, mantenimiento preventivo, sustentación. | Proyecto entregado. |

## 8.2. Configuración del entorno de desarrollo

### `application.properties` (Spring Boot + SQL Server)

```properties
# =========================================================
#  DATASOURCE - SQL SERVER
# =========================================================
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=PichangaApp;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourStrongPassword
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# =========================================================
#  JPA / HIBERNATE
# =========================================================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20

# =========================================================
#  SECURITY / JWT
# =========================================================
app.jwt.secret=SECRETO_SUPER_SEGURO_CAMBIAR_EN_PRODUCCION
app.jwt.expiration-ms=900000       # 15 minutos (access)
app.jwt.refresh-expiration-ms=604800000  # 7 días (refresh)

# =========================================================
#  SERVER
# =========================================================
server.port=8080
```

### Comandos de inicialización

```bash
# Backend (Spring Boot)
mvn clean install
mvn spring-boot:run

# Frontend (React)
npm install
npm run dev
```

## 8.3. Equipo de desarrollo (5 integrantes)

| Rol | Responsabilidades |
|-----|-------------------|
| Integrante 1 (Líder/Backend) | Arquitectura, módulos de autenticación y reservas. |
| Integrante 2 (Backend) | Módulos de canchas, pagos, pichangas. |
| Integrante 3 (Frontend) | Interfaz de búsqueda, catálogo y detalle. |
| Integrante 4 (Frontend) | Dashboard propietario, reportes, perfil. |
| Integrante 5 (QA/Docs) | Pruebas, documentación, control de versiones, integraciones (chatbot, dashboard). |

\newpage

# 9. CONTROL DE VERSIONES

## 9.1. Estrategia de ramas (Git Flow)

```
main (producción)
 ├── release/ (candidatos a producción)
 ├── develop (integración)
 │    ├── feature/auth
 │    ├── feature/reservas
 │    ├── feature/pagos
 │    ├── feature/pichangas
 │    └── feature/dashboard
 └── hotfix/ (correcciones urgentes)
```

## 9.2. Flujo de trabajo

| Etapa | Descripción |
|-------|-------------|
| 1. Crear rama | `git checkout -b feature/reserva-pago` desde `develop`. |
| 2. Commits convencionales | `feat:`, `fix:`, `docs:`, `test:`, `refactor:` |
| 3. Push | `git push origin feature/reserva-pago` |
| 4. Pull Request | Abrir PR hacia `develop`, con 2 reviewers. |
| 5. CI automático | GitHub Actions ejecuta tests y build. |
| 6. Merge | Tras aprobación, merge a `develop`. |
| 7. Release | Al finalizar hitos, merge a `main` y crear tag `v1.0.0`. |

## 9.3. Ejemplo de mensajes de commit

```
feat: add JWT authentication flow
fix: prevent double booking of same time slot
docs: update API documentation for payment endpoints
test: add unit tests for reservation service
refactor: extract payment strategy interface
```

## 9.4. Políticas del repositorio

- **Protección de `main`:** requiere PR aprobado y checks CI pasados.
- **No commits directos a `main` o `develop`.**
- **Revisión de código:** mínimo 2 aprobaciones.
- **Versionado semántico:** `MAJOR.MINOR.PATCH` (ej. `v1.0.0`).
- **Backups:** el repositorio en GitHub actúa como respaldo del código.

\newpage

# 10. INTEGRACIÓN DE CHATBOT CON IA

## 10.1. Descripción

PichangaApp integrará un **chatbot con IA** para automatizar la atención al cliente, responder preguntas frecuentes, resolver dudas sobre reservas y asistir a los usuarios 24/7.

## 10.2. Arquitectura del chatbot

```
┌──────────────┐      ┌──────────────────────┐      ┌─────────────────┐
│   Usuario    │ <──> │   Widget Chatbot     │ <──> │  Backend IA     │
│   (Frontend) │      │   (React)            │      │  (LLM API)      │
└──────────────┘      └──────────────────────┘      └───────┬─────────┘
                                                            │
                                               ┌────────────▼─────────┐
                                               │  Contexto BD         │
                                               │  (SQL Server -       │
                                               │   disponibilidad,    │
                                               │   precios, reservas) │
                                               └──────────────────────┘
```

## 10.3. Funcionalidades

| Función | Descripción |
|---------|-------------|
| Respuestas a preguntas frecuentes | ¿Cómo reservo? ¿Cómo cancelo? ¿Métodos de pago? |
| Consulta de disponibilidad | "¿Qué canchas hay libres mañana en Miraflores?" |
| Asistencia en reservas | Guía paso a paso para completar una reserva. |
| Notificaciones proactivas | Recordatorios de reservas y pichangas. |
| Derivación a humano | Escalamiento automático a un agente cuando el chatbot no resuelve. |

## 10.4. Tecnologías sugeridas

| Componente | Tecnología |
|------------|------------|
| Motor de conversación | OpenAI GPT-4o / Anthropic Claude API |
| Middleware | Servicio Node.js o módulo Spring Boot que consume la API de IA |
| Contexto | Consultas a la BD SQL Server (disponibilidad, precios) |
| Canal | Widget embebido en React (`<ChatBotWidget />`) + opción WhatsApp Business |
| Persistencia | Tabla `chat_history` en SQL Server |

## 10.5. Flujo típico

1. El usuario hace clic en "¿Ayuda?" → se abre el widget del chatbot.
2. El usuario escribe: *"¿Cuánto cuesta reservar una cancha en Lima Sur?"*
3. El backend consulta `CanchaDAO` por distrito y obtiene el precio promedio.
4. El chatbot responde: *"El precio por hora es de S/ 15.00. Puedes reservar aquí."*
5. Si la duda persiste, el bot ofrece "Hablar con asesor" y escala a un humano.

## 10.6. Beneficios

- Reducción del 30% en tickets de atención al cliente.
- Respuestas inmediatas 24/7.
- Mejora de la experiencia del usuario.
- Datos de interacción que alimentan mejoras de producto.

\newpage

# 11. INTEGRACIÓN DE DASHBOARD

## 11.1. Descripción

El **dashboard** de PichangaApp proporciona a propietarios y administradores una visión clara y en tiempo real de los indicadores clave del negocio (KPIs).

## 11.2. Tipos de dashboard

| Dashboard | Usuario | Contenido |
|-----------|---------|-----------|
| Dashboard de propietario | Propietario de canchas | Reservas del día, ingresos, ocupación, rating. |
| Dashboard de administrador | Administrador del sistema | Usuarios registrados, canchas activas, ingresos totales, transacciones. |
| Dashboard de jugador | Jugador | Próximas reservas, historial, gastos, pichangas. |

## 11.3. Indicadores clave (KPIs)

| KPI | Descripción | Fórmula |
|-----|-------------|---------|
| Reservas del día | Total de reservas activas hoy | `COUNT(reservas WHERE fecha = hoy)` |
| Ingresos del mes | Ingresos acumulados en el mes | `SUM(pagos.pagado) WHERE MONTH = actual` |
| Ocupación promedio | % de horarios ocupados | `(horas ocupadas / horas disponibles) × 100` |
| Rating promedio | Calificación de la cancha | `AVG(calificacion.puntaje)` |
| Nuevos usuarios | Altas en el periodo | `COUNT(usuarios WHERE fecha_registro ∈ periodo)` |

## 11.4. Tecnologías sugeridas

| Componente | Tecnología |
|------------|------------|
| Gráficos | Chart.js / Recharts |
| Tablas | Material-UI DataGrid |
| Exportación | jspdf (PDF), PapaParse (CSV) |
| Backend | Endpoints `/api/dashboard/{id}` que agregan KPIs |

## 11.5. Ejemplo de endpoint

```java
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ReservaDAO reservaDAO;

    @GetMapping("/propietario/{propietarioId}")
    public Map<String, Object> getDashboard(@PathVariable Long propietarioId) {
        Map<String, Object> data = new HashMap<>();
        data.put("reservasHoy", reservaDAO.countReservasHoy(propietarioId));
        data.put("ingresosMes", reservaDAO.sumIngresosMes(propietarioId));
        return data;
    }
}
```

## 11.6. Visualización

> **Nota para el informe:** incluir capturas de los gráficos generados con Chart.js (líneas de evolución semanal, barras comparativas, donas de ocupación).

\newpage

# 12. REGLAS DE NEGOCIO

Las reglas de negocio definen las condiciones y restricciones que gobiernan el funcionamiento de PichangaApp. Estas reglas deben implementarse tanto en la capa de base de datos (restricciones, triggers) como en la capa de servicios (validaciones).

## 12.1. Reglas de registro y autenticación

| ID | Regla | Descripción |
|----|-------|-------------|
| RN01 | Unicidad de correo | Cada dirección de correo electrónico sólo puede estar asociada a una cuenta. El sistema debe rechazar registros con correo duplicado. |
| RN02 | Contraseña segura | La contraseña debe tener mínimo 8 caracteres, incluyendo al menos una letra mayúscula, una minúscula y un número. |
| RN03 | Tipo de usuario obligatorio | Todo usuario debe elegir su tipo (jugador, propietario) en el registro. El tipo determina los permisos y módulos visibles. |
| RN04 | Verificación de correo | La cuenta sólo se activa después de confirmar el correo electrónico mediante el enlace enviado (vigencia 24 horas). |
| RN05 | Sesión única por token | Un usuario no puede tener más de una sesión activa con el mismo access token; al iniciar sesión se invalida el token anterior. |

## 12.2. Reglas de búsqueda y reservas

| ID | Regla | Descripción |
|----|-------|-------------|
| RN06 | Disponibilidad en tiempo real | La búsqueda de canchas debe reflejar el estado de disponibilidad en tiempo real, bloqueando horarios ya reservados. |
| RN07 | Bloqueo exclusivo de horario | Un bloque horario sólo puede ser reservado por un único usuario a la vez. Si dos usuarios intentan reservarlo simultáneamente, el sistema debe rechazar al segundo con el mensaje "Horario ya no disponible". |
| RN08 | Horario dentro del rango de atención | No se permite reservar fuera del horario de apertura/cierre definido por el propietario. |
| RN09 | Reserva mínima y máxima | La duración mínima de una reserva es de 1 hora y la máxima de 3 horas consecutivas. |
| RN10 | Antelación mínima | No se permite reservar con menos de 2 horas de antelación ni más de 30 días de anticipación. |
| RN11 | Pago previo obligatorio | La reserva pasa al estado "confirmada" únicamente después de completar el pago. Si el pago no se completa en 15 minutos, la reserva se libera automáticamente. |

## 12.3. Reglas de pago

| ID | Regla | Descripción |
|----|-------|-------------|
| RN12 | Métodos de pago permitidos | Sólo se aceptan Yape, Plin, Mercado Pago y PayPal como métodos de pago. |
| RN13 | Integridad de transacción | Los IDs de transacción externos (Yape/Plin/Mercado Pago/PayPal) deben ser únicos; los duplicados se rechazan. |
| RN14 | Monto mínimo | El monto mínimo de pago por reserva es de S/ 10.00. |
| RN15 | Registro de auditoría | Todo pago debe quedar registrado con su fecha, método, monto y estado para efectos de auditoría. |

## 12.4. Reglas de pichangas

| ID | Regla | Descripción |
|----|-------|-------------|
| RN16 | Cupos limitados | La cantidad de participantes en una pichanga no puede exceder el cupo definido al crearla. |
| RN17 | Reserva previa del organizador | Sólo puede publicar una pichanga un jugador con una reserva confirmada para la misma cancha, fecha y hora. |
| RN18 | Sin conflicto de horarios | Un jugador no puede unirse a una pichanga si ya tiene una reserva o pichanga en el mismo horario. |
| RN19 | Edad mínima | El organizador debe tener al menos 18 años o estar acompañado por un adulto mayor de 21 años. |
| RN20 | Cierre de inscripción | La inscripción a una pichanga se cierra 2 horas antes de la hora de inicio o cuando los cupos se agotan. |

## 12.5. Reglas de cancelación y reembolso

| ID | Regla | Descripción |
|----|-------|-------------|
| RN21 | Cancelación con reembolso | El jugador puede cancelar una reserva con al menos 24 horas de antelación y recibir el 100% del importe. |
| RN22 | Cancelación parcial | La cancelación entre 2 y 24 horas antes genera un reembolso del 50%. |
| RN23 | Cancelación sin reembolso | Las cancelaciones dentro de las 2 horas previas al inicio no generan reembolso. |
| RN24 | Cancelación por propietario | Si el propietario cancela una reserva confirmada, el jugador recibe el 100% del reembolso y una compensación del 10% en créditos de la plataforma. |

## 12.6. Reglas de calificaciones

| ID | Regla | Descripción |
|----|-------|-------------|
| RN25 | Calificación post-uso | Sólo los jugadores que hayan completado una reserva pueden calificar la cancha. |
| RN26 | Rango de puntaje | La calificación debe estar en el rango de 1 a 5 estrellas. |
| RN27 | Una calificación por reserva | Cada reserva sólo puede generar una calificación del jugador hacia la cancha. |
| RN28 | Moderación de comentarios | Los comentarios que contengan lenguaje ofensivo o información falsa pueden ser eliminados por el administrador. |

## 12.7. Reglas de administración

| ID | Regla | Descripción |
|----|-------|-------------|
| RN29 | Registro de canchas | Sólo un propietario verificado puede registrar una nueva cancha. |
| RN30 | Aprobación de canchas | Las canchas nuevas deben ser aprobadas por el administrador antes de publicarse en el catálogo (máximo 48 horas). |
| RN31 | Bloqueo de horario reservado | El propietario no puede bloquear un horario que ya está reservado y pagado; debe solicitar una cancelación previa. |
| RN32 | Reportes disponibles | Los reportes sólo pueden consultarse dentro del rango de fechas permitido (máximo 12 meses hacia atrás). |

## 12.8. Reglas de datos y seguridad

| ID | Regla | Descripción |
|----|-------|-------------|
| RN33 | Protección de datos personales | Los datos personales se tratarán conforme a la Ley N° 29733 de Protección de Datos Personales del Perú. |
| RN34 | Retención de datos | El historial de reservas se conserva por un mínimo de 5 años para fines tributarios y de auditoría. |
| RN35 | Respaldos automáticos | La base de datos se respalda automáticamente cada 24 horas, con retención mínima de 30 días. |
| RN36 | Intento de acceso fallido | Después de 5 intentos fallidos de inicio de sesión, la cuenta se bloquea por 15 minutos. |

\newpage

# 13. POLÍTICAS DE LA EMPRESA

## 13.1. Política de Privacidad

PichangaApp se compromete a proteger la privacidad y los datos personales de sus usuarios.

**Datos recopilados:**
- Nombre, correo electrónico, teléfono, contraseña cifrada (BCrypt).
- Datos de reservas, pagos e historial de uso.
- Ubicación (sólo cuando el usuario lo autoriza para buscar canchas cercanas).

**Uso de los datos:**
- Operación y mejora de la plataforma.
- Gestión de reservas y pagos.
- Envío de notificaciones y comunicaciones autorizadas.
- Cumplimiento de obligaciones legales.

**Derechos del usuario:**
- Acceder, rectificar, cancelar u oponerse al tratamiento de sus datos.
- Solicitar la eliminación de su cuenta y datos personales.
- Ejercer sus derechos escribiendo a `privacidad@pichangaapp.pe`.

**No compartimos datos personales con terceros** excepto cuando sea necesario para procesar pagos (pasarelas) o cuando la ley lo exija.

## 13.2. Política de Pagos y Reembolsos

- Los pagos se procesan a través de Yape, Plin, Mercado Pago y PayPal.
- Las reservas sólo se confirman cuando el pago se completa.
- Los reembolsos se rigen por las reglas RN21, RN22, RN23 y RN24.
- El plazo máximo de devolución es de 7 días hábiles.
- PichangaApp no se hace responsable por demoras en devoluciones ocasionadas por entidades financieras.

## 13.3. Política de Cancelación

| Antelación | Reembolso |
|------------|-----------|
| Más de 24 horas | 100% |
| Entre 2 y 24 horas | 50% |
| Menos de 2 horas | 0% |
| Cancelación por propietario | 100% + 10% en créditos |

## 13.4. Política de Uso Aceptable

El usuario se compromete a:
- Usar la plataforma únicamente para fines lícitos.
- No publicar información falsa o engañosa.
- No intentar acceder a cuentas de otros usuarios.
- No realizar reservas fraudulentas o especulativas.
- Respetar las normas de convivencia del deporte amateur.

Las violaciones a esta política pueden resultar en la suspensión o eliminación de la cuenta.

## 13.5. Política de Seguridad

- Las contraseñas se almacenan cifradas con BCrypt.
- Las sesiones se protegen con JWT (expiración de 15 minutos para access token).
- Se implementan respaldos automáticos diarios de la base de datos.
- Se realizan auditorías de seguridad periódicas.
- El acceso al panel de administración requiere doble factor de autenticación (2FA).

## 13.6. Política de Moderación de Contenido

- Las calificaciones y comentarios son revisados para evitar lenguaje ofensivo.
- Los contenidos que infrinjan las políticas pueden ser eliminados.
- Los usuarios que reincidan en conductas inapropiadas pueden ser suspendidos.
- El administrador tiene la potestad de retirar canchas del catálogo que incumplan las políticas.

## 13.7. Política de Soporte al Cliente

- **Horario:** Lunes a sábado, 8:00 a 22:00.
- **Canales:** Chatbot IA (24/7), correo electrónico, formulario de contacto.
- **Tiempo de respuesta:** máximo 24 horas hábiles.
- **Incidentes críticos:** respuesta en menos de 4 horas.

## 13.8. Política de Ética y Cumplimiento

- Cumplimiento de la Ley N° 29733 (Protección de Datos Personales).
- Cumplimiento de las normas de protección al consumidor.
- Transparencia en comisiones y tarifas.
- Prohibición de prácticas de competencia desleal.
- Compromiso con la responsabilidad social y el fomento del deporte amateur.

## 13.9. Términos y Condiciones (Resumen)

1. Al registrarse, el usuario acepta los términos y condiciones de PichangaApp.
2. La plataforma podrá modificar los términos con aviso previo.
3. PichangaApp no se responsabiliza por daños derivados del mal uso de la plataforma.
4. La responsabilidad total de PichangaApp no excederá el monto pagado por el usuario en los últimos 30 días.
5. Estos términos se rigen por las leyes de la República del Perú; cualquier controversia se somete a los tribunales de Lima.

\newpage

# ANEXO A: PRESUPUESTO DETALLADO

> **Restricción del proyecto:** presupuesto máximo **S/ 30,000** y **16 semanas** de desarrollo.

## A.1. Resumen ejecutivo del presupuesto

| Partida | Monto (S/) | % del presupuesto |
|---------|-----------|-------------------|
| 1. Recursos humanos (equipo de desarrollo) | S/ 18,144 | 60.5% |
| 2. Infraestructura y hosting | S/ 1,600 | 5.3% |
| 3. Herramientas de software y licencias | S/ 840 | 2.8% |
| 4. Integraciones y pasarelas de pago | S/ 800 | 2.7% |
| 5. Marketing y lanzamiento | S/ 2,000 | 6.7% |
| 6. Contingencia | S/ 3,000 | 10.0% |
| 7. Capacitación y soporte | S/ 1,000 | 3.3% |
| **TOTAL** | **S/ 27,384** | **100%** |

## A.2. Desglose detallado

### A.2.1. Recursos humanos

| Rol | Responsabilidad | Semanas | Horas estimadas | Tarifa hora (S/) | Total (S/) |
|-----|-----------------|---------|-----------------|------------------|------------|
| Líder de proyecto / Backend | Arquitectura, coordinación, módulos core | 16 | 256 | S/ 15.00 | S/ 3,840 |
| Desarrollador Backend | Canchas, pagos, pichangas | 14 | 224 | S/ 12.50 | S/ 2,800 |
| Desarrollador Frontend 1 | Búsqueda, catálogo, detalle | 12 | 192 | S/ 12.50 | S/ 2,400 |
| Desarrollador Frontend 2 | Dashboard, perfil, reportes | 12 | 192 | S/ 12.50 | S/ 2,400 |
| QA / Documentación | Pruebas, docs, CI, integraciones | 14 | 224 | S/ 11.00 | S/ 2,464 |
| Diseñador UX/UI (freelance) | Prototipos Figma, diseño visual | 4 | 80 | S/ 18.00 | S/ 1,440 |
| Consultor de integraciones | Yape/Plin, Mercado Pago, chatbot | 4 | 64 | S/ 12.50 | S/ 800 |
| **Subtotal recursos humanos** | | | | | **S/ 18,144** |

### A.2.2. Infraestructura y hosting (16 semanas)

| Recurso | Descripción | Costo mensual (S/) | Meses | Total (S/) |
|---------|-------------|--------------------|-------|------------|
| VPS/App Service (backend) | Azure App Service B1 o equivalente | S/ 90 | 4 | S/ 360 |
| Hosting frontend | Vercel Pro / Netlify | S/ 80 | 4 | S/ 320 |
| SQL Server (licencia dev / Azure SQL) | SQL Server 2019 Developer (gratis) + Azure SQL básico | S/ 150 | 4 | S/ 600 |
| Almacenamiento de imágenes | Azure Blob / S3 | S/ 50 | 4 | S/ 200 |
| Dominio | `pichangaapp.pe` | S/ 120 | 1 | S/ 120 |
| SSL / CDN | Let's Encrypt (gratis) + Cloudflare free | S/ 0 | 4 | S/ 0 |
| Monitoreo (Prometheus/Grafana) | Herramientas open source en el VPS | S/ 0 | 4 | S/ 0 |
| **Subtotal infraestructura** | | | | **S/ 1,600** |

> **Nota:** SQL Server Developer Edition es gratuita para desarrollo; en producción se puede usar SQL Server Express (gratis, hasta 10 GB) o Azure SQL con costos ajustados al plan básico.

### A.2.3. Herramientas de software y licencias

| Herramienta | Tipo | Costo (S/) |
|-------------|------|------------|
| IDE (IntelliJ IDEA Community / VS Code) | Gratis | S/ 0 |
| Figma (plan educativo/equipo) | Freemium | S/ 300 |
| GitHub (organización privada) | Team plan | S/ 240 |
| Postman (equipo) | Freemium | S/ 0 |
| Docker Desktop | Gratis | S/ 0 |
| Microsoft 365 (correo corporativo) | 5 usuarios × S/ 60 | S/ 300 |
| Swagger / OpenAPI | Gratis | S/ 0 |
| **Subtotal herramientas** | | **S/ 840** |

### A.2.4. Integraciones y pasarelas de pago

| Integración | Tipo | Costo (S/) |
|-------------|------|------------|
| Yape (Banca Móvil BCP) | Integración por QR manual (0 costo) | S/ 0 |
| Plin (interoperable) | Integración por QR manual | S/ 0 |
| Mercado Pago (Perú) | Comisión por transacción (descontada en producción) | S/ 0 |
| PayPal REST | Comisión por transacción | S/ 0 |
| Twilio SMS (recordatorios) | Créditos iniciales | S/ 400 |
| OpenAI API (chatbot) | Créditos de prueba para desarrollo | S/ 400 |
| Firebase Cloud Messaging | Gratis | S/ 0 |
| **Subtotal integraciones** | | **S/ 800** |

### A.2.5. Marketing y lanzamiento

| Actividad | Costo (S/) |
|-----------|------------|
| Diseño de marca (logo, identidad) | S/ 400 |
| Landing page de lanzamiento | S/ 300 |
| Publicidad en redes sociales (Meta Ads) | S/ 700 |
| Alianzas con 10 canchas piloto | S/ 300 |
| Material de presentación (informe, demos) | S/ 300 |
| **Subtotal marketing** | **S/ 2,000** |

### A.2.6. Contingencia

| Concepto | Monto (S/) |
|----------|------------|
| Reserva para imprevistos (10% del subtotal) | S/ 3,000 |

### A.2.7. Capacitación y soporte

| Concepto | Monto (S/) |
|----------|------------|
| Manuales de usuario y admin | S/ 400 |
| Capacitación al equipo y propietarios piloto | S/ 600 |
| **Subtotal capacitación** | **S/ 1,000** |

## A.3. Validación del presupuesto

| Partida | Monto (S/) |
|---------|-----------|
| Recursos humanos | S/ 18,144 |
| Infraestructura | S/ 1,600 |
| Herramientas | S/ 840 |
| Integraciones | S/ 800 |
| Marketing | S/ 2,000 |
| Contingencia | S/ 3,000 |
| Capacitación | S/ 1,000 |
| **TOTAL** | **S/ 27,384** |

> **✅ Cumple la restricción:** S/ 27,384 ≤ S/ 30,000. Queda un **margen de S/ 2,616** para absorber variaciones de costos o ampliar alguna partida.

## A.4. Optimizaciones propuestas

| Optimización | Ahorro potencial |
|--------------|------------------|
| Usar SQL Server Developer/Express (gratis) en lugar de licencia paga | S/ 600 |
| Usar servicios free tier (Vercel, Cloudflare, FCM) | S/ 800 |
| Negociar créditos educativos de OpenAI (programa para universidades) | S/ 400 |
| GitHub Education / GitHub Free para organizaciones educativas | S/ 240 |
| **Total ahorro potencial** | **S/ 2,040** |

Con estas optimizaciones, el costo total podría reducirse a **≈ S/ 25,344**, dejando un margen de **S/ 4,656** para cubrir riesgos o ampliar el alcance.

## A.5. Proyección de ingresos (post-lanzamiento)

| Fuente de ingreso | Estimación |
|-------------------|------------|
| Comisión del 10% por reserva | S/ 5–15 por reserva |
| Suscripción premium propietarios | S/ 49–99 / mes |
| Publicidad segmentada | S/ 200–500 / mes |
| **Punto de equilibrio estimado** | 8–12 meses post-lanzamiento |

# CONCLUSIÓN

PichangaApp constituye una solución tecnológica viable, acorde al presupuesto de S/ 30,000 y al plazo de 16 semanas. El análisis de alternativas confirma que la **aplicación web responsive + API REST con Spring Boot, Hibernate y SQL Server** es la opción óptima, combinando:

- **Viabilidad económica:** S/ 27,384 estimados, con margen de contingencia.
- **Viabilidad técnica:** arquitectura moderna y escalable (React 19 + Spring Boot 3 + SQL Server).
- **Viabilidad operativa:** equipo de 5 integrantes con roles claros y roadmap definido.
- **Potencial de mercado:** necesidad real en el sector deportivo amateur peruano.

Los siguientes pasos inmediatos son: validar los prototipos con usuarios piloto, configurar el repositorio Git, iniciar el diseño de la base de datos en SQL Server y comenzar el desarrollo del módulo de autenticación.

---

**Fin del documento** — PichangaApp Informe Técnico del Proyecto (Avance 1)

*Documento generado con apoyo de herramientas MCP (Context7 para documentación técnica actualizada de React, Spring Boot y Hibernate).*