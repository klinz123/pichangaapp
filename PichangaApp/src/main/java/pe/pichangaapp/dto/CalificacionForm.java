package pe.pichangaapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalificacionForm {

    @NotNull(message = "Reserva no identificada")
    private Long reservaId;

    @Min(value = 1, message = "La calificación mínima es 1 estrella")
    @Max(value = 5, message = "La calificación máxima es 5 estrellas")
    private int puntaje = 5;

    @Size(max = 800, message = "El comentario no puede superar 800 caracteres")
    private String comentario;
}
