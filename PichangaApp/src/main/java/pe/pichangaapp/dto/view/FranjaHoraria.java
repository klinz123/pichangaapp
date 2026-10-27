package pe.pichangaapp.dto.view;

import lombok.Getter;
import pe.pichangaapp.dto.view.EstadoFranja;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class FranjaHoraria {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalTime inicio;
    private final LocalTime fin;
    private final EstadoFranja estado;
    private final BigDecimal precio;
    private final String detalle;

    public FranjaHoraria(LocalTime inicio, LocalTime fin, EstadoFranja estado, BigDecimal precio, String detalle) {
        this.inicio = inicio;
        this.fin = fin;
        this.estado = estado;
        this.precio = precio;
        this.detalle = detalle;
    }

    public boolean isLibre() {
        return estado == EstadoFranja.LIBRE;
    }

    public boolean isReservada() {
        return estado == EstadoFranja.RESERVADA;
    }

    public String getRango() {
        return inicio.format(HORA) + " – " + fin.format(HORA);
    }
}
