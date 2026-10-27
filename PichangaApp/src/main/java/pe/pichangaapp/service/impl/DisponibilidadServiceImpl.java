package pe.pichangaapp.service.impl;

import com.google.common.collect.Range;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.BloqueoHorarioDAO;
import pe.pichangaapp.dao.CanchaDAO;
import pe.pichangaapp.dao.HorarioDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.domain.BloqueoHorario;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.domain.EstadoReserva;
import pe.pichangaapp.domain.Horario;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.dto.BloqueoForm;
import pe.pichangaapp.dto.HorarioForm;
import pe.pichangaapp.dto.view.EstadoFranja;
import pe.pichangaapp.dto.view.FranjaHoraria;
import pe.pichangaapp.service.DisponibilidadService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisponibilidadServiceImpl implements DisponibilidadService {

    /** R02: solo las reservas pendientes o confirmadas ocupan la cancha. */
    private static final List<EstadoReserva> ESTADOS_VIGENTES =
            List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);

    private final CanchaDAO canchaDAO;
    private final HorarioDAO horarioDAO;
    private final BloqueoHorarioDAO bloqueoDAO;
    private final ReservaDAO reservaDAO;

    @Override
    @Transactional(readOnly = true)
    public List<FranjaHoraria> franjasDelDia(Long canchaId, LocalDate fecha) {
        Cancha cancha = canchaDAO.findById(canchaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cancha no existe."));
        Ventana ventana = ventanaDe(cancha, fecha);
        if (ventana == null) {
            return List.of();
        }

        List<Reserva> reservas = reservaDAO
                .findByCanchaIdAndFechaAndEstadoInOrderByHoraInicioAsc(canchaId, fecha, ESTADOS_VIGENTES);
        List<BloqueoHorario> bloqueos = bloqueoDAO
                .findByCanchaIdAndFechaOrderByHoraInicioAsc(canchaId, fecha);

        List<FranjaHoraria> franjas = new ArrayList<>();

        // Convertir a minutos desde medianoche para manejar cierre a las 00:00 (medianoche)
        int aperturaMin = ventana.apertura().getHour() * 60 + ventana.apertura().getMinute();
        int cierreMin = ventana.cierre().getHour() * 60 + ventana.cierre().getMinute();
        if (cierreMin == 0) {
            cierreMin = 24 * 60; // 00:00 se trata como fin del día (24:00)
        }

        int slotInicio = aperturaMin;
        while (slotInicio + 60 <= cierreMin) {
            LocalTime inicio = LocalTime.of(slotInicio / 60, slotInicio % 60);
            LocalTime fin = LocalTime.of((slotInicio + 60) / 60, (slotInicio + 60) % 60);
            Range<LocalTime> slot = Range.closedOpen(inicio, fin);
            EstadoFranja estado = EstadoFranja.LIBRE;
            String detalle = "Disponible";

            if (reservas.stream().anyMatch(r -> seSolapa(slot, r.getHoraInicio(), r.getHoraFin()))) {
                estado = EstadoFranja.RESERVADA;
                detalle = "Horario reservado";
            } else {
                for (BloqueoHorario bloqueo : bloqueos) {
                    if (seSolapa(slot, bloqueo.getHoraInicio(), bloqueo.getHoraFin())) {
                        estado = EstadoFranja.BLOQUEADA;
                        detalle = StringUtils.defaultIfBlank(bloqueo.getMotivo(), "Bloqueado por el propietario");
                        break;
                    }
                }
            }

            if (estado == EstadoFranja.LIBRE
                    && LocalDateTime.of(fecha, inicio).isBefore(LocalDateTime.now())) {
                estado = EstadoFranja.PASADA;
                detalle = "Horario pasado";
            }

            franjas.add(new FranjaHoraria(inicio, fin, estado, cancha.getPrecioHora(), detalle));
            slotInicio += 60;
        }
        return franjas;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaDisponible(Long canchaId, LocalDate fecha, LocalTime horaInicio) {
        if (canchaId == null || fecha == null || horaInicio == null) {
            return false;
        }
        Cancha cancha = canchaDAO.findById(canchaId).orElse(null);
        if (cancha == null || !cancha.isActiva()) {
            return false;
        }
        Ventana ventana = ventanaDe(cancha, fecha);
        if (ventana == null) {
            return false;
        }
        LocalDateTime inicio = LocalDateTime.of(fecha, horaInicio);
        if (inicio.isBefore(LocalDateTime.now())) {
            return false;
        }
        if (horaInicio.isBefore(ventana.apertura())
                || horaInicio.plusHours(1).isAfter(ventana.cierre())) {
            return false;
        }
        if (horaInicio.getMinute() != 0 || horaInicio.getSecond() != 0) {
            return false;
        }

        Range<LocalTime> slot = Range.closedOpen(horaInicio, horaInicio.plusHours(1));
        boolean ocupado = reservaDAO
                .findByCanchaIdAndFechaAndEstadoInOrderByHoraInicioAsc(canchaId, fecha, ESTADOS_VIGENTES)
                .stream().anyMatch(r -> seSolapa(slot, r.getHoraInicio(), r.getHoraFin()));
        if (ocupado) {
            return false;
        }
        return bloqueoDAO.findByCanchaIdAndFechaOrderByHoraInicioAsc(canchaId, fecha).stream()
                .noneMatch(b -> seSolapa(slot, b.getHoraInicio(), b.getHoraFin()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horario> horarios(Long canchaId) {
        return horarioDAO.findByCanchaIdOrderByDiaSemanaAsc(canchaId);
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioForm horarioDe(Long canchaId, int diaSemana) {
        Cancha cancha = canchaDAO.findById(canchaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cancha no existe."));
        return horarioDAO.findByCanchaIdAndDiaSemana(canchaId, diaSemana)
                .map(h -> {
                    HorarioForm form = new HorarioForm();
                    form.setDiaSemana(h.getDiaSemana());
                    form.setHoraApertura(h.getHoraApertura());
                    form.setHoraCierre(h.getHoraCierre());
                    form.setActivo(h.isActivo());
                    return form;
                })
                .orElseGet(() -> {
                    HorarioForm form = new HorarioForm();
                    form.setDiaSemana(diaSemana);
                    form.setHoraApertura(cancha.getHoraApertura());
                    form.setHoraCierre(cancha.getHoraCierre());
                    form.setActivo(true);
                    return form;
                });
    }

    @Override
    @Transactional
    public void guardarHorario(Long canchaId, Long propietarioId, HorarioForm form) {
        Cancha cancha = canchaDelPropietario(canchaId, propietarioId);
        if (!form.isHorarioValido()) {
            throw new ReglaNegocioException("La hora de apertura debe ser anterior a la hora de cierre.");
        }
        if (form.getDiaSemana() < 1 || form.getDiaSemana() > 7) {
            throw new ReglaNegocioException("Día de la semana inválido.");
        }
        Horario horario = horarioDAO.findByCanchaIdAndDiaSemana(canchaId, form.getDiaSemana())
                .orElseGet(() -> {
                    Horario nuevo = new Horario();
                    nuevo.setCancha(cancha);
                    nuevo.setDiaSemana(form.getDiaSemana());
                    return nuevo;
                });
        horario.setHoraApertura(form.getHoraApertura());
        horario.setHoraCierre(form.getHoraCierre());
        horario.setActivo(form.isActivo());
        horarioDAO.save(horario);
    }

    @Override
    @Transactional
    public void eliminarHorario(Long canchaId, Long propietarioId, int diaSemana) {
        canchaDelPropietario(canchaId, propietarioId);
        horarioDAO.findByCanchaIdAndDiaSemana(canchaId, diaSemana).ifPresent(horarioDAO::delete);
    }

    @Override
    @Transactional
    public void bloquear(Long canchaId, Long propietarioId, BloqueoForm form) {
        Cancha cancha = canchaDelPropietario(canchaId, propietarioId);
        if (form.getFecha() == null || form.getHoraInicio() == null) {
            throw new ReglaNegocioException("Selecciona fecha y hora para el bloqueo.");
        }
        LocalDateTime inicio = LocalDateTime.of(form.getFecha(), form.getHoraInicio());
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("No puedes bloquear un horario que ya pasó.");
        }
        Ventana ventana = ventanaDe(cancha, form.getFecha());
        if (ventana == null || form.getHoraInicio().isBefore(ventana.apertura())
                || form.getHoraInicio().plusHours(1).isAfter(ventana.cierre())) {
            throw new ReglaNegocioException("El horario está fuera del horario de atención.");
        }
        if (reservaDAO.existsByCanchaIdAndFechaAndHoraInicioAndEstadoIn(
                canchaId, form.getFecha(), form.getHoraInicio(), ESTADOS_VIGENTES)) {
            throw new ReglaNegocioException("No puedes bloquear un horario que ya tiene una reserva.");
        }
        if (bloqueoDAO.existsByCanchaIdAndFechaAndHoraInicio(canchaId, form.getFecha(), form.getHoraInicio())) {
            throw new ReglaNegocioException("Ese horario ya está bloqueado.");
        }
        BloqueoHorario bloqueo = new BloqueoHorario();
        bloqueo.setCancha(cancha);
        bloqueo.setFecha(form.getFecha());
        bloqueo.setHoraInicio(form.getHoraInicio());
        bloqueo.setHoraFin(form.getHoraInicio().plusHours(1));
        bloqueo.setMotivo(StringUtils.abbreviate(
                StringUtils.defaultIfBlank(form.getMotivo(), "Bloqueado por el propietario"), 200));
        bloqueoDAO.save(bloqueo);
    }

    @Override
    @Transactional
    public void desbloquear(Long canchaId, Long propietarioId, LocalDate fecha, LocalTime horaInicio) {
        canchaDelPropietario(canchaId, propietarioId);
        BloqueoHorario bloqueo = bloqueoDAO.findByCanchaIdAndFechaAndHoraInicio(canchaId, fecha, horaInicio)
                .orElseThrow(() -> new RecursoNoEncontradoException("El bloqueo no existe."));
        bloqueoDAO.delete(bloqueo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueoHorario> bloqueosDelDia(Long canchaId, LocalDate fecha) {
        return bloqueoDAO.findByCanchaIdAndFechaOrderByHoraInicioAsc(canchaId, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public int horasDisponibles(Long canchaId, LocalDate fecha) {
        return (int) franjasDelDia(canchaId, fecha).stream().filter(FranjaHoraria::isLibre).count();
    }

    private Cancha canchaDelPropietario(Long canchaId, Long propietarioId) {
        Cancha cancha = canchaDAO.findById(canchaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La cancha no existe."));
        if (!cancha.getPropietario().getId().equals(propietarioId)) {
            throw new RecursoNoEncontradoException("La cancha no pertenece a tu cuenta.");
        }
        return cancha;
    }

    private Ventana ventanaDe(Cancha cancha, LocalDate fecha) {
        Horario horario = horarioDAO.findByCanchaIdAndDiaSemana(cancha.getId(), fecha.getDayOfWeek().getValue())
                .orElse(null);
        if (horario != null) {
            if (!horario.isActivo()) {
                return null;
            }
            return new Ventana(horario.getHoraApertura(), horario.getHoraCierre());
        }
        return new Ventana(cancha.getHoraApertura(), cancha.getHoraCierre());
    }

    private boolean seSolapa(Range<LocalTime> slot, LocalTime inicio, LocalTime fin) {
        if (inicio == null || fin == null) {
            return false;
        }
        return slot.isConnected(Range.closedOpen(inicio, fin));
    }

    private record Ventana(LocalTime apertura, LocalTime cierre) {
    }
}
