package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.pichangaapp.domain.EstadoReserva;
import pe.pichangaapp.domain.Reserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaDAO extends JpaRepository<Reserva, Long> {

    @EntityGraph(attributePaths = {"cancha", "usuario"})
    List<Reserva> findByUsuarioIdOrderByFechaDescHoraInicioDesc(Long usuarioId);

    @EntityGraph(attributePaths = {"cancha", "usuario"})
    List<Reserva> findByCanchaPropietarioIdOrderByFechaDescHoraInicioDesc(Long propietarioId);

    @EntityGraph(attributePaths = {"cancha", "cancha.propietario", "usuario"})
    @Query("select r from Reserva r where r.id = :id")
    Optional<Reserva> findCompletaPorId(@Param("id") Long id);

    boolean existsByCanchaIdAndFechaAndHoraInicioAndEstadoIn(
            Long canchaId, LocalDate fecha, java.time.LocalTime horaInicio, List<EstadoReserva> estados);

    boolean existsByUsuarioIdAndFechaAndHoraInicioAndEstadoIn(
            Long usuarioId, LocalDate fecha, java.time.LocalTime horaInicio, List<EstadoReserva> estados);

    boolean existsByCodigo(String codigo);

    @EntityGraph(attributePaths = {"cancha", "cancha.propietario"})
    List<Reserva> findByUsuarioIdAndEstadoInOrderByFechaAscHoraInicioAsc(
            Long usuarioId, List<EstadoReserva> estados);

    long countByCanchaIdAndFechaAndEstadoIn(Long canchaId, LocalDate fecha, List<EstadoReserva> estados);

    List<Reserva> findByCanchaIdAndFechaAndEstadoInOrderByHoraInicioAsc(
            Long canchaId, LocalDate fecha, List<EstadoReserva> estados);

    List<Reserva> findByEstadoAndExpiraEnBefore(EstadoReserva estado, LocalDateTime momento);

    @EntityGraph(attributePaths = {"cancha", "usuario"})
    List<Reserva> findByEstadoAndFechaBetween(
            EstadoReserva estado, LocalDate desde, LocalDate hasta);

    @EntityGraph(attributePaths = {"cancha", "usuario"})
    @Query("""
            select r from Reserva r
            where r.cancha.propietario.id = :propietarioId
              and r.fecha between :desde and :hasta
              and r.estado in :estados
            order by r.fecha desc, r.horaInicio desc
            """)
    List<Reserva> listarDelPropietarioEnRango(
            @Param("propietarioId") Long propietarioId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("estados") List<EstadoReserva> estados);

    @Query("""
            select coalesce(sum(r.precioTotal), 0) from Reserva r
            where r.cancha.propietario.id = :propietarioId
              and r.fecha between :desde and :hasta
              and r.estado in (pe.pichangaapp.domain.EstadoReserva.CONFIRMADA,
                               pe.pichangaapp.domain.EstadoReserva.COMPLETADA)
            """)
    BigDecimal sumarIngresos(
            @Param("propietarioId") Long propietarioId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    @Query("""
            select count(r) from Reserva r
            where r.cancha.propietario.id = :propietarioId
              and r.fecha between :desde and :hasta
              and r.estado in (pe.pichangaapp.domain.EstadoReserva.CONFIRMADA,
                               pe.pichangaapp.domain.EstadoReserva.COMPLETADA)
            """)
    long contarDelPropietarioEnRango(
            @Param("propietarioId") Long propietarioId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    @Query("""
            select count(r) from Reserva r
            where r.cancha.propietario.id = :propietarioId and r.fecha = :fecha
              and r.estado in (pe.pichangaapp.domain.EstadoReserva.CONFIRMADA,
                               pe.pichangaapp.domain.EstadoReserva.COMPLETADA)
            """)
    long contarDelDia(@Param("propietarioId") Long propietarioId, @Param("fecha") LocalDate fecha);

    @EntityGraph(attributePaths = {"cancha", "usuario"})
    @Query("""
            select r from Reserva r
            where r.estado = pe.pichangaapp.domain.EstadoReserva.CONFIRMADA
              and r.fecha between :desde and :hasta
            """)
    List<Reserva> listarConfirmadasEnRango(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @EntityGraph(attributePaths = {"cancha", "cancha.propietario", "usuario"})
    @Query("""
            select r from Reserva r
            where r.fecha between :desde and :hasta
              and r.estado in :estados
            order by r.fecha asc, r.horaInicio asc
            """)
    List<Reserva> listarEnRango(@Param("desde") LocalDate desde,
                                @Param("hasta") LocalDate hasta,
                                @Param("estados") List<EstadoReserva> estados);

    @Query("select count(r) from Reserva r where r.estado = :estado")
    long contarPorEstado(@Param("estado") EstadoReserva estado);
}
