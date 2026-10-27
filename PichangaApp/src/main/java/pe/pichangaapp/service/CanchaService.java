package pe.pichangaapp.service;

import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.dto.BusquedaForm;
import pe.pichangaapp.dto.CanchaForm;

import java.util.List;

public interface CanchaService {

    List<Cancha> buscar(BusquedaForm filtros);

    Cancha obtener(Long id);

    Cancha crear(Long propietarioId, CanchaForm form);

    Cancha actualizar(Long id, Long propietarioId, CanchaForm form);

    void cambiarEstadoPropietario(Long id, Long propietarioId, boolean activa);

    void cambiarEstadoAdmin(Long id, boolean activa);

    List<Cancha> delPropietario(Long propietarioId);

    List<Cancha> activas();

    List<Cancha> todas();

    List<String> distritos();

    double promedioCalificacion(Long canchaId);

    long cantidadResenas(Long canchaId);

    CanchaForm formularioDe(Cancha cancha);

    boolean perteneceA(Long canchaId, Long propietarioId);
}
