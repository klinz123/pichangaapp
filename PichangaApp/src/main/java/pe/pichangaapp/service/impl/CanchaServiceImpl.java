package pe.pichangaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.CanchaDAO;
import pe.pichangaapp.dao.UsuarioDAO;
import pe.pichangaapp.dao.spec.CanchaSpecifications;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.dto.BusquedaForm;
import pe.pichangaapp.dto.CanchaForm;
import pe.pichangaapp.dto.view.EstadoFranja;
import pe.pichangaapp.dto.view.FranjaHoraria;
import pe.pichangaapp.service.CanchaService;
import pe.pichangaapp.service.DisponibilidadService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CanchaServiceImpl implements CanchaService {

    private final CanchaDAO canchaDAO;
    private final UsuarioDAO usuarioDAO;
    private final DisponibilidadService disponibilidadService;

    @Override
    @Transactional(readOnly = true)
    public List<Cancha> buscar(BusquedaForm filtros) {
        BusquedaForm criterios = filtros == null ? new BusquedaForm() : filtros;
        List<Cancha> resultado = canchaDAO.findAll(CanchaSpecifications.conFiltros(criterios),
                Sort.by(Sort.Direction.ASC, "nombre"));

        if (criterios.getFecha() != null) {
            LocalDate fecha = criterios.getFecha();
            resultado = resultado.stream()
                    .filter(c -> disponibilidadService.franjasDelDia(c.getId(), fecha).stream()
                            .anyMatch(f -> f.getEstado() == EstadoFranja.LIBRE
                                    && (criterios.getHora() == null || f.getInicio().equals(criterios.getHora()))))
                    .toList();
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Cancha obtener(Long id) {
        return canchaDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cancha no existe."));
    }

    @Override
    @Transactional
    public Cancha crear(Long propietarioId, CanchaForm form) {
        Usuario propietario = usuarioDAO.findById(propietarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado."));
        if (propietario.getRol() != Rol.PROPIETARIO) {
            throw new ReglaNegocioException("Solo las cuentas de propietario pueden registrar canchas.");
        }
        if (!form.isHorarioValido()) {
            throw new ReglaNegocioException("La hora de apertura debe ser anterior a la hora de cierre.");
        }
        Cancha cancha = new Cancha();
        cancha.setPropietario(propietario);
        aplicar(form, cancha);
        return canchaDAO.save(cancha);
    }

    @Override
    @Transactional
    public Cancha actualizar(Long id, Long propietarioId, CanchaForm form) {
        Cancha cancha = obtenerDelPropietario(id, propietarioId);
        if (!form.isHorarioValido()) {
            throw new ReglaNegocioException("La hora de apertura debe ser anterior a la hora de cierre.");
        }
        aplicar(form, cancha);
        return canchaDAO.save(cancha);
    }

    @Override
    @Transactional
    public void cambiarEstadoPropietario(Long id, Long propietarioId, boolean activa) {
        Cancha cancha = obtenerDelPropietario(id, propietarioId);
        cancha.setActiva(activa);
        canchaDAO.save(cancha);
    }

    @Override
    @Transactional
    public void cambiarEstadoAdmin(Long id, boolean activa) {
        Cancha cancha = obtener(id);
        cancha.setActiva(activa);
        canchaDAO.save(cancha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cancha> delPropietario(Long propietarioId) {
        return canchaDAO.findByPropietarioIdOrderByNombreAsc(propietarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cancha> activas() {
        return canchaDAO.findByActivaTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cancha> todas() {
        return canchaDAO.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> distritos() {
        return canchaDAO.listarDistritos();
    }

    @Override
    @Transactional(readOnly = true)
    public double promedioCalificacion(Long canchaId) {
        Double promedio = canchaDAO.promedioCalificacion(canchaId);
        return promedio == null ? 0 : Math.round(promedio * 10) / 10.0;
    }

    @Override
    @Transactional(readOnly = true)
    public long cantidadResenas(Long canchaId) {
        return canchaDAO.contarCalificaciones(canchaId);
    }

    @Override
    public CanchaForm formularioDe(Cancha cancha) {
        CanchaForm form = new CanchaForm();
        form.setNombre(cancha.getNombre());
        form.setDescripcion(cancha.getDescripcion());
        form.setDireccion(cancha.getDireccion());
        form.setDistrito(cancha.getDistrito());
        form.setSuperficie(cancha.getSuperficie());
        form.setCapacidad(cancha.getCapacidad());
        form.setPrecioHora(cancha.getPrecioHora());
        form.setHoraApertura(cancha.getHoraApertura());
        form.setHoraCierre(cancha.getHoraCierre());
        form.setLatitud(cancha.getLatitud());
        form.setLongitud(cancha.getLongitud());
        form.setActiva(cancha.isActiva());
        return form;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean perteneceA(Long canchaId, Long propietarioId) {
        return canchaDAO.findById(canchaId)
                .map(c -> c.getPropietario().getId().equals(propietarioId))
                .orElse(false);
    }

    private Cancha obtenerDelPropietario(Long id, Long propietarioId) {
        Cancha cancha = obtener(id);
        if (!cancha.getPropietario().getId().equals(propietarioId)) {
            throw new RecursoNoEncontradoException("La cancha no pertenece a tu cuenta.");
        }
        return cancha;
    }

    private void aplicar(CanchaForm form, Cancha cancha) {
        cancha.setNombre(form.getNombre().trim());
        cancha.setDescripcion(form.getDescripcion());
        cancha.setDireccion(form.getDireccion().trim());
        cancha.setDistrito(form.getDistrito().trim());
        cancha.setSuperficie(form.getSuperficie());
        cancha.setCapacidad(form.getCapacidad());
        cancha.setPrecioHora(form.getPrecioHora());
        cancha.setHoraApertura(form.getHoraApertura());
        cancha.setHoraCierre(form.getHoraCierre());
        cancha.setLatitud(form.getLatitud());
        cancha.setLongitud(form.getLongitud());
        cancha.setActiva(form.isActiva());
    }
}
