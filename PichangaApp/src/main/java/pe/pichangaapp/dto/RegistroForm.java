package pe.pichangaapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pe.pichangaapp.domain.Rol;

import java.time.LocalDate;

@Getter
@Setter
public class RegistroForm {

    @NotBlank(message = "Ingresa tu nombre completo")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    private String nombre;

    @NotBlank(message = "Ingresa tu correo")
    @Email(message = "Ingresa un correo válido")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Ingresa una contraseña")
    @Size(min = 8, max = 60, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "La contraseña debe combinar letras y números")
    private String password;

    @NotBlank(message = "Confirma tu contraseña")
    private String confirmarPassword;

    @Pattern(regexp = "^$|^[0-9+\\-\\s]{6,20}$", message = "Ingresa un teléfono válido")
    private String telefono;

    @NotNull(message = "Selecciona el tipo de cuenta")
    private Rol rol = Rol.JUGADOR;

    @NotNull(message = "Ingresa tu fecha de nacimiento")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @AssertTrue(message = "Debes aceptar los términos y la política de privacidad")
    private boolean aceptaTerminos;

    public boolean isPasswordsCoinciden() {
        return password != null && password.equals(confirmarPassword);
    }
}
