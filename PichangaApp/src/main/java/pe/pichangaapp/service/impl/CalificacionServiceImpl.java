package pe.pichangaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.CalificacionDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.domain.Calificacion;
import pe.pichangaapp.domain.EstadoReserva;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.domain.TipoNotificacion;
import pe.pichangaapp.dto.CalificacionForm;
import pe.pichangaapp.service.CalificacionService;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionDAO calificacionDAO;
    private final ReservaDAO reservaDAO;
    private final NotificacionService notificacionService;

    @Override
    @Transactional
    public Calificacion registrar(Long usuarioId, CalificacionForm form) {
        Reserva reserva = reservaDAO.findCompletaPorId(form.getReservaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no existe."));
        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new RecursoNoEncontradoException("La reserva no existe.");
        }
        if (reserva.getEstado() != EstadoReserva.COMPLETADA
                && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("Solo puedes calificar una reserva que ya se jugó.");
        }
        if (!reserva.getFechaHoraFin().isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("Podrás calificar cuando termine tu reserva.");
        }
        if (calificacionDAO.existsByReservaId(reserva.getId())) {
            throw new ReglaNegocioException("Ya calificaste esta reserva.");
        }

        Calificacion calificacion = new Calificacion();
        calificacion.setUsuario(reserva.getUsuario());
        calificacion.setCancha(reserva.getCancha());
        calificacion.setReserva(reserva);
        calificacion.setPuntaje(form.getPuntaje());
        calificacion.setComentario(StringUtils.abbreviate(StringUtils.trimToNull(form.getComentario()), 800));
        try {
            calificacion = calificacionDAO.saveAndFlush(calificacion);
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("Ya calificaste esta reserva.");
        }

        notificacionService.enviar(reserva.getCancha().getPropietario(), TipoNotificacion.CALIFICACION,
                "Nueva calificación",
                reserva.getUsuario().getNombre() + " calificó " + reserva.getCancha().getNombre()
                        + " con " + form.getPuntaje() + " estrellas.",
                "/panel/canchas/" + reserva.getCancha().getId() + "/resenas");
        return calificacion;
    }

    @Override
    @Transactional
    public void responder(Long calificacionId, Long propietarioId, String respuesta) {
        Calificacion calificacion = calificacionDAO
                .findByIdAndCanchaPropietarioId(calificacionId, propietarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La calificación no existe."));
        calificacion.setRespuestaPropietario(StringUtils.abbreviate(respuesta, 800));
        calificacion.setFechaRespuesta(LocalDateTime.now());
        calificacionDAO.save(calificacion);
        notificacionService.enviar(calificacion.getUsuario(), TipoNotificacion.CALIFICACION,
                "El complejo respondió tu reseña",
                "Tu opinión sobre " + calificacion.getCancha().getNombre() + " recibió una respuesta.",
                "/canchas/" + calificacion.getCancha().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Calificacion> listarPorCancha(Long canchaId) {
        return calificacionDAO.findByCanchaIdOrderByFechaCreacionDesc(canchaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Calificacion> listarDelPropietario(Long propietarioId) {
        return calificacionDAO.listarDelPropietario(propietarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public double promedio(Long canchaId) {
        Double promedio = calificacionDAO.promedioPorCancha(canchaId);
        return promedio == null ? 0 : Math.round(promedio * 10) / 10.0;
    }

    @Override
    @Transactional(readOnly = true)
    public long cantidad(Long canchaId) {
        return calificacionDAO.countByCanchaId(canchaId);
    }
}
