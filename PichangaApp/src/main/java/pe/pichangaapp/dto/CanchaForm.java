package pe.pichangaapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pe.pichangaapp.domain.TipoSuperficie;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
public class CanchaForm {

    @NotBlank(message = "Ingresa el nombre del complejo")
    @Size(max = 120)
    private String nombre;

    @Size(max = 1000)
    private String descripcion;

    @NotBlank(message = "Ingresa la dirección")
    @Size(max = 200)
    private String direccion;

    @NotBlank(message = "Selecciona un distrito")
    @Size(max = 80)
    private String distrito;

    @NotNull(message = "Selecciona la superficie")
    private TipoSuperficie superficie;

    @Min(value = 4, message = "La capacidad mínima es 4 jugadores")
    @Max(value = 30, message = "La capacidad máxima es 30 jugadores")
    private int capacidad = 10;

    @NotNull(message = "Ingresa el precio por hora")
    @DecimalMin(value = "1.00", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "5000.00", message = "El precio no puede superar S/ 5000")
    private BigDecimal precioHora;

    @NotNull(message = "Ingresa la hora de apertura")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaApertura;

    @NotNull(message = "Ingresa la hora de cierre")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaCierre;

    @DecimalMin(value = "-90.0", message = "Latitud inválida")
    @DecimalMax(value = "90.0", message = "Latitud inválida")
    private BigDecimal latitud;

    @DecimalMin(value = "-180.0", message = "Longitud inválida")
    @DecimalMax(value = "180.0", message = "Longitud inválida")
    private BigDecimal longitud;

    private boolean activa = true;

    public boolean isHorarioValido() {
        return horaApertura != null && horaCierre != null && horaApertura.isBefore(horaCierre);
    }
}
