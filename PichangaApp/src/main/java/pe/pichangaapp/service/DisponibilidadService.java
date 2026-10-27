package pe.pichangaapp.service;

import pe.pichangaapp.domain.BloqueoHorario;
import pe.pichangaapp.domain.Horario;
import pe.pichangaapp.dto.BloqueoForm;
import pe.pichangaapp.dto.HorarioForm;
import pe.pichangaapp.dto.view.FranjaHoraria;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DisponibilidadService {

    List<FranjaHoraria> franjasDelDia(Long canchaId, LocalDate fecha);

    boolean estaDisponible(Long canchaId, LocalDate fecha, LocalTime horaInicio);

    List<Horario> horarios(Long canchaId);

    HorarioForm horarioDe(Long canchaId, int diaSemana);

    void guardarHorario(Long canchaId, Long propietarioId, HorarioForm form);

    void eliminarHorario(Long canchaId, Long propietarioId, int diaSemana);

    void bloquear(Long canchaId, Long propietarioId, BloqueoForm form);

    void desbloquear(Long canchaId, Long propietarioId, LocalDate fecha, LocalTime horaInicio);

    List<BloqueoHorario> bloqueosDelDia(Long canchaId, LocalDate fecha);

    int horasDisponibles(Long canchaId, LocalDate fecha);
}
