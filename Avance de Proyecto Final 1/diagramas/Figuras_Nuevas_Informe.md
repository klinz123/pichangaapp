# Texto de figuras nuevas para el informe PichangaApp (APA)

> Listo para copiar y pegar en el informe. Cada bloque incluye el título de la figura (estilo APA, en negrita bajo la imagen), la imagen a insertar y la nota.
>
> **Consejo:** usa Word con "Referencias → Insertar título" (Caption). Así, al insertar cada figura en su sección, los números se renumeran automáticamente. La numeración siguiente es el **resultado final coherente** de todo el documento si insertas todo en el orden indicado.

---

## Mapa de numeración final (antes → después)

| Figura actual | Contenido | Nueva numeración |
|---|---|---|
| — | Diagrama ER (nueva, §3.1) | Figura 2 |
| — | BPMN reserva y pago (nueva, §3.3) | Figura 3 |
| — | BPMN registro y verificación (nueva, §3.3) | Figura 4 |
| Figura 2 | P1 login | Figura 5 |
| Figura 3 | P1 registro | Figura 6 |
| Figura 4 | P1 catálogo | Figura 7 |
| Figura 5 | P1 detalle | Figura 8 |
| — | P1 confirmación de reserva (nueva) | Figura 9 |
| Figura 6 | P2 panel de control | Figura 10 |
| Figura 7 | P2 parrilla de horarios | Figura 11 |
| Figura 8 | P2 historial de reservas | Figura 12 |
| — | P2 formulario para añadir horario (nueva) | Figura 13 |
| — | P2 reportes e ingresos (nueva) | Figura 14 |
| Figura 9 | P3 página de inicio | Figura 15 |
| Figura 10 | P3 búsqueda avanzada | Figura 16 |
| Figura 11 | P3 resultados de búsqueda | Figura 17 |
| Figura 12 | P3 horarios y disponibilidad | Figura 18 |
| — | P3 resumen de reserva (nueva) | Figura 19 |
| Figura 13 | P3 confirmación de pago | Figura 20 |
| Figura 14 | P3 confirmación de reserva | Figura 21 |
| — | P3 creación de pichanga (nueva) | Figura 22 |
| — | P3 listado y unión a pichangas (nueva) | Figura 23 |
| Figura 15 | Captura web: inicio (vacía) | Figura 24 |
| Figura 16 | Captura web: búsqueda (vacía) | Figura 25 |
| — | Diagrama de clases (nueva, §6) | Figura 26 |
| Figura 17 | Diagrama de Gantt | Figura 27 |

---

## Sección 3 — Diseño de la Solución

### 3.1 Modelo de datos → Diagrama Entidad-Relación

```
Figura 2
Diagrama entidad-relación de PichangaApp

[Imagen: Diagrama_ER_PichangaApp.drawio → exportar PNG]

Nota. Elaboración propia con draw.io. El modelo incluye las entidades horario y pichanga_participante, necesarias para modelar la disponibilidad (RF04–RF05) y la inscripción a pichangas (RF07). Se emplea notación de pata de cuervo: 1:N, 1:1 y N:M mediante tabla puente.
```

**Dónde insertar:** al final de la sección 3.1, después de la Tabla 11.

### Nueva subsección 3.3 Modelado de procesos (BPMN 2.0)

```
Figura 3
Diagrama de procesos BPMN 2.0: reserva de cancha con pago

[Imagen: BPMN_P04_Reserva_Cancha_Pago.drawio → exportar PNG]

Nota. Elaboración propia con draw.io (BPMN 2.0). El proceso P04 muestra la interacción entre el jugador, el sistema PichangaApp y la pasarela de pago, incluida la regla RN11: si el pago no se completa en 15 minutos, el bloque se libera. Las flechas punteadas representan mensajes entre piscinas.
```

```
Figura 4
Diagrama de procesos BPMN 2.0: registro y verificación de identidad

[Imagen: BPMN_P01_Registro_Verificacion.drawio → exportar PNG]

Nota. Elaboración propia con draw.io (BPMN 2.0). El proceso P01 cubre el registro con verificación de correo (vigencia 24 h) y la verificación opcional de identidad del propietario, aprobada por el administrador en un máximo de 48 h (RN04, RN29).
```

**Dónde insertar:** como nueva subsección 3.3 entre "3.1 Modelo de datos" y "3.2 Seguridad del diseño".

---

## Sección 4 — Prototipos (pantallas nuevas en Figma)

### Prototipo 1 (Alternativa 1) — se añade la 5.ª pantalla

```
Figura 9
Prototipo 1: confirmación de reserva

[Imagen: P1-ConfirmacionReserva → exportar PNG desde Figma]

Nota. Elaboración propia en Figma. Con esta pantalla el Prototipo 1 alcanza las cinco pantallas requeridas: inicio de sesión, registro, catálogo, detalle y confirmación de reserva.
```

**Dónde insertar:** en 4.1, después de la actual Figura 5 (detalle de cancha).

### Prototipo 2 (Alternativa 3) — se añaden las pantallas 4.ª y 5.ª

```
Figura 13
Prototipo 2: formulario para añadir horario

[Imagen: P2-FormularioHorario → exportar PNG desde Figma]

Nota. Elaboración propia en Figma. El formulario permite al propietario registrar un nuevo bloque horario con su estado inicial (libre o bloqueado), cubriendo el RF12.
```

```
Figura 14
Prototipo 2: reportes e ingresos del propietario

[Imagen: P2-Reportes → exportar PNG desde Figma]

Nota. Elaboración propia en Figma. El reporte presenta indicadores de reservas, ocupación e ingresos, con exportación a CSV o PDF, cubriendo el objetivo específico 8.
```

**Dónde insertar:** en 4.2, después de la actual Figura 8 (historial de reservas).

### Prototipo 3 (Alternativa 2, definitivo) — se añaden 3 pantallas

```
Figura 19
Prototipo 3: resumen de reserva

[Imagen: P3-ResumenReserva → exportar PNG desde Figma]

Nota. Elaboración propia en Figma. Pantalla previa al pago que detalla cancha, fecha, horario, subtotal, comisión y total (pantalla 5 de la Tabla 12).
```

**Dónde insertar:** en 4.3, entre la actual Figura 12 (horarios y disponibilidad) y la actual Figura 13 (confirmación de pago).

```
Figura 22
Prototipo 3: creación de pichanga

[Imagen: P3-CrearPichanga → exportar PNG desde Figma]

Nota. Elaboración propia en Figma. El jugador con reserva confirmada publica una pichanga definiendo cancha, fecha, hora, cupos y precio por participante (RF06, RN17).
```

```
Figura 23
Prototipo 3: listado y unión a pichangas

[Imagen: P3-Pichangas → exportar PNG desde Figma]

Nota. Elaboración propia en Figma. El listado muestra pichangas abiertas con filtros y permite al jugador unirse mientras queden cupos (RF07, RN16 y RN18).
```

**Dónde insertar:** en 4.3, después de la actual Figura 14 (confirmación de reserva).

> **Nota para la Tabla 12:** añadir las filas "Resumen de reserva", "Crear pichanga" y "Listado y unión a pichangas" para reflejar las 9+ pantallas reales del prototipo definitivo.

---

## Sección 6 — Diseño Arquitectónico → Diagrama de clases

```
Figura 26
Diagrama de clases de PichangaApp

[Imagen: Diagrama_Clases_PichangaApp.drawio → exportar PNG]

Nota. Elaboración propia con draw.io. El diagrama muestra las entidades del dominio, los enumerados, las interfaces DAO que extienden JpaRepository y los controladores REST, aplicando los patrones MTV y DAO descritos en 6.1 y 6.2.
```

**Dónde insertar:** en la sección 6, después de la Tabla 14 (DAO definidos).

---

## Resumen de imágenes a exportar

| Archivo/Frame | Herramienta | Exportar como |
|---|---|---|
| `Diagrama_ER_PichangaApp.drawio` | app.diagrams.net | PNG (File → Export as) |
| `BPMN_P04_Reserva_Cancha_Pago.drawio` | app.diagrams.net | PNG |
| `BPMN_P01_Registro_Verificacion.drawio` | app.diagrams.net | PNG |
| `Diagrama_Clases_PichangaApp.drawio` | app.diagrams.net | PNG |
| `P1-ConfirmacionReserva` | Figma | PNG (2× para nitidez) |
| `P2-FormularioHorario` | Figma | PNG |
| `P2-Reportes` | Figma | PNG |
| `P3-ResumenReserva` | Figma | PNG |
| `P3-CrearPichanga` | Figma | PNG |
| `P3-Pichangas` | Figma | PNG |

---
*Nota: Elaboración propia.*