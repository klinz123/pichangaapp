package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.pichangaapp.domain.BloqueoHorario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BloqueoHorarioDAO extends JpaRepository<BloqueoHorario, Long> {

    List<BloqueoHorario> findByCanchaIdAndFechaOrderByHoraInicioAsc(Long canchaId, LocalDate fecha);

    Optional<BloqueoHorario> findByCanchaIdAndFechaAndHoraInicio(Long canchaId, LocalDate fecha, LocalTime horaInicio);

    boolean existsByCanchaIdAndFechaAndHoraInicio(Long canchaId, LocalDate fecha, LocalTime horaInicio);

    List<BloqueoHorario> findByCanchaIdAndFechaBetween(Long canchaId, LocalDate desde, LocalDate hasta);
}
