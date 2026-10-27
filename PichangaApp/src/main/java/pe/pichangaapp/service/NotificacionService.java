package pe.pichangaapp.service;

import pe.pichangaapp.domain.Notificacion;
import pe.pichangaapp.domain.TipoNotificacion;
import pe.pichangaapp.domain.Usuario;

import java.util.List;

public interface NotificacionService {

    void enviar(Usuario usuario, TipoNotificacion tipo, String titulo, String contenido, String enlace);

    List<Notificacion> listar(Long usuarioId);

    List<Notificacion> ultimas(Long usuarioId);

    long contarNoLeidas(Long usuarioId);

    void marcarLeida(Long notificacionId, Long usuarioId);

    void marcarTodasLeidas(Long usuarioId);
}
