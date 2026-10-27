package pe.pichangaapp.dto.view;

import pe.pichangaapp.domain.Cancha;

import java.math.BigDecimal;

/**
 * Vista aplanada de una cancha para listados y tarjetas: incluye el promedio
 * de calificaciones y el numero de resenas ya resueltos por el servicio, de
 * modo que las plantillas no disparen consultas adicionales.
 */
public record CanchaTarjeta(
        Long id,
        String nombre,
        String direccion,
        String distrito,
        String superficie,
        int capacidad,
        BigDecimal precioHora,
        double promedio,
        long resenas) {

    public static CanchaTarjeta de(Cancha cancha, double promedio, long resenas) {
        return new CanchaTarjeta(
                cancha.getId(),
                cancha.getNombre(),
                cancha.getDireccion(),
                cancha.getDistrito(),
                cancha.getSuperficie() == null ? "" : cancha.getSuperficie().getEtiqueta(),
                cancha.getCapacidad(),
                cancha.getPrecioHora(),
                promedio,
                resenas);
    }

    public String getUbicacion() {
        return distrito + " · " + direccion;
    }

    public String getPromedioTexto() {
        return String.format(java.util.Locale.ROOT, "%.1f", promedio);
    }

    /** Estrellas enteras redondeadas, para el componente de calificacion. */
    public int getEstrellas() {
        return (int) Math.round(promedio);
    }

    public String getResenasTexto() {
        return resenas == 1 ? "1 reseña" : resenas + " reseñas";
    }

    public boolean isSinResenas() {
        return resenas == 0;
    }
}
