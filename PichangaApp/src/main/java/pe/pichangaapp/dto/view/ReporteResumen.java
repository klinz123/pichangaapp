package pe.pichangaapp.dto.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ReporteResumen(
        LocalDate desde,
        LocalDate hasta,
        BigDecimal ingresos,
        long reservas,
        long canceladas,
        double ocupacion,
        double calificacionPromedio,
        List<SerieDia> serie,
        List<IngresoCancha> porCancha,
        Map<String, Long> metodosPago
) {

    public record SerieDia(LocalDate fecha, String etiqueta, long reservas, BigDecimal ingresos) {
    }

    public record IngresoCancha(String cancha, long reservas, BigDecimal ingresos) {
    }
}
