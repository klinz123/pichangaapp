# PichangaApp – Proyecto Mejorado

## 1. Idea Mejorada

**PichangaApp** es una plataforma web tipo *marketplace* que conecta a jugadores de fútbol amateur con propietarios de canchas deportivas. La solución centraliza la búsqueda, reserva, pago y organización de partidos (pichangas), sustituyendo las coordinaciones por redes sociales, llamadas y mensajes.  

**Nuevas características clave:**
- **Catálogo inteligente:** filtrado avanzado por ubicación, superficie (césped sintético, césped natural), horarios disponibles, precio y opiniones de usuarios.
- **Sistema de pichangas dinámicas:** los jugadores pueden crear pichangas con cupos limitados, horarios fijos o flexibles, y recibir notificaciones cuando hay suficiente interés para formar el partido.
- **Integración de pagos regionales:** además de Yape y Plin, se integra **Mercado Pago** y **PayPal** para cobertura internacional y usuarios sin cuentas bancarias locales.
- **Perfiles verificados:** opción de verificación de identidad para propietarios (foto de licencia de actividad física, RUC) y jugadores (carnet de afiliación a club), mostrando un sello de "verificado" en el perfil.
- **Historial y estadísticas:** cada usuario tiene un historial de reservas, participaciones en pichangas, calificaciones dadas y recibidas, y métricas de uso (número de partidos, gasto total, etc.).
- **Notificaciones push y SMS:** recordatorios de reservas próximas, confirmaciones de pichangas completadas, y alertas de disponibilidad de última hora.
- **Modo offline/online:** los propietarios pueden marcar temporalmente la cancha como "no disponible" sin conexión, y los cambios se sincronizarán cuando haya internet.
- **Soporte multilingüe:** interfaz en español y inglés, con opción de cambiar idioma en el perfil.

**Objetivo ampliado:** Facilitar la vida de jugadores y propietarios de canchas amateur en el Perú y, progresivamente, en otros países latinoamericanos, ofreciendo una experiencia segura, rápida y transparente.

---

## 2. Procesos de Negocio

| Proceso | Actor(es) | Descripción paso a paso | Resultado esperado |
|---------|-----------|------------------------|--------------------|
| **P01. Registro y alta de usuario** | Jugador / Propietario | 1. El usuario accede a la página de registro.<br>2. Completa formulario (nombre, correo, teléfono, tipo de usuario).<br>3. Selecciona “verificar identidad” (opcional).<br>4. Sistema envía correo de confirmación con enlace.<br>5. Al hacer clic, la cuenta se activa. | Cuenta de usuario activa, listo para login. |
| **P02. Inicio de sesión** | Jugador / Propietario | 1. Ingresa correo y contraseña (o login social Google/Apple).<br>2. Si habilitado, factor de segunda autenticación (SMS OTP).<br>3. Acceso al dashboard. | Sesión iniciada, acceso al área correspondiente. |
| **P03. Búsqueda de canchas** | Jugador | 1. Ingresa ubicación (dirección, zona, o usa GPS).<br>2. Filtra por fecha, hora, superficie, precio máximo.<br>3. Ve lista con disponibilidad en tiempo real, fotos, y opiniones.<br>4. Selecciona una cancha para ver detalle. | Lista de canchas que coinciden con los filtros. |
| **P04. Reserva de cancha** | Jugador | 1. En el detalle de la cancha, selecciona bloque horario disponible.<br>2. Revisa resumen: fecha, hora, precio total.<br>3. Elige método de pago (Yape, Plin, Mercado Pago, PayPal).<br>4. Confirma pago; sistema genera comprobante.<br>5. La cancha pasa a estado “reservado” para ese bloque. | Reserva confirmada y pago registrado. |
| **P05. Gestión de horarios (propietario)** | Propietario | 1. Ingresa al panel de administración con credenciales.<br>2. Ve parrilla de horarios por cancha.<br>3. Bloquea o libera horarios (arrastrar soltar o clic).<br>4. Sistema actualiza disponibilidad al instante para todos los jugadores. | Horarios actualizados y sincronizados. |
| **P06. Publicación de pichanga** | Jugador (con reserva confirmada) | 1. Desde el dashboard, selecciona “Crear pichanga”.<br>2. Llena datos: fecha, hora, cancha, número de cupos, descripción, precio opcional por participante.<br>3. Publica; la pichanga aparece en el listado público.<br>4. Otros jugadores pueden unirse mientras queden cupos. | Pichanga activa y visible para la comunidad. |
| **P07. Unión a pichanga** | Jugador | 1. Navega por pichangas abiertas.<br>2. Filtra por fecha, cancha, nivel de juego.<br>3. Clic en “Unirse”; sistema verifica que el usuario no tenga reservas conflictivas.<br>4. Confirma inscripción; cupo se descuenta.<br>5. Se envía notificación de confirmación. | Jugador inscrito en la pichanga. |
| **P08. Procesamiento de pago** | Jugador | 1. Al momento de reservar o unirse a una pichanga con costo, se elige método de pago.<br>2. Se redirige a la pasarela correspondiente (Yape/Plin QR, Mercado Pago, PayPal).<br>3. El usuario completa el pago en su aplicación.<br>4. Pasarela devuelve estado de transacción al sistema.<br>5. Sistema actualiza estado a “pagado” y envía comprobante. | Pago validado y reserva/pichanga asegurada. |
| **P09. Notificaciones** | Sistema | 1. Recordatorio 24 h antes de la reserva.<br>2. Confirmación inmediatamente después de la reserva.<br>3. Alerta de pichanga llena o cancelada.<br>4. Correo mensual de resumen de gastos y participaciones. | Usuario informado y compromiso reducido de ausencias. |
| **P10. Calificación y reseña** | Jugador (después de la reserva) | 1. Tras finalizar el partido, el sistema invita a calificar la cancha (1‑5 estrellas) y dejar comentarios.<br>2. La calificación se publica en el perfil de la cancha y afecta el rating promedio.<br>3. Propietario puede responder a comentarios. | Mejora calidad de información y fomenta buen comportamiento. |
| **P11. Generación de reportes (propietario)** | Propietario | 1. En el panel, selecciona rango de fechas.<br>2. Sistema muestra gráficos: reservas totales, ingresos, pichangas organizadas, jugadores activos.<br>3. Exporta a CSV/PDF o visualiza en pantalla. | Datos estratégicos para la toma de decisiones. |

---

## 3. Reglas de Negocio (Domain Rules)

1. **R01 – Unicidad de correo:** Cada dirección de correo electrónico sólo puede estar asociada a una cuenta de usuario. Los intentos de registro con correo existente deben retornar error.
2. **R02 – Horario excludente:** Una vez que un jugador reserva un bloque horario, ese hueco queda “bloqueado” para nuevas reservas. Si otro jugador intenta reservar el mismo hueco, el sistema debe mostrar “horario no disponible”.
3. **R03 – Cupos de pichanga:** La cantidad de participantes en una pichanga no puede exceder el límite definido al crearla. El sistema debe impedir la inscripción cuando el cupo está lleno.
4. **R04 – Pago previo obligatorio:** Para reservar una cancha, el pago debe estar *completado* antes de cambiar el estado de la reserva a “confirmada”. Si el pago falla, la reserva se cancela automáticamente después de 15 min.
5. **R05 – Verificación de identidad (opcional):** Los propietarios que deseen mostrar el sello “verificado” deben subir una foto de su licencia de actividad física o RUC. Un administrador revisa y aprueba en un plazo máximo de 48 h.
6. **R06 – Política de cancelación:** El jugador puede cancelar una reserva con al menos 2 h de antelación; se devuelve el 80 % del importe. Cancelaciones dentro de los 2 h no generan reembolso.
7. **R07 – Horario de atención:** Las canchas tienen horarios de apertura y cierre definidos por el propietario. El sistema no permite reservas fuera de esos horarios.
8. **R08 – Notificación de cambios:** Si el propietario modifica un horario ya reservado, el sistema debe notificar a los jugadores afectados mediante push/SMS y ofrecerles la opción de re‑reservar o cancelar con reembolso completo.
9. **R09 – Integridad de datos de pago:** Los IDs de transacción de Yape/Plin/Mercado Pago/PayPal deben ser únicos por sesión; duplicados deben ser rechazados.
10. **R10 – Edad mínima:** Para crear una pichanga, el organizador debe tener al menos 18 años o estar acompañado de un adulto mayor de 21 años.

---

## 4. Términos y Condiciones (Terms & Conditions)

### 4.1. Aceptación
Al registrarse en PichangaApp, el usuario acepta estos Términos y Condiciones en su versión publicada en el momento del registro. Se entiende que el usuario ha leído y comprendido el contenido.

### 4.2. Uso de la Plataforma
- El usuario se compromete a utilizar la plataforma únicamente para fines lícitos y de acuerdo con la presente política.
- Está prohibido intentar obtener acceso no autorizado a cuentas, sistemas o información ajena.
- Está prohibido publicar contenido ilícito, ofensivo, discriminatorio o que infrinja derechos de terceros.

### 4.3. Cuenta de Usuario
- El usuario es responsable de mantener la confidencialidad de su contraseña y de todas las actividades que se realicen bajo su cuenta.
- El usuario debe notificar inmediatamente a PichangaApp cualquier uso no autorizado de su cuenta o brecha de seguridad.
- PichangaApp se reserva el derecho de suspender o dar de baja cuentas que violen estos términos o presenten actividad sospechosa.

### 4.4. Reservas y Pagos
- Todas las reservas están sujetas a la disponibilidad real de la cancha en el momento de la confirmación del pago.
- Los precios mostrados son orientativos; el precio final se confirmará después de seleccionar el método de pago y aplicarse los impuestos correspondientes.
- Los reembolsos se procesarán según la Política de Cancelación (Art. R06). PichangaApp no se responsabiliza por retrasos en la devolución de fondos causados por entidades financieras.

### 4.5. Pichangas
- El organizador de una pichanga es responsable de informar con exactitud la fecha, hora, lugar y número de cupos.
- PichangaApp no se hace responsable por cancelaciones o cambios organizados por el propio propietario de la cancha, siempre y cuando se cumplan los plazos de notificación (mínimo 2 h antes del inicio).
- Los participantes aceptan unirse bajo su propio riesgo y deben cumplir con las normas de seguridad y convivencia del deportista amateur.

### 4.6. Privacidad y Tratamiento de Datos
- Los datos personales (nombre, correo, teléfono, datos de pago) serán tratados de acuerdo con la **Política de Privacidad** de PichangaApp, la cual cumple con la normativa de protección de datos del Perú (Ley N° 29733) y el RGPD para usuarios europeos.
- Los datos se utilizan para la correcta operación de la plataforma, mejora de servicios, envío de comunicaciones comerciales (solo con consentimiento) y cumplimiento de obligaciones legales.
- El usuario tiene derecho a acceder, rectificar, actualizar u oponer al tratamiento de sus datos escribiendo a privacidad@pichangaapp.pe.

### 4.7. Propiedad Intelectual
- Todo el contenido de la plataforma (marcas, logotipos, diseños, código fuente, textos) es propiedad de PichangaApp o de sus licenciantes y está protegido por las leyes de propiedad intelectual.
- Queda prohibida la reproducción, distribución o modificación no autorizada de dicho contenido.

### 4.8. Limitación de Responsabilidad
- PichangaApp no garantiza la disponibilidad ininterrumpida de la plataforma y no se responsabiliza por daños directos, indirectos, incidentales o consecuentes derivados del uso o imposibilidad de uso del servicio.
- La responsabilidad total de PichangaApp por cualquier reclamo derivado de estos Términos no excederá el monto pagado por el usuario en los últimos 30 días.

### 4.9. Modificaciones
- PichangaApp podrá actualizar estos Términos y Condiciones en cualquier momento. Se comunicará el cambio a través de notificación en la plataforma y/o correo electrónico. El uso continuado de la plataforma después de dichos cambios implica la aceptación de los nuevos términos.

### 4.10. Ley Aplicable y Jurisdicción
- Estos Términos se rigen por las leyes de la República del Perú. Cualquier controversia será sometida a los tribunales competentes de Lima, renunciando a cualquier otro fuero.

---

## 5. Arquitectura y Tecnología (Resumen)

| Componente | Tecnología sugerida |
|------------|---------------------|
| **Frontend** | React + TypeScript, Redux Toolkit, Material‑UI (o Tailwind CSS). |
| **Backend** | Node.js (NestJS) o Java Spring Boot (según el equipo). |
| **Base de datos** | SQL Server (por ejemplo, SQL Server 2019/2022) con tipos de datos **geometry**/**geography** y soporte espacial mediante Hibernate (por ejemplo, la extensión `org.hibernate.spatial` o el uso de la funcionalidad espacial de EF Core / Hibernate). | |
| **Autenticación** | JWT con refresh‑token, Opcional OAuth 2.0 (Google, Apple). |
| **Pasarelas de pago** | Integraciones oficiales: Yape/Plin (API local), Mercado Pago SDK, PayPal REST. |
| **Notificaciones** | Firebase Cloud Messaging (FCM) para push, Twilio SMS para recordatorios. |
| **Despliegue** | Vercel (frontend) + Supabase/Heroku (backend + base de datos) o AWS Elastic Beanstalk. |
| **Control de versiones** | Git Hub con flujo *Gitflow* o *Trunk‑based* y PRs revisados. |
| **Testing** | Jest + React Testing Library (frontend); Postman/Spring Test (backend); pruebas de seguridad OWASP Top 10. |

---

## 6. Próximos Pasos (Roadmap Mejorado)

| Semana | Entregable |
|--------|------------|
| 01‑02 | Definición final del **Lean Canvas**, modelo de datos preliminar y especificación de APIs. |
| 03 | **Toma de requerimientos** formal (usuarios, casos de uso, reglas de negocio). |
| 04 | **Entrega Avance Proyecto Final 1** (este documento). |
| 05‑06 | Modelado BPM y diseño de esquema relacional (SQL Server). |
| 07 | Diseño UX/UI de pantallas clave (login, búsqueda, detalle cancha, panel propietario). |
| 08‑09 | **Entrega Avance Proyecto Final 2** (prototipado interactivo y arquitectura del sistema). |
| 10‑11 | Desarrollo backend: módulos de autenticación, reserva, integración de pagos, notificaciones. |
| 12 | **Entrega Avance Proyecto Final 3** (versión funcional mínima – MVP). |
| 13 | Pruebas funcionales QA y corrección de bugs. |
| 14 | Pruebas de seguridad (penetration test básico, revisión de OWASP). |
| 15 | Despliegue en entorno de producción (Vercel + Supabase) y configuración de dominios. |
| 16 | Monitoreo, mantenimiento preventivo y sustentación final. Presentación del proyecto concluido. |

---

**Fin del documento**.  
Este archivo reúve la idea mejorada, procesos de negocio, reglasdomain y términos y condiciones listos para ser revisados por el equipo y el docente. Puedes guardarlo, compartirlo o continuar con el desarrollo conforme al roadmap.