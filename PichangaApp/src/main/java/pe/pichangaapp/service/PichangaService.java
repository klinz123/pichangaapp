package pe.pichangaapp.service;

import pe.pichangaapp.domain.NivelPichanga;
import pe.pichangaapp.domain.Pichanga;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.dto.PichangaForm;

import java.time.LocalDate;
import java.util.List;

public interface PichangaService {

    Pichanga crear(Long organizadorId, PichangaForm form);

    Pichanga obtenerCompleta(Long id);

    List<Pichanga> listarPublicadas(String distrito, LocalDate fecha, NivelPichanga nivel, String texto);

    List<Pichanga> delUsuario(Long usuarioId);

    List<Pichanga> organizadasPor(Long usuarioId);

    void unirse(Long pichangaId, Long usuarioId);

    void salir(Long pichangaId, Long usuarioId);

    void cancelar(Long pichangaId, Long organizadorId);

    int finalizarVencidas();

    List<Reserva> reservasDisponiblesParaCrear(Long usuarioId);

    List<Usuario> participantes(Long pichangaId);

    boolean esParticipante(Long pichangaId, Long usuarioId);
}
