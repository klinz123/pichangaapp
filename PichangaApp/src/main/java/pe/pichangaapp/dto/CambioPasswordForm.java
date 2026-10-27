package pe.pichangaapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioPasswordForm {

    @NotBlank(message = "Ingresa tu contraseña actual")
    private String passwordActual;

    @NotBlank(message = "Ingresa la nueva contraseña")
    @Size(min = 8, max = 60, message = "La nueva contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "La contraseña debe combinar letras y números")
    private String passwordNueva;

    @NotBlank(message = "Confirma la nueva contraseña")
    private String confirmarPassword;

    public boolean isPasswordsCoinciden() {
        return passwordNueva != null && passwordNueva.equals(confirmarPassword);
    }
}
