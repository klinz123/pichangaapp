package pe.pichangaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.CalificacionDAO;
import pe.pichangaapp.dao.CanchaDAO;
import pe.pichangaapp.dao.PagoDAO;
import pe.pichangaapp.dao.PichangaDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.dao.UsuarioDAO;
import pe.pichangaapp.domain.*;
import pe.pichangaapp.service.DisponibilidadService;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaServiceImpl.class);
    private static final List<EstadoReserva> VIGENTES =
            List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);
    private static final List<EstadoReserva> TODAS = List.of(EstadoReserva.values());
    private static final List<EstadoReserva> CONTABLES =
            List.of(EstadoReserva.CONFIRMADA, EstadoReserva.COMPLETADA);
    private static final List<EstadoPichanga> PICHANGAS_ACTIVAS =
            List.of(EstadoPichanga.ABIERTA, EstadoPichanga.LLENA);
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ReservaDAO reservaDAO;
    private final CanchaDAO canchaDAO;
    private final UsuarioDAO usuarioDAO;
    private final PagoDAO pagoDAO;
    private final CalificacionDAO calificacionDAO;
    private final PichangaDAO pichangaDAO;
    private final DisponibilidadService disponibilidadService;
    private final NotificacionService notificacionService;

    @Value("${app.reserva.minutos-expiracion:15}")
    private int minutosExpiracion;

    @Value("${app.reserva.horas-minimas-cancelacion:2}")
    private int horasMinimasCancelacion;

    @Value("${app.reserva.porcentaje-reembolso:80}")
    private int porcentajeReembolso;

    @Value("${app.reserva.horas-anticipacion-maxima:60}")
    private int horasAnticipacionMaxima;

    @Override
    @Transactional
    public Reserva crear(Long usuarioId, Long canchaId, LocalDate fecha, LocalTime horaInicio) {
        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
        Cancha cancha = canchaDAO.findById(canchaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cancha no existe."));

        if (!cancha.isActiva()) {
            throw new ReglaNegocioException("La cancha no está disponible para reservas.");
        }
        if (fecha == null || horaInicio == null) {
            throw new ReglaNegocioException("Selecciona la fecha y el horario que deseas reservar.");
        }
        LocalDateTime inicio = LocalDateTime.of(fecha, horaInicio);
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("No puedes reservar un horario que ya pasó.");
        }
        if (fecha.isAfter(LocalDate.now().plusDays(horasAnticipacionMaxima / 24))) {
            throw new ReglaNegocioException("Solo puedes reservar con hasta "
                    + (horasAnticipacionMaxima / 24) + " días de anticipación.");
        }
        if (!disponibilidadService.estaDisponible(canchaId, fecha, horaInicio)) {
            throw new ReglaNegocioException("Ese horario ya no está disponible. Elige otra franja.");
        }

        Reserva reserva = new Reserva();
        reserva.setCodigo(generarCodigo());
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaInicio.plusHours(1));
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(cancha.getPrecioHora());
        reserva.setExpiraEn(LocalDateTime.now().plusMinutes(minutosExpiracion));

        try {
            reserva = reservaDAO.saveAndFlush(reserva);
        } catch (DataIntegrityViolationException ex) {
            // R02: la restriccion unica de la base de datos evita la doble reserva concurrente.
            log.warn("Colision de horario cancha={} fecha={} hora={}", canchaId, fecha, horaInicio);
            throw new ReglaNegocioException("Otro jugador acaba de reservar ese horario. Elige otra franja.");
        }

        notificacionService.enviar(usuario, TipoNotificacion.RESERVA, "Reserva iniciada",
                "Reserva " + reserva.getCodigo() + " en " + cancha.getNombre()
                        + ". Tienes " + minutosExpiracion + " minutos para completar el pago.",
                "/pagos/" + reserva.getId());
        return reserva;
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerDetalle(Long reservaId, Long usuarioId, boolean admin) {
        Reserva reserva = reservaDAO.findCompletaPorId(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no existe."));
        boolean esDueno = reserva.getUsuario().getId().equals(usuarioId);
        boolean esPropietario = reserva.getCancha().getPropietario().getId().equals(usuarioId);
        if (!esDueno && !esPropietario && !admin) {
            throw new RecursoNoEncontradoException("La reserva no existe.");
        }
        return reserva;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> delUsuario(Long usuarioId) {
        return reservaDAO.findByUsuarioIdOrderByFechaDescHoraInicioDesc(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> delPropietario(Long propietarioId, LocalDate desde, LocalDate hasta) {
        return reservaDAO.listarDelPropietarioEnRango(propietarioId, desde, hasta, TODAS);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> reservasEnRango(LocalDate desde, LocalDate hasta) {
        return reservaDAO.listarEnRango(desde, hasta, CONTABLES);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTodas() {
        return reservaDAO.count();
    }

    @Override
    @Transactional
    public void cancelarPorJugador(Long reservaId, Long usuarioId) {
        Reserva reserva = reservaDAO.findCompletaPorId(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no existe."));
        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new RecursoNoEncontradoException("La reserva no existe.");
        }
        if (!reserva.estaVigente()) {
            throw new ReglaNegocioException("Esta reserva ya no se puede cancelar.");
        }
        if (pichangaDAO.existsByReservaIdAndEstadoIn(reservaId, PICHANGAS_ACTIVAS)) {
            throw new ReglaNegocioException("No puedes cancelar esta reserva porque tiene una pichanga activa. Cancela primero la pichanga.");
        }
        if (!reserva.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("El partido ya comenzó; no es posible cancelar.");
        }

        BigDecimal reembolso = BigDecimal.ZERO;
        Pago pago = pagoDAO.findByReservaId(reservaId).orElse(null);
        if (pago != null && pago.getEstado() == EstadoPago.PAGADO) {
            long horas = Duration.between(LocalDateTime.now(), reserva.getFechaHoraInicio()).toHours();
            if (horas >= horasMinimasCancelacion) {
                // R06: con 2 horas o mas de anticipacion se devuelve el 80 %.
                reembolso = pago.getMonto()
                        .multiply(BigDecimal.valueOf(porcentajeReembolso))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                pago.setMontoReembolsado(reembolso);
                pago.setEstado(EstadoPago.REEMBOLSADO_PARCIAL);
                pagoDAO.save(pago);
            }
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaDAO.save(reserva);

        String mensaje = reembolso.signum() > 0
                ? "Se registrará un reembolso de S/ " + reembolso + " según la política de cancelación."
                : "La cancelación se registró sin reembolso (menos de "
                    + horasMinimasCancelacion + " horas de anticipación).";
        notificacionService.enviar(reserva.getUsuario(), TipoNotificacion.RESERVA, "Reserva cancelada",
                "La reserva " + reserva.getCodigo() + " fue cancelada. " + mensaje, "/mis-reservas");
        notificacionService.enviar(reserva.getCancha().getPropietario(), TipoNotificacion.RESERVA,
                "Reserva cancelada",
                reserva.getUsuario().getNombre() + " canceló la reserva " + reserva.getCodigo()
                        + " del " + reserva.getFecha() + " a las " + reserva.getHoraInicio() + ".",
                "/panel/reservas");
        log.info("Reserva cancelada id={} reembolso={}", reservaId, reembolso);
    }

    @Override
    @Transactional
    public void cancelarPorPropietario(Long reservaId, Long propietarioId) {
        Reserva reserva = reservaDAO.findCompletaPorId(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no existe."));
        if (!reserva.getCancha().getPropietario().getId().equals(propietarioId)) {
            throw new RecursoNoEncontradoException("La reserva no pertenece a tus canchas.");
        }
        if (!reserva.estaVigente()) {
            throw new ReglaNegocioException("Esta reserva ya no se puede cancelar.");
        }
        if (pichangaDAO.existsByReservaIdAndEstadoIn(reservaId, PICHANGAS_ACTIVAS)) {
            throw new ReglaNegocioException("No puedes cancelar esta reserva porque tiene una pichanga activa. El organizador debe cancelar la pichanga primero.");
        }

        Pago pago = pagoDAO.findByReservaId(reservaId).orElse(null);
        if (pago != null && pago.getEstado() == EstadoPago.PAGADO) {
            // R08: si el propietario cancela, el reembolso es del 100 %.
            pago.setMontoReembolsado(pago.getMonto());
            pago.setEstado(EstadoPago.REEMBOLSADO);
            pagoDAO.save(pago);
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaDAO.save(reserva);

        notificacionService.enviar(reserva.getUsuario(), TipoNotificacion.RESERVA,
                "Reserva cancelada por el complejo",
                "El complejo canceló tu reserva " + reserva.getCodigo()
                        + ". Recibirás el reembolso completo y puedes volver a reservar.",
                "/canchas");
        log.info("Reserva cancelada por propietario id={} owner={}", reservaId, propietarioId);
    }

    @Override
    @Transactional
    public int expirarPendientes() {
        List<Reserva> vencidas = reservaDAO.findByEstadoAndExpiraEnBefore(
                EstadoReserva.PENDIENTE, LocalDateTime.now());
        for (Reserva reserva : vencidas) {
            reserva.setEstado(EstadoReserva.EXPIRADA);
            reservaDAO.save(reserva);
            notificacionService.enviar(reserva.getUsuario(), TipoNotificacion.RESERVA, "Reserva expirada",
                    "La reserva " + reserva.getCodigo() + " expiró porque no se completó el pago.",
                    "/reservas/mis-reservas");
        }
        if (!vencidas.isEmpty()) {
            log.info("Reservas expiradas automaticamente: {}", vencidas.size());
        }
        return vencidas.size();
    }

    @Override
    @Transactional
    public int completarFinalizadas() {
        LocalDate hoy = LocalDate.now();
        List<Reserva> candidatas = reservaDAO.listarConfirmadasEnRango(hoy.minusDays(7), hoy);
        int completadas = 0;
        for (Reserva reserva : candidatas) {
            if (reserva.getFechaHoraFin().isBefore(LocalDateTime.now())) {
                reserva.setEstado(EstadoReserva.COMPLETADA);
                reservaDAO.save(reserva);
                completadas++;
                notificacionService.enviar(reserva.getUsuario(), TipoNotificacion.CALIFICACION,
                        "¿Cómo estuvo tu pichanga?",
                        "Califica la cancha " + reserva.getCancha().getNombre()
                                + " y ayuda a otros jugadores a elegir mejor.",
                        "/calificaciones/" + reserva.getId());
            }
        }
        return completadas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> reservasCalificables(Long usuarioId) {
        return reservaDAO.findByUsuarioIdOrderByFechaDescHoraInicioDesc(usuarioId).stream()
                .filter(r -> (r.getEstado() == EstadoReserva.COMPLETADA
                        || r.getEstado() == EstadoReserva.CONFIRMADA))
                .filter(r -> r.getFechaHoraFin().isBefore(LocalDateTime.now()))
                .filter(r -> !calificacionDAO.existsByReservaId(r.getId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> reservasParaPichanga(Long usuarioId) {
        return reservaDAO.findByUsuarioIdAndEstadoInOrderByFechaAscHoraInicioAsc(
                        usuarioId, List.of(EstadoReserva.CONFIRMADA)).stream()
                .filter(r -> r.getFechaHoraInicio().isAfter(LocalDateTime.now()))
                .filter(r -> !pichangaDAO.existsByReservaId(r.getId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean puedeCalificar(Long reservaId, Long usuarioId) {
        Reserva reserva = reservaDAO.findById(reservaId).orElse(null);
        if (reserva == null || !reserva.getUsuario().getId().equals(usuarioId)) {
            return false;
        }
        if (reserva.getEstado() != EstadoReserva.COMPLETADA
                && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            return false;
        }
        return reserva.getFechaHoraFin().isBefore(LocalDateTime.now())
                && !calificacionDAO.existsByReservaId(reservaId);
    }

    private String generarCodigo() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        for (int intento = 0; intento < 8; intento++) {
            StringBuilder sufijo = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sufijo.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
            }
            String codigo = "PIC-" + fecha + "-" + sufijo;
            if (!reservaDAO.existsByCodigo(codigo)) {
                return codigo;
            }
        }
        throw new ReglaNegocioException("No se pudo generar el código de reserva. Intenta nuevamente.");
    }
}
