# PichangaApp — Contexto de producto

## Qué es

Marketplace peruano para **reservar canchas deportivas por hora** y **organizar pichangas** (partidos amateur) con cupos abiertos. Proyecto académico de Ingeniería de Software, construido con Spring Boot 3 + Thymeleaf + SQL Server.

## Para quién

| Persona | Necesita | Éxito |
|---|---|---|
| **Jugador** (18–35, Lima) | Encontrar cancha disponible hoy o mañana cerca de casa, pagar sin llamar a nadie | Reservar en menos de 2 minutos |
| **Propietario** de complejo | Publicar horarios, cobrar, reducir ausencias | Ver su agenda llena y cobrada |
| **Administrador** | Moderar canchas, usuarios y reservas | Operar sin planillas paralelas |

## Trabajos por resolver

1. "¿Qué canchas hay libres en mi distrito a las 8 de la noche?" → filtros por distrito, fecha y hora.
2. "No completo equipo" → pichangas abiertas con cupos y barra de ocupación.
3. "No quiero coordinar por WhatsApp" → reserva, pago y notificaciones en un solo flujo.
4. "Quiero llenar mis horas muertas" → panel del propietario con horarios y reservas.

## Superficies existentes

`/` home · `/canchas` catálogo + filtros · `/canchas/{id}` detalle con parrilla de horarios · `/pichangas` comunidad · `/login` `/registro` · `/perfil` · `/mis-reservas` · `/panel` (propietario) · `/admin` · `/pagos` · `/notificaciones` · `/calificaciones` · páginas de error 400/403/404/5xx.

## Modo de diseño

- **Home = Persuade.** Debe vender la reserva en el primer viewport y demostrar que hay canchas reales disponibles.
- **Catálogo y detalle = Operate.** Escaneo rápido, jerarquía clara, decisiones sin fricción.
- **Auth y paneles = Operate.** Consistencia, formularios legibles, estados de error explícitos.

## Verdad del producto (no inventar)

- Moneda **S/ (PEN)**, zona horaria **America/Lima**, distritos reales de Lima en el seed.
- Medios de pago mostrados: **Yape, Plin, tarjeta, Mercado Pago**.
- Reserva con expiración de **15 minutos**, cancelación con **2 horas** de anticipación, reembolso **80%**.
- Cuentas demo visibles en `/login`: jugador, propietario y admin.
- Las fotos de canchas son **ilustrativas** (banco de imágenes con licencia), no fotos de los locales reales. Debe indicarse en el pie.

## Restricciones técnicas

- Thymeleaf con layout `layout/base :: pagina(titulo, contenido)`; cada vista declara su `<main id="contenido">`.
- Bootstrap 5 y Bootstrap Icons se sirven **localmente** desde `/vendor`.
- El frontend debe funcionar **sin conexión a internet** (fotos y fuentes locales).
- No romper contratos existentes: nombres de campos de formulario, rutas, `sec:` y variables de modelo.
