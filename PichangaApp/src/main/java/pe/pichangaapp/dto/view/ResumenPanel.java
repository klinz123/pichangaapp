package pe.pichangaapp.dto.view;

import pe.pichangaapp.domain.Reserva;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ResumenPanel(
        long reservasHoy,
        BigDecimal ingresosMes,
        long canchasActivas,
        double ocupacion,
        double calificacionPromedio,
        long pichangasAbiertas,
        List<Reserva> proximasReservas,
        Map<String, Long> reservasSemana
) {
}
