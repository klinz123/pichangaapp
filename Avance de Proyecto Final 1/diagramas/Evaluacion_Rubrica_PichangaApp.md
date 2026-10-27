# Evaluación del informe PichangaApp contra la rúbrica

**Documento evaluado:** `Informe_Proyecto_PichangaApp_APA7_mejorado.docx`
**Fecha de evaluación:** Septiembre 2026

---

## Veredicto general

| Criterio de la rúbrica | Cumplimiento | Acción requerida |
|---|---|---|
| **1. Análisis del contexto + Canvas** | ✅ Cumple | Ninguna (solo verificar que la Figura 1 Canvas se vea bien al exportar). |
| **2. Tres alternativas, ≥5 pantallas c/u, ≥50 % Java** | ❌ No cumple | Reformular 2 alternativas con Java y añadir 5 pantallas de mockup. |
| **3. Estructura con ≥3 diagramas** | ⚠️ Parcial (1 de 5) | Ya se entregaron: BPMN 2.0 (×2), Clases y ER en drawio + Project Charter. Ahora cumple 4 de 5. |
| **4. Prototipo 100 % alcance + herramienta de mockups** | ⚠️ Parcial | Añadir matriz de trazabilidad (ya entregada) + 5 pantallas faltantes. |

---

## Detalle por criterio

### Criterio 1 — Análisis del contexto de la empresa ✅

| Aspecto exigido | Dónde está en el informe | Cumple |
|---|---|---|
| Análisis del contexto | Sección 1.1 Descripción de la empresa (Tabla 2) | ✔ |
| Problema identificado y descrito | Sección 1.2 (bien descrito desde ambos lados: jugador y propietario) | ✔ |
| Visión | Sección 1.3 | ✔ |
| Misión | Sección 1.4 | ✔ |
| Entorno | Sección 1.5 (usuarios, hardware, software, restricciones) | ✔ |
| Estrategias | Sección 1.6 (Tabla 5, seis líneas estratégicas) | ✔ |
| Planes de la empresa | Sección 1.7 (corto, mediano, largo plazo) | ✔ |
| Canvas de modelo de negocio graficado | Sección 1.8, Figura 1 (imagen embebida) + Tabla 7 (9 bloques) | ✔ |
| Alcance funcional y no funcional | Tabla 8 (RF01–RF12) y Tabla 9 (RNF01–RNF10) | ✔ |

**Conclusión:** este criterio está completo y bien desarrollado.

---

### Criterio 2 — Tres alternativas, ≥5 pantallas cada una, ≥50 % Java ❌

**Lo que hay:** 3 alternativas con tabla comparativa (Tabla 10) y prototipos asociados. Bien planteado el proceso de descarte.

**Lo que NO cumple:**

| Requisito | Alternativa 1 (Híbrida RN+Capacitor) | Alternativa 2 (Web + API) | Alternativa 3 (Nat. Kotlin+Swift) |
|---|---|---|---|
| **≥ 5 pantallas** | ❌ Solo 4 (login, registro, catálogo, detalle) | ✔ 8 pantallas (Tabla 12) | ❌ Solo 3 (panel, parrilla, historial) |
| **≥ 50 % Java** | ❌ React Native/Capacitor = JavaScript | ⚠️ Backend Java (Spring Boot) pero no está declarado el % | ❌ Kotlin + Swift = 0 % Java |

**Cómo corregirlo (mínimo indispensable):**

1. **Reformular la Alternativa 1** como "aplicación web Java con **Spring MVC + Thymeleaf** (y Bootstrap)" → 100 % Java. O bien "JavaFX + API REST".
2. **Reformular la Alternativa 3** como "aplicación **Android nativa en Java** (con API REST en Java)" → 100 % Java (eliminar Kotlin y Swift). Es la forma más directa de cumplir el 50 %.
3. **Declarar explícitamente el % Java** en la Tabla 10 (por ejemplo: Alt 2 = "backend 100 % Java ≈ 50 % del código"). El docente necesita ver el porcentaje escrito.
4. **Añadir 5 pantallas** (ver matriz de trazabilidad, sección C): 1 al Prototipo 1, 2 al Prototipo 2 y 2 al Prototipo 3.

> ⚠️ **Inconsistencia a corregir:** la Sección 4 dice que los Prototipos 1 y 2 "exploran soluciones simples de la alternativa web", pero la Tabla 10 los asocia a las Alternativas 1 y 3. Uniformizar el texto con la tabla.

---

### Criterio 3 — Estructura de la solución con ≥3 diagramas ✅ (tras esta entrega)

La rúbrica pide **al menos 3** de: project charter básico, gantt chart, diagramas de procesos, diagramas de clases, diagrama ER.

**Estado anterior:** solo existía el Gantt (Figura 17) = 1 de 5.

**Se agregaron (carpeta `diagramas/`):**

| Entregable | Archivo | Formato |
|---|---|---|
| Diagrama de procesos BPMN 2.0 — P04 Reserva y pago | `BPMN_P04_Reserva_Cancha_Pago.drawio` | drawio |
| Diagrama de procesos BPMN 2.0 — P01 Registro y verificación | `BPMN_P01_Registro_Verificacion.drawio` | drawio |
| Diagrama de clases | `Diagrama_Clases_PichangaApp.drawio` | drawio |
| Diagrama Entidad-Relación (pata de cuervo) | `Diagrama_ER_PichangaApp.drawio` | drawio |
| Project Charter básico | `Project_Charter_PichangaApp.md` | Markdown |
| Diagrama de Gantt | ya existente (Figura 17 + PPTX en assets) | — |

Con esto el informe cumple **4 de los 5** documentos exigidos (Gantt, procesos BPMN, clases, ER) + el charter como bono.

**Cómo incorporarlos al informe:**
- Abrir cada `.drawio` en **app.diagrams.net**, ajustar si es necesario, y exportar como PNG (File → Export as → PNG).
- Insertar como nuevas figuras en la Sección 3 (Diseño de la Solución) con nota "Elaboración propia con draw.io (BPMN 2.0)".

**Nota de diseño:** el diagrama ER añade dos tablas que no estaban en la Tabla 11 (HORARIO y PICHANGA_PARTICIPANTE). Son necesarias para modelar correctamente RF04/RF05 (horarios) y RF07 (unión a pichangas N:M). Actualizar la Tabla 11 para incluirlas y quedar coherente.

---

### Criterio 4 — Prototipo que cubre el 100 % del alcance + herramienta de mockups ⚠️

**Lo que hay:**
- ✔ Prototipos elaborados en **Figma** (equivalente válido a Balsamiq, el informe lo declara en la Sección 4).
- ✔ Prototipo 3 (definitivo) recorre el flujo completo reserva + pago (Tabla 12: 8 pantallas).
- ❌ **No hay evidencia explícita** de que el prototipo cubra el 100 % del alcance: falta la matriz de trazabilidad RF → pantalla.

**Lo que se agregó:**
- `Matriz_Trazabilidad_RF_RNF_Prototipo.md` — tabla que mapea RF01–RF12 y RNF01–RNF10 contra pantallas. Insertarla en la Sección 4.

**Lo que aún falta hacer (manual, en Figma):**
1. Pantallas de **Crear pichanga** (RF06) y **Listado/unión a pichanga** (RF07) — hoy están comprometidas en el alcance pero sin mockup.
2. Pantallas de **Formulario añadir horario** y **Reportes del propietario** para el Prototipo 2.
3. Exportar las capturas reales de la Sección 5 ("Pantallazos Web" está prevista pero vacía).

---

## Resumen de acciones

1. **En el docx (texto):**
   - Reformular Alternativa 1 y 3 para que sean ≥50 % Java (Spring MVC/Thymeleaf y Android Java, respectivamente).
   - Escribir el % Java en la Tabla 10.
   - Corregir la incoherencia Prototipo ↔ Alternativa de la Sección 4.
   - Actualizar Tabla 11 (entidades) con HORARIO y PICHANGA_PARTICIPANTE.
2. **En Figma (mockups):** crear 5 pantallas nuevas (matriz sección C).
3. **En el informe (figuras):** insertar los 4 diagramas drawio exportados a PNG + el Project Charter + la matriz de trazabilidad.

---
*Nota: Elaboración propia.*