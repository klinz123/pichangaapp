package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.pichangaapp.domain.Horario;

import java.util.List;
import java.util.Optional;

public interface HorarioDAO extends JpaRepository<Horario, Long> {

    List<Horario> findByCanchaIdOrderByDiaSemanaAsc(Long canchaId);

    Optional<Horario> findByCanchaIdAndDiaSemana(Long canchaId, int diaSemana);

    boolean existsByCanchaIdAndDiaSemana(Long canchaId, int diaSemana);
}
