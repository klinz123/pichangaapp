# Matriz de Trazabilidad — Alcance vs. Prototipo (PichangaApp)

> Evidencia para el Criterio 4 de la rúbrica: demuestra que el prototipo cubre el 100 % del alcance comprometido. Incluir esta tabla en la sección 4 (Prototipos) del informe.

## A. Requerimientos funcionales (RF01–RF12) → Pantallas del prototipo

| ID | Requerimiento | Pantalla(s) que lo cubren | Estado |
|---|---|---|---|
| RF01 | Registro de usuarios | P1-2 Registro; P3 pantalla 1 (Home → Crear cuenta) | ✔ |
| RF02 | Inicio de sesión | P1-1 Login; P3 pantalla 1 | ✔ |
| RF03 | Gestión de perfil | P2 Historial / Perfil; Tabla 13 Perfil de usuario | ✔ |
| RF04 | Búsqueda de canchas | P1-3 Catálogo; P1-4 Detalle; P3-2 Búsqueda avanzada; P3-3 Resultados | ✔ |
| RF05 | Reserva de canchas | P1-ConfirmacionReserva; P3-4 Horarios; P3-ResumenReserva; P3-Pago | ✔ |
| RF06 | Publicación de pichangas | P3-CrearPichanga | ✔ |
| RF07 | Unión a pichangas | P3-Pichangas (listado + botón "Unirme") | ✔ |
| RF08 | Gestión de pagos | P3-6 Confirmación de pago (QR Yape/Plin); P3-7 Confirmación final | ✔ |
| RF09 | Notificaciones | P3-8 Notificación push; P2 Historial (recordatorios) | ✔ |
| RF10 | Calificación de canchas | P1-4 Detalle (sección reseñas); Tabla 13 Perfil | ✔ |
| RF11 | Historial de reservas | P1-ConfirmacionReserva ("Ver mis reservas"); P2-3 Historial de reservas | ✔ |
| RF12 | Administración de canchas | P2-1 Panel de control; P2-FormularioHorario; P2-ParrillaHorarios; P2-Reportes | ✔ |

> **100 % del alcance funcional cubierto con mockups.** Todos los RF01–RF12 tienen pantallas dedicadas o se cubren dentro de un flujo prototipado.

## B. Requerimientos no funcionales (RNF01–RNF10) → Cómo los cubre el prototipo/diseño

| ID | Requerimiento | Evidencia en el informe |
|---|---|---|
| RNF01 | Seguridad (JWT + BCrypt) | Sección 3.2 Seguridad del diseño; 12.8 Datos y seguridad |
| RNF02 | Disponibilidad 99 % | Sección 8 (despliegue); Anexo presupuesto (infraestructura) |
| RNF03 | Rendimiento < 3 s | Tabla 9 RNF03; arquitectura por capas |
| RNF04 | Usabilidad | Prototipos navegables (Figma); regla de negocio de ≤ 3 pasos |
| RNF05 | Compatibilidad (Android/iOS/web) | Prototipo responsive; Tabla 9 RNF05 |
| RNF06 | Escalabilidad (1000 usuarios) | Justificación tecnológica; Spring Boot + SQL Server |
| RNF07 | Mantenibilidad (cobertura > 80 %) | F6 Calidad; Tabla 17 |
| RNF08 | Confiabilidad | RNF08; backups diarios (RNF10) |
| RNF09 | Accesibilidad | Prototipos responsivos; Tabla 9 RNF09 |
| RNF10 | Backups diarios (30 días) | Tabla 9 RNF10; RN35 |

## C. Pantallas creadas en Figma (estado: completado — 6 pantallas nuevas)

Se crearon en la página **`PichangaApp - Prototipos`** de Figma, replicando el estilo existente (celeste `#0b87d6`, sidebar `#0f172a`):

| # | Pantalla (nombre en Figma) | Prototipo | RF que cubre | Resultado |
|---|---|---|---|---|
| 1 | `P1-ConfirmacionReserva` | Prototipo 1 (pasa de 4 a **5**) | RF05, RF11 | ✔ |
| 2 | `P2-FormularioHorario` | Prototipo 2 (pasa de 3 a 4) | RF12 | ✔ |
| 3 | `P2-Reportes` | Prototipo 2 (pasa a **5**) | RF12 / dashboard | ✔ |
| 4 | `P3-ResumenReserva` | Prototipo 3 (pantalla 5 de la Tabla 12) | RF05 | ✔ |
| 5 | `P3-CrearPichanga` | Prototipo 3 (definitivo) | RF06 | ✔ |
| 6 | `P3-Pichangas` | Prototipo 3 (definitivo) | RF07 | ✔ |

**Conteo final por alternativa (Criterio 2):**
- Alternativa 1 → Prototipo 1: **5 pantallas** ✔
- Alternativa 2 → Prototipo 3: **8+ pantallas** (Home, Búsqueda, Resultados, Horarios, Resumen, Pago, Confirmación, Pichangas, Crear pichanga) ✔
- Alternativa 3 → Prototipo 2: **5 pantallas** ✔

---
*Nota: Elaboración propia.*