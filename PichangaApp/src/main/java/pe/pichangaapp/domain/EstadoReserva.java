package pe.pichangaapp.domain;

public enum EstadoReserva {
    PENDIENTE("Pendiente de pago", "warning"),
    CONFIRMADA("Confirmada", "success"),
    CANCELADA("Cancelada", "secondary"),
    EXPIRADA("Expirada", "dark"),
    COMPLETADA("Completada", "info");

    private final String etiqueta;
    private final String color;

    EstadoReserva(String etiqueta, String color) {
        this.etiqueta = etiqueta;
        this.color = color;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColor() {
        return color;
    }

    public boolean estaVigente() {
        return this == PENDIENTE || this == CONFIRMADA;
    }
}
