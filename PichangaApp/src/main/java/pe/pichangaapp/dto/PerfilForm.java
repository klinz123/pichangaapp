package pe.pichangaapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class PerfilForm {

    @NotBlank(message = "Ingresa tu nombre completo")
    @Size(min = 3, max = 120)
    private String nombre;

    @Pattern(regexp = "^$|^[0-9+\\-\\s]{6,20}$", message = "Ingresa un teléfono válido")
    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;
}
