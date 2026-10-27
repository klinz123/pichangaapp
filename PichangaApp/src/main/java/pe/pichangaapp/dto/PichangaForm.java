package pe.pichangaapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import pe.pichangaapp.domain.NivelPichanga;

import java.math.BigDecimal;

@Getter
@Setter
public class PichangaForm {

    @NotNull(message = "Selecciona la reserva que respalda la pichanga")
    private Long reservaId;

    @Min(value = 2, message = "La pichanga necesita al menos 2 cupos")
    @Max(value = 30, message = "El máximo es 30 cupos")
    private int cuposTotal = 10;

    @NotNull(message = "Selecciona el nivel de juego")
    private NivelPichanga nivel = NivelPichanga.MIXTO;

    @Size(max = 800, message = "La descripción no puede superar 800 caracteres")
    private String descripcion;

    @NotNull(message = "Indica la cuota por participante")
    @DecimalMin(value = "0.00", message = "La cuota no puede ser negativa")
    @DecimalMax(value = "500.00", message = "La cuota no puede superar S/ 500")
    private BigDecimal precioParticipante = BigDecimal.ZERO;
}
