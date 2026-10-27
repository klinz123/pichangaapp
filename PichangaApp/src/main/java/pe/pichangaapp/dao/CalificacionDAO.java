package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.pichangaapp.domain.Calificacion;

import java.util.List;
import java.util.Optional;

public interface CalificacionDAO extends JpaRepository<Calificacion, Long> {

    @EntityGraph(attributePaths = "usuario")
    List<Calificacion> findByCanchaIdOrderByFechaCreacionDesc(Long canchaId);

    Optional<Calificacion> findByReservaId(Long reservaId);

    boolean existsByReservaId(Long reservaId);

    long countByCanchaId(Long canchaId);

    @Query("select avg(c.puntaje) from Calificacion c where c.cancha.id = :canchaId")
    Double promedioPorCancha(@Param("canchaId") Long canchaId);

    @Query("""
            select c from Calificacion c
            where c.cancha.propietario.id = :propietarioId
            order by c.fechaCreacion desc
            """)
    List<Calificacion> listarDelPropietario(@Param("propietarioId") Long propietarioId);

    @EntityGraph(attributePaths = {"usuario", "cancha"})
    Optional<Calificacion> findByIdAndCanchaPropietarioId(Long id, Long propietarioId);

    @Query("select avg(c.puntaje) from Calificacion c where c.cancha.propietario.id = :propietarioId")
    Double promedioDelPropietario(@Param("propietarioId") Long propietarioId);
}
