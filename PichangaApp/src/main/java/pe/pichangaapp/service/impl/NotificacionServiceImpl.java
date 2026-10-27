package pe.pichangaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.NotificacionDAO;
import pe.pichangaapp.domain.Notificacion;
import pe.pichangaapp.domain.TipoNotificacion;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceImpl.class);

    private final NotificacionDAO notificacionDAO;

    @Override
    @Transactional
    public void enviar(Usuario usuario, TipoNotificacion tipo, String titulo, String contenido, String enlace) {
        if (usuario == null || usuario.getId() == null) {
            return;
        }
        Notificacion notificacion = new Notificacion(usuario, tipo, titulo, contenido, enlace);
        notificacionDAO.save(notificacion);
        log.info("Notificacion usuario={} tipo={} titulo={}", usuario.getEmail(), tipo, titulo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notificacion> listar(Long usuarioId) {
        return notificacionDAO.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notificacion> ultimas(Long usuarioId) {
        return notificacionDAO.findTop8ByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNoLeidas(Long usuarioId) {
        return notificacionDAO.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Override
    @Transactional
    public void marcarLeida(Long notificacionId, Long usuarioId) {
        Notificacion notificacion = notificacionDAO.findByIdAndUsuarioId(notificacionId, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La notificación no existe."));
        notificacion.setLeida(true);
        notificacionDAO.save(notificacion);
    }

    @Override
    @Transactional
    public void marcarTodasLeidas(Long usuarioId) {
        notificacionDAO.marcarTodasLeidas(usuarioId);
    }
}
