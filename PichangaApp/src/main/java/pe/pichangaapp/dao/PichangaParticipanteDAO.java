package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.pichangaapp.domain.Pichanga;
import pe.pichangaapp.domain.PichangaParticipante;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PichangaParticipanteDAO extends JpaRepository<PichangaParticipante, Long> {

    long countByPichangaId(Long pichangaId);

    boolean existsByPichangaIdAndUsuarioId(Long pichangaId, Long usuarioId);

    boolean existsByPichangaIdAndUsuarioIdAndPichangaEstado(Long pichangaId, Long usuarioId,
                                                            pe.pichangaapp.domain.EstadoPichanga estado);

    @EntityGraph(attributePaths = "usuario")
    List<PichangaParticipante> findByPichangaIdOrderByFechaUnionAsc(Long pichangaId);

    Optional<PichangaParticipante> findByPichangaIdAndUsuarioId(Long pichangaId, Long usuarioId);

    @Query("""
            select count(pp) from PichangaParticipante pp
            where pp.usuario.id = :usuarioId
              and pp.pichanga.id <> :pichangaId
              and pp.pichanga.fecha = :fecha
              and pp.pichanga.horaInicio = :horaInicio
              and pp.pichanga.estado in (pe.pichangaapp.domain.EstadoPichanga.ABIERTA,
                                         pe.pichangaapp.domain.EstadoPichanga.LLENA)
            """)
    long contarConflictosHorario(
            @Param("usuarioId") Long usuarioId,
            @Param("pichangaId") Long pichangaId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio);

    @EntityGraph(attributePaths = {"cancha", "organizador"})
    @Query("""
            select pp.pichanga from PichangaParticipante pp
            where pp.usuario.id = :usuarioId
            order by pp.pichanga.fecha desc, pp.pichanga.horaInicio desc
            """)
    List<Pichanga> listarPichangasDelUsuario(@Param("usuarioId") Long usuarioId);
}
