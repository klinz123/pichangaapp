package pe.pichangaapp.dto.view;

/**
 * Punto de una serie temporal para los gráficos del dashboard.
 * La altura llega ya calculada (0-100) para que la plantilla no haga matemáticas.
 */
public record PuntoSerie(String etiqueta, long valor, int altura, boolean actual) {
}
