package pe.pichangaapp.service;

import pe.pichangaapp.domain.MetodoPago;
import pe.pichangaapp.domain.Pago;
import pe.pichangaapp.domain.Reserva;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PagoService {

    Pago obtenerPorReserva(Long reservaId);

    Pago registrarPago(Long reservaId, Long usuarioId, MetodoPago metodo, String referencia);

    boolean estaPagada(Reserva reserva);

    /** Ingresos netos cobrados al propietario en un rango de fechas. */
    BigDecimal ingresosNetos(Long propietarioId, LocalDate desde, LocalDate hasta);

    /** Ingresos netos cobrados en toda la plataforma en un rango de fechas. */
    BigDecimal ingresosNetosGlobales(LocalDate desde, LocalDate hasta);
}
