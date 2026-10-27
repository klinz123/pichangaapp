package pe.pichangaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.PichangaDAO;
import pe.pichangaapp.dao.PichangaParticipanteDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.dao.UsuarioDAO;
import pe.pichangaapp.domain.*;
import pe.pichangaapp.dto.PichangaForm;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.PichangaService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PichangaServiceImpl implements PichangaService {

    private static final Logger log = LoggerFactory.getLogger(PichangaServiceImpl.class);
    private static final List<EstadoReserva> RESERVAS_VIGENTES =
            List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);

    private final PichangaDAO pichangaDAO;
    private final PichangaParticipanteDAO participanteDAO;
    private final ReservaDAO reservaDAO;
    private final UsuarioDAO usuarioDAO;
    private final ReservaService reservaService;
    private final NotificacionService notificacionService;

    @Override
    @Transactional
    public Pichanga crear(Long organizadorId, PichangaForm form) {
        Usuario organizador = usuarioDAO.findById(organizadorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
        if (organizador.getFechaNacimiento() == null
                || Period.between(organizador.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
            throw new ReglaNegocioException("Debes ser mayor de 18 años para organizar una pichanga.");
        }
        if (form.getReservaId() == null) {
            throw new ReglaNegocioException("Selecciona la reserva que respalda la pichanga.");
        }
        Reserva reserva = reservaService.obtenerDetalle(form.getReservaId(), organizadorId, false);
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("Solo puedes publicar una pichanga con una reserva confirmada.");
        }
        if (!reserva.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("La reserva ya pasó; elige una reserva futura.");
        }
        if (pichangaDAO.existsByReservaId(reserva.getId())) {
            throw new ReglaNegocioException("Esa reserva ya tiene una pichanga publicada.");
        }
        if (form.getCuposTotal() > reserva.getCancha().getCapacidad()) {
            throw new ReglaNegocioException("Los cupos no pueden superar la capacidad de la cancha ("
                    + reserva.getCancha().getCapacidad() + ").");
        }
        if (form.getCuposTotal() < 2) {
            throw new ReglaNegocioException("La pichanga debe permitir al menos 2 jugadores.");
        }

        Pichanga pichanga = new Pichanga();
        pichanga.setOrganizador(organizador);
        pichanga.setCancha(reserva.getCancha());
        pichanga.setReserva(reserva);
        pichanga.setFecha(reserva.getFecha());
        pichanga.setHoraInicio(reserva.getHoraInicio());
        pichanga.setHoraFin(reserva.getHoraFin());
        pichanga.setCuposTotal(form.getCuposTotal());
        pichanga.setCuposOcupados(1);
        pichanga.setNivel(form.getNivel());
        pichanga.setDescripcion(StringUtils.abbreviate(form.getDescripcion(), 800));
        pichanga.setPrecioParticipante(form.getPrecioParticipante());
        pichanga.setEstado(EstadoPichanga.ABIERTA);
        pichanga = pichangaDAO.save(pichanga);

        participanteDAO.save(new PichangaParticipante(pichanga, organizador));
        log.info("Pichanga creada id={} organizador={} cupos={}",
                pichanga.getId(), organizadorId, pichanga.getCuposTotal());
        return pichanga;
    }

    @Override
    @Transactional(readOnly = true)
    public Pichanga obtenerCompleta(Long id) {
        return pichangaDAO.findCompletaPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La pichanga no existe."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pichanga> listarPublicadas(String distrito, LocalDate fecha, NivelPichanga nivel, String texto) {
        List<Pichanga> base = pichangaDAO
                .findByEstadoInAndFechaGreaterThanEqualOrderByFechaAscHoraInicioAsc(
                        List.of(EstadoPichanga.ABIERTA, EstadoPichanga.LLENA), LocalDate.now());
        String filtroTexto = StringUtils.trimToEmpty(texto).toLowerCase(Locale.ROOT);
        return base.stream()
                .filter(p -> StringUtils.isBlank(distrito)
                        || p.getCancha().getDistrito().equalsIgnoreCase(distrito))
                .filter(p -> fecha == null || p.getFecha().equals(fecha))
                .filter(p -> nivel == null || p.getNivel() == nivel)
                .filter(p -> filtroTexto.isEmpty()
                        || p.getCancha().getNombre().toLowerCase(Locale.ROOT).contains(filtroTexto)
                        || StringUtils.defaultString(p.getDescripcion()).toLowerCase(Locale.ROOT)
                            .contains(filtroTexto))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pichanga> delUsuario(Long usuarioId) {
        return participanteDAO.listarPichangasDelUsuario(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pichanga> organizadasPor(Long usuarioId) {
        return pichangaDAO.findByOrganizadorIdOrderByFechaDescHoraInicioDesc(usuarioId);
    }

    @Override
    @Transactional
    public void unirse(Long pichangaId, Long usuarioId) {
        Pichanga pichanga = pichangaDAO.findCompletaPorId(pichangaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La pichanga no existe."));
        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));

        if (pichanga.getEstado() == EstadoPichanga.CANCELADA) {
            throw new ReglaNegocioException("Esta pichanga fue cancelada por el organizador.");
        }
        if (pichanga.getEstado() == EstadoPichanga.FINALIZADA) {
            throw new ReglaNegocioException("Esta pichanga ya se jugó.");
        }
        if (pichanga.getEstado() != EstadoPichanga.ABIERTA) {
            throw new ReglaNegocioException("Esta pichanga ya completó sus cupos. Prueba con otra.");
        }
        if (!pichanga.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("La pichanga ya comenzó.");
        }
        if (participanteDAO.existsByPichangaIdAndUsuarioId(pichangaId, usuarioId)) {
            throw new ReglaNegocioException("Ya formas parte de esta pichanga.");
        }
        if (pichanga.getCuposOcupados() >= pichanga.getCuposTotal()) {
            actualizarEstadoLlena(pichanga);
            throw new ReglaNegocioException("La pichanga se llenó. Busca otra disponible.");
        }
        if (participanteDAO.contarConflictosHorario(usuarioId, pichangaId,
                pichanga.getFecha(), pichanga.getHoraInicio()) > 0) {
            throw new ReglaNegocioException("Ya participas en otra pichanga a la misma hora.");
        }
        if (reservaDAO.existsByUsuarioIdAndFechaAndHoraInicioAndEstadoIn(
                usuarioId, pichanga.getFecha(), pichanga.getHoraInicio(), RESERVAS_VIGENTES)) {
            throw new ReglaNegocioException("Tienes una reserva propia en ese mismo horario.");
        }

        try {
            participanteDAO.saveAndFlush(new PichangaParticipante(pichanga, usuario));
            pichanga.setCuposOcupados(pichanga.getCuposOcupados() + 1);
            if (pichanga.getCuposOcupados() >= pichanga.getCuposTotal()) {
                pichanga.setEstado(EstadoPichanga.LLENA);
            }
            pichangaDAO.saveAndFlush(pichanga);
        } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("Los cupos cambiaron mientras confirmabas. Intenta nuevamente.");
        }

        notificacionService.enviar(usuario, TipoNotificacion.PICHANGA, "Te uniste a una pichanga",
                "Estás dentro de la pichanga en " + pichanga.getCancha().getNombre() + " el "
                        + pichanga.getFecha() + " a las " + pichanga.getHoraInicio() + ".",
                "/pichangas/" + pichanga.getId());
        if (pichanga.getEstado() == EstadoPichanga.LLENA) {
            notificacionService.enviar(pichanga.getOrganizador(), TipoNotificacion.PICHANGA, "Pichanga completa",
                    "Tu pichanga en " + pichanga.getCancha().getNombre() + " completó los "
                            + pichanga.getCuposTotal() + " cupos.",
                    "/pichangas/" + pichanga.getId());
            notificarParticipantes(pichanga, "Pichanga completa",
                    "La pichanga del " + pichanga.getFecha() + " a las " + pichanga.getHoraInicio()
                            + " ya tiene todos sus cupos.");
        }
        log.info("Usuario {} se unio a pichanga {}", usuarioId, pichangaId);
    }

    @Override
    @Transactional
    public void salir(Long pichangaId, Long usuarioId) {
        Pichanga pichanga = pichangaDAO.findCompletaPorId(pichangaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La pichanga no existe."));
        PichangaParticipante participante = participanteDAO
                .findByPichangaIdAndUsuarioId(pichangaId, usuarioId)
                .orElseThrow(() -> new ReglaNegocioException("No participas en esta pichanga."));
        if (pichanga.getOrganizador().getId().equals(usuarioId)) {
            throw new ReglaNegocioException("Como organizador no puedes salir. Si ya no se juega, cancela la pichanga.");
        }
        if (pichanga.getEstado() == EstadoPichanga.CANCELADA
                || pichanga.getEstado() == EstadoPichanga.FINALIZADA) {
            throw new ReglaNegocioException("Esta pichanga ya no está activa.");
        }
        if (!pichanga.getFechaHoraInicio().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ReglaNegocioException("Ya falta menos de 2 horas para el partido, así que no puedes salir. Contacta al organizador.");
        }
        participanteDAO.delete(participante);
        pichanga.setCuposOcupados(Math.max(0, pichanga.getCuposOcupados() - 1));
        if (pichanga.getEstado() == EstadoPichanga.LLENA) {
            pichanga.setEstado(EstadoPichanga.ABIERTA);
        }
        pichangaDAO.save(pichanga);
        notificacionService.enviar(pichanga.getOrganizador(), TipoNotificacion.PICHANGA,
                "Un jugador salió de tu pichanga",
                "Quedan " + pichanga.getCuposDisponibles() + " cupos disponibles.",
                "/pichangas/" + pichanga.getId());
    }

    @Override
    @Transactional
    public void cancelar(Long pichangaId, Long organizadorId) {
        Pichanga pichanga = pichangaDAO.findCompletaPorId(pichangaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La pichanga no existe."));
        if (!pichanga.getOrganizador().getId().equals(organizadorId)) {
            throw new RecursoNoEncontradoException("La pichanga no existe.");
        }
        if (pichanga.getEstado() == EstadoPichanga.CANCELADA) {
            throw new ReglaNegocioException("La pichanga ya está cancelada.");
        }
        if (!pichanga.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("No puedes cancelar una pichanga que ya comenzó.");
        }
        pichanga.setEstado(EstadoPichanga.CANCELADA);
        pichangaDAO.save(pichanga);
        notificarParticipantes(pichanga, "Pichanga cancelada",
                "El organizador canceló la pichanga del " + pichanga.getFecha()
                        + " a las " + pichanga.getHoraInicio() + ".");
        log.info("Pichanga {} cancelada por {}", pichangaId, organizadorId);
    }

    @Override
    @Transactional
    public int finalizarVencidas() {
        List<Pichanga> vencidas = pichangaDAO.findVencidas(
                List.of(EstadoPichanga.ABIERTA, EstadoPichanga.LLENA),
                LocalDate.now(), LocalTime.now());
        for (Pichanga pichanga : vencidas) {
            pichanga.setEstado(EstadoPichanga.FINALIZADA);
            pichangaDAO.save(pichanga);
        }
        return vencidas.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> reservasDisponiblesParaCrear(Long usuarioId) {
        return reservaService.reservasParaPichanga(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> participantes(Long pichangaId) {
        return participanteDAO.findByPichangaIdOrderByFechaUnionAsc(pichangaId).stream()
                .map(PichangaParticipante::getUsuario)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esParticipante(Long pichangaId, Long usuarioId) {
        return participanteDAO.existsByPichangaIdAndUsuarioId(pichangaId, usuarioId);
    }

    private void actualizarEstadoLlena(Pichanga pichanga) {
        pichanga.setEstado(EstadoPichanga.LLENA);
        pichangaDAO.save(pichanga);
    }

    private void notificarParticipantes(Pichanga pichanga, String titulo, String contenido) {
        participanteDAO.findByPichangaIdOrderByFechaUnionAsc(pichanga.getId()).stream()
                .map(PichangaParticipante::getUsuario)
                .filter(u -> !u.getId().equals(pichanga.getOrganizador().getId()))
                .forEach(u -> notificacionService.enviar(u, TipoNotificacion.PICHANGA, titulo, contenido,
                        "/pichangas/" + pichanga.getId()));
    }
}
