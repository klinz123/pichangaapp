package pe.pichangaapp.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import pe.pichangaapp.domain.TipoSuperficie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BusquedaForm {

    private String texto;
    private String distrito;
    private TipoSuperficie superficie;

    private BigDecimal precioMax;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime hora;

    public boolean tieneFiltros() {
        return (texto != null && !texto.isBlank())
                || (distrito != null && !distrito.isBlank())
                || superficie != null
                || precioMax != null
                || fecha != null
                || hora != null;
    }
}
