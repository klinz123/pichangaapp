package pe.pichangaapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BloqueoForm {

    @NotNull(message = "Selecciona una fecha")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "Selecciona la hora de inicio")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @Size(max = 200)
    private String motivo;
}
