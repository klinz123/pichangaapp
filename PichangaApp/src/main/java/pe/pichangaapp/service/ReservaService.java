package pe.pichangaapp.service;

import pe.pichangaapp.domain.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaService {

    Reserva crear(Long usuarioId, Long canchaId, LocalDate fecha, LocalTime horaInicio);

    Reserva obtenerDetalle(Long reservaId, Long usuarioId, boolean admin);

    List<Reserva> delUsuario(Long usuarioId);

    List<Reserva> delPropietario(Long propietarioId, LocalDate desde, LocalDate hasta);

    /** Reservas confirmadas o completadas de todo el sistema en un rango de fechas. */
    List<Reserva> reservasEnRango(LocalDate desde, LocalDate hasta);

    /** Total histórico de reservas registradas. */
    long contarTodas();

    void cancelarPorJugador(Long reservaId, Long usuarioId);

    void cancelarPorPropietario(Long reservaId, Long propietarioId);

    int expirarPendientes();

    int completarFinalizadas();

    List<Reserva> reservasCalificables(Long usuarioId);

    List<Reserva> reservasParaPichanga(Long usuarioId);

    boolean puedeCalificar(Long reservaId, Long usuarioId);
}
