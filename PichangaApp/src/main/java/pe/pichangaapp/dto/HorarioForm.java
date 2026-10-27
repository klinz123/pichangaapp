package pe.pichangaapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@Setter
public class HorarioForm {

    @Min(value = 1, message = "Día inválido")
    @Max(value = 7, message = "Día inválido")
    private int diaSemana;

    @NotNull(message = "Ingresa la hora de apertura")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaApertura;

    @NotNull(message = "Ingresa la hora de cierre")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaCierre;

    private boolean activo = true;

    public boolean isHorarioValido() {
        return horaApertura != null && horaCierre != null && horaApertura.isBefore(horaCierre);
    }
}
