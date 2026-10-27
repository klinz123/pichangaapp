# PichangaApp — Sistema de diseño

## Mundo visual: «Césped de día»

Un marketplace de barrio visto a las 9 de la mañana: césped recién cortado, cielo limeño despejado, líneas blancas nítidas. **Claro, aireado y deportivo**, no corporativo. La energía viene del color y de la fotografía real, no de gradientes morados ni glassmorphism.

**Anti-referencias:** dashboards SaaS grises, tarjetas flotando sin jerarquía, degradados azul-violeta genéricos, iconos SVG de cancha sustituyendo fotos.

## Paleta

| Token | Hex | Uso |
|---|---|---|
| `--pa-tinta` | `#0C1F17` | Texto principal, titulares |
| `--pa-tinta-suave` | `#3D5548` | Párrafos |
| `--pa-tinta-tenue` | `#7A8F82` | Metadatos |
| `--pa-verde-700` | `#14683C` | Hover de primario, texto sobre claro |
| `--pa-verde-600` | `#17864B` | **Primario** (botones, enlaces) |
| `--pa-verde-500` | `#1FA35C` | Acentos, barras de progreso |
| `--pa-verde-100` | `#E7F6EC` | Fondos de badge y sección |
| `--pa-lima` | `#D8F36B` | Resalte deportivo (chips sobre foto, subrayados) |
| `--pa-cielo` | `#0B84C4` | Información, enlaces secundarios |
| `--pa-cielo-100` | `#E6F4FD` | Fondo informativo |
| `--pa-ambar` | `#E9A23B` | Estrellas y avisos |
| `--pa-arcilla` | `#C4552F` | Peligro / destructivo |
| `--pa-lienzo` | `#F6F8F3` | Fondo de página |
| `--pa-superficie` | `#FFFFFF` | Tarjetas |
| `--pa-linea` | `#E3E9E2` | Bordes |
| `--pa-linea-fuerte` | `#CBD6CB` | Bordes enfatizados |

Sobre foto se usa tinta `#08231A` con opacidad y texto blanco; nunca texto verde sobre césped.

## Tipografía

- **Display:** `Archivo` 700/800, tracking `-0.03em`, `line-height .95–1.05`. Titulares grandes (clamp 2.4rem → 4.4rem).
- **Texto:** `Inter` 400/500/600, `line-height 1.65`, medida máxima 68ch.
- **Datos:** cifras con `font-variant-numeric: tabular-nums` en precios y métricas.
- Escala: 0.75 / 0.875 / 1 / 1.125 / 1.375 / 1.75 / 2.25 / 3 / 4rem.
- Fuentes **locales** en `/fonts` con `font-display: swap`; fallback a system-ui.

## Espaciado y forma

- Escala 4 px: 4, 8, 12, 16, 24, 32, 48, 64, 96, 128.
- Contenedor 1200 px; gutters 24 px (16 px en móvil); `.contenedor-angosto` 760 px.
- Radios: 10 (controles), 16 (tarjetas), 24 (bloques), 999 (pills).
- Sombras suaves y cortas, nunca difusas a 40 px: `0 1px 2px`, `0 8px 24px -12px`.
- Secciones alternan lienzo y blanco; el ritmo vertical es 80/112 px.

## Fotografía

- **Real, diurna, horizontal**, césped o sintético con líneas visibles.
- Tratamiento: `object-fit: cover`, relación 16/10 en tarjetas y 21/9 en hero; velo verde-tinta al 45–65 % solo donde hay texto.
- Sin marcos redondeados exagerados; radio 16 en tarjetas, 24 en hero.
- Pie de página incluye crédito de imágenes (`/img/ATTRIBUTION.md`).

## Componentes clave

1. **Barra**: sticky, fondo blanco translúcido, borde inferior de 1 px; marca con balón; nav con subrayado animado; menú móvil desplegable.
2. **Hero**: foto a sangre + titular + CTA + **buscador real** (distrito / fecha / hora → `GET /canchas`), con tarjeta que solapa la sección siguiente.
3. **Tarjeta de cancha**: foto 16/10, chip de superficie, rating con estrella, dirección con pin, precio y CTA.
4. **Pichanga**: nivel como chip, barra de cupos, precio por jugador.
5. **Franja horaria**: botón de 44 px mínimo, libre/ocupada/seleccionada con color + texto, nunca solo color.
6. **Auth**: panel lateral con foto y mensaje; formulario en tarjeta blanca.

## Movimiento

- Duraciones 120–220 ms, `cubic-bezier(.2,.7,.3,1)`; entrada de secciones con `IntersectionObserver` y desplazamiento de 12 px.
- Hover: elevación de 2 px y sombra; nunca escalar más de 1.01.
- `prefers-reduced-motion: reduce` desactiva todo salvo cambios de opacidad.

## Accesibilidad

- Contraste mínimo 4.5:1 en texto, 3:1 en UI.
- Foco visible: anillo de 3 px `--pa-cielo` con offset 2 px.
- Un solo `h1` por página; `main#contenido` como destino del enlace "Saltar al contenido".
- Formularios con `<label>` asociado, `aria-live` en alertas, `aria-expanded` en menús.
- Objetivos táctiles ≥ 44×44 px.
