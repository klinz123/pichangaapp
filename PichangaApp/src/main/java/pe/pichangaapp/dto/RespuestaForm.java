package pe.pichangaapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespuestaForm {

    @NotBlank(message = "Escribe una respuesta")
    @Size(max = 800, message = "La respuesta no puede superar 800 caracteres")
    private String respuesta;
}
