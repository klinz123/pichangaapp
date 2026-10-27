package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.pichangaapp.domain.EstadoPichanga;
import pe.pichangaapp.domain.Pichanga;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PichangaDAO extends JpaRepository<Pichanga, Long> {

    @EntityGraph(attributePaths = {"cancha", "organizador"})
    List<Pichanga> findByEstadoInAndFechaGreaterThanEqualOrderByFechaAscHoraInicioAsc(
            List<EstadoPichanga> estados, LocalDate fecha);

    @EntityGraph(attributePaths = {"cancha", "organizador"})
    @Query("select p from Pichanga p where p.id = :id")
    Optional<Pichanga> findCompletaPorId(@Param("id") Long id);

    @EntityGraph(attributePaths = {"cancha"})
    List<Pichanga> findByOrganizadorIdOrderByFechaDescHoraInicioDesc(Long organizadorId);

    @EntityGraph(attributePaths = {"cancha", "organizador"})
    List<Pichanga> findAllByOrderByFechaDescHoraInicioDesc();

    boolean existsByReservaId(Long reservaId);

    boolean existsByReservaIdAndEstadoIn(Long reservaId, List<EstadoPichanga> estados);

    @Query("""
            select p from Pichanga p
            where p.estado in :estados
              and (p.fecha < :hoy or (p.fecha = :hoy and p.horaFin <= :hora))
            """)
    List<Pichanga> findVencidas(@Param("estados") List<EstadoPichanga> estados,
                                @Param("hoy") LocalDate hoy,
                                @Param("hora") LocalTime hora);

    long countByEstado(EstadoPichanga estado);

    long countByCanchaPropietarioIdAndEstado(Long propietarioId, EstadoPichanga estado);
}
