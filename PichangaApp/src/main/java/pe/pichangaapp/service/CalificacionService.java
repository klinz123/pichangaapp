package pe.pichangaapp.service;

import pe.pichangaapp.domain.Calificacion;
import pe.pichangaapp.dto.CalificacionForm;

import java.util.List;

public interface CalificacionService {

    Calificacion registrar(Long usuarioId, CalificacionForm form);

    void responder(Long calificacionId, Long propietarioId, String respuesta);

    List<Calificacion> listarPorCancha(Long canchaId);

    List<Calificacion> listarDelPropietario(Long propietarioId);

    double promedio(Long canchaId);

    long cantidad(Long canchaId);
}
