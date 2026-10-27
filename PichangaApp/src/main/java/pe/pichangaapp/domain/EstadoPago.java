package pe.pichangaapp.domain;

public enum EstadoPago {
    PENDIENTE("Pendiente", "warning"),
    PAGADO("Pagado", "success"),
    RECHAZADO("Rechazado", "danger"),
    REEMBOLSADO("Reembolsado", "info"),
    REEMBOLSADO_PARCIAL("Reembolso parcial", "info");

    private final String etiqueta;
    private final String color;

    EstadoPago(String etiqueta, String color) {
        this.etiqueta = etiqueta;
        this.color = color;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColor() {
        return color;
    }
}
