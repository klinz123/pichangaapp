package pe.pichangaapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pe.pichangaapp.domain.MetodoPago;

@Getter
@Setter
public class PagoForm {

    @NotNull(message = "Selecciona un método de pago")
    private MetodoPago metodo;

    private String referencia;
}
