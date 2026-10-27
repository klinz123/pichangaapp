package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.pichangaapp.domain.Cancha;

import java.util.List;
import java.util.Optional;

public interface CanchaDAO extends JpaRepository<Cancha, Long>, JpaSpecificationExecutor<Cancha> {

    @EntityGraph(attributePaths = "propietario")
    List<Cancha> findByActivaTrueOrderByNombreAsc();

    @EntityGraph(attributePaths = "propietario")
    List<Cancha> findByPropietarioIdOrderByNombreAsc(Long propietarioId);

    @EntityGraph(attributePaths = "propietario")
    List<Cancha> findAllByOrderByNombreAsc();

    List<Cancha> findByActivaTrueAndDistritoIgnoreCaseOrderByNombreAsc(String distrito);

    long countByActivaTrue();

    long countByPropietarioId(Long propietarioId);

    @Query("select distinct c.distrito from Cancha c where c.activa = true order by c.distrito")
    List<String> listarDistritos();

    @Query("select avg(cal.puntaje) from Calificacion cal where cal.cancha.id = :canchaId")
    Double promedioCalificacion(@Param("canchaId") Long canchaId);

    @Query("select count(cal) from Calificacion cal where cal.cancha.id = :canchaId")
    long contarCalificaciones(@Param("canchaId") Long canchaId);

    @EntityGraph(attributePaths = "propietario")
    @Override
    Optional<Cancha> findById(Long id);
}
