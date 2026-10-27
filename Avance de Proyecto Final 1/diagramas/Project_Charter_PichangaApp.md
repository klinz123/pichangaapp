# Project Charter Básico — PichangaApp

> Documento de respaldo para el Criterio 3 de la rúbrica (diseño de la estructura de la solución). Incluir como sección nueva del informe o como anexo.

## 1. Información general

| Campo | Detalle |
|---|---|
| Nombre del proyecto | PichangaApp — Plataforma web de reserva de canchas y pichangas |
| Curso | Curso Integrador I: Sistemas Software |
| Fecha de inicio | Semana 1 |
| Fecha de fin | Semana 16 (plazo máximo) |
| Presupuesto aprobado | S/ 30,000 (máximo) |
| Patrocinador / Stakeholder principal | Docente del curso / Junta directiva del proyecto |
| Líder del proyecto | Marizza (líder y backend) |

## 2. Justificación del proyecto

Los jugadores de fútbol amateur coordinan partidos por canales informales (llamadas, WhatsApp, Facebook), lo que genera demoras, información desactualizada y dobles reservas. Los propietarios de canchas administran su parrilla de horarios sin una herramienta digital. PichangaApp centraliza la búsqueda, reserva, pago y organización de pichangas en una única plataforma web.

## 3. Objetivos

- **Objetivo general:** Desarrollar una plataforma web tipo marketplace que permita buscar, reservar y pagar canchas deportivas y organizar/participar en pichangas, dentro de un presupuesto de S/ 30,000 y 16 semanas.
- **Objetivos específicos:** (Ver Tabla 1 del informe — 10 objetivos específicos).

## 4. Alcance

**Dentro del alcance (funcional):**
- Registro e inicio de sesión (jugador, propietario, administrador).
- Búsqueda de canchas con filtros (ubicación, fecha, hora, superficie, precio).
- Reserva de canchas con confirmación y bloqueo de horarios.
- Pagos con Yape, Plin, Mercado Pago y PayPal.
- Publicación y unión a pichangas con control de cupos.
- Notificaciones (correo, push, SMS).
- Calificación y reseñas de canchas.
- Historial de reservas y perfil.
- Panel de administración del propietario con reportes exportables.
- Dashboard del administrador.

**Dentro del alcance (no funcional):**
- Seguridad (JWT + BCrypt, Ley 29733).
- Disponibilidad 99 %, respuesta < 3 s, ≥ 1000 usuarios concurrentes, backups diarios.

**Fuera del alcance:**
- Aplicaciones nativas (Android/iOS) en esta fase.
- Verificación de identidad presencial.
- Expansión internacional en esta fase.

## 5. Entregables principales

| # | Entregable | Semana |
|---|---|---|
| E1 | Documento de requisitos + prototipos | 4 |
| E2 | Diagramas (BPMN 2.0, clases, ER, Gantt) + script SQL | 7 |
| E3 | API REST funcional | 11 |
| E4 | Aplicación web React | 12 |
| E5 | Prototipo integrado (frontend + backend + pagos prueba) | 12 |
| E6 | Reporte de calidad (funcional, seguridad, rendimiento) | 14 |
| E7 | Plataforma en línea (producción + SSL) | 15 |
| E8 | Proyecto entregado + sustentación | 16 |

## 6. Hitos principales

| Hito | Fecha (semana) |
|---|---|
| Avance 1 — Requisitos y prototipos | Semana 4 |
| Avance 2 — Diseño y arquitectura | Semana 7 |
| Avance 3 — MVP funcional | Semana 12 |
| Cierre y sustentación | Semana 16 |

## 7. Riesgos principales y mitigación

| Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|
| Integración de pasarelas de pago retrasada | Media | Alto | Usar modos sandbox desde la fase F5; consultor de integraciones (Tabla 31 del informe). |
| Doble reserva por concurrencia | Media | Alto | Bloqueo optimista + restricción de unicidad en BD (cancha, fecha, hora_inicio). |
| Sobrecosto por SQL Server en producción | Media | Medio | Usar edición de desarrollo / planes gratuitos (optimización S/ 25,344). |
| Desviación de cronograma | Media | Alto | Seguimiento semanal del Gantt; fases con entregables verificables. |
| Seguridad de datos personales | Baja | Alto | JWT + BCrypt + backups diarios + cumplimiento Ley 29733. |

## 8. Recursos y presupuesto

| Partida | Monto (S/) |
|---|---|
| Recursos humanos | 18,144 |
| Infraestructura y hosting | 1,600 |
| Herramientas y licencias | 840 |
| Integraciones y pasarelas | 800 |
| Marketing y lanzamiento | 2,000 |
| Contingencia | 3,000 |
| Capacitación y soporte | 1,000 |
| **Total** | **27,384** |

## 9. Equipo del proyecto

| Rol | Responsable |
|---|---|
| Líder de proyecto y backend | Marizza |
| Desarrollador backend | Segundo T. |
| Desarrollador frontend | Juan F. |
| Desarrollador frontend | Erick |
| Calidad y documentación | Jairo S. |

## 10. Criterios de éxito

- El prototipo cubre el 100 % del alcance funcional comprometido (RF01–RF12) y los RNF01–RNF10.
- El sistema queda operativo dentro del presupuesto y el plazo.
- Todas las pruebas funcionales, de seguridad y de rendimiento aprobadas (F6).
- Cobertura de pruebas superior al 80 % (RNF07).

---
*Nota: Elaboración propia.*