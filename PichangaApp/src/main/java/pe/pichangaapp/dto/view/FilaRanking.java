package pe.pichangaapp.dto.view;

import java.math.BigDecimal;

/**
 * Fila de un ranking (canchas más reservadas) con su barra de progreso ya calculada.
 */
public record FilaRanking(String titulo, String subtitulo, long reservas, BigDecimal ingresos, int porcentaje) {
}
