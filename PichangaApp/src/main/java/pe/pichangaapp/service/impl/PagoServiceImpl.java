package pe.pichangaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.PagoDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.domain.*;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.PagoService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoServiceImpl.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PagoDAO pagoDAO;
    private final ReservaDAO reservaDAO;
    private final NotificacionService notificacionService;

    @Override
    @Transactional(readOnly = true)
    public Pago obtenerPorReserva(Long reservaId) {
        return pagoDAO.findByReservaId(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva aún no tiene un pago registrado."));
    }

    @Override
    @Transactional
    public Pago registrarPago(Long reservaId, Long usuarioId, MetodoPago metodo, String referencia) {
        if (metodo == null) {
            throw new ReglaNegocioException("Selecciona un método de pago.");
        }
        Reserva reserva = reservaDAO.findCompletaPorId(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no existe."));
        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new RecursoNoEncontradoException("La reserva no existe.");
        }

        Pago existente = pagoDAO.findByReservaId(reservaId).orElse(null);
        if (existente != null && existente.getEstado() == EstadoPago.PAGADO) {
            return existente;
        }
        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("La reserva ya se encuentra pagada.");
        }
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ReglaNegocioException("La reserva ya no está disponible para pago.");
        }
        if (reserva.getExpiraEn() != null && reserva.getExpiraEn().isBefore(LocalDateTime.now())) {
            reserva.setEstado(EstadoReserva.EXPIRADA);
            reservaDAO.save(reserva);
            throw new ReglaNegocioException("El tiempo para pagar expiró. Vuelve a seleccionar tu horario.");
        }

        Pago pago = existente != null ? existente : new Pago();
        pago.setReserva(reserva);
        pago.setMetodo(metodo);
        pago.setMonto(reserva.getPrecioTotal());
        pago.setMontoReembolsado(BigDecimal.ZERO);
        pago.setEstado(EstadoPago.PAGADO);
        pago.setFechaPago(LocalDateTime.now());
        pago.setReferencia(StringUtils.abbreviate(referencia, 120));
        pago.setTransaccionId(generarTransaccion(metodo));
        pago = pagoDAO.save(pago);

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setExpiraEn(null);
        reservaDAO.save(reserva);

        notificacionService.enviar(reserva.getUsuario(), TipoNotificacion.PAGO, "Pago confirmado",
                "Tu reserva " + reserva.getCodigo() + " quedó confirmada. Te esperamos en "
                        + reserva.getCancha().getNombre() + " el " + reserva.getFecha()
                        + " a las " + reserva.getHoraInicio() + ".",
                "/reservas/" + reserva.getId());
        notificacionService.enviar(reserva.getCancha().getPropietario(), TipoNotificacion.PAGO,
                "Nueva reserva pagada",
                reserva.getUsuario().getNombre() + " pagó S/ " + reserva.getPrecioTotal()
                        + " por " + reserva.getCancha().getNombre() + " (" + reserva.getFecha()
                        + " " + reserva.getHoraInicio() + ").",
                "/panel/reservas");
        log.info("Pago registrado reserva={} metodo={} monto={} transaccion={}",
                reserva.getCodigo(), metodo, pago.getMonto(), pago.getTransaccionId());
        return pago;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaPagada(Reserva reserva) {
        if (reserva == null || reserva.getId() == null) {
            return false;
        }
        return pagoDAO.findByReservaId(reserva.getId())
                .map(p -> p.getEstado() == EstadoPago.PAGADO
                        || p.getEstado() == EstadoPago.REEMBOLSADO_PARCIAL)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal ingresosNetos(Long propietarioId, LocalDate desde, LocalDate hasta) {
        return pagoDAO.sumarPagosNetos(propietarioId, desde, hasta);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal ingresosNetosGlobales(LocalDate desde, LocalDate hasta) {
        return pagoDAO.sumarPagosNetosGlobalEnRango(desde, hasta);
    }

    private String generarTransaccion(MetodoPago metodo) {
        String prefijo = switch (metodo) {
            case YAPE -> "YP";
            case PLIN -> "PL";
            case MERCADO_PAGO -> "MP";
            case PAYPAL -> "PP";
        };
        for (int intento = 0; intento < 10; intento++) {
            String numero = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
            String id = prefijo + "-" + System.currentTimeMillis() + "-" + numero;
            if (!pagoDAO.existsByTransaccionId(id)) {
                return id;
            }
        }
        throw new ReglaNegocioException("No se pudo generar la transacción de pago. Intenta nuevamente.");
    }
}
