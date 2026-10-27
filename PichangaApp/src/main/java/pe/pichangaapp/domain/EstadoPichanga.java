package pe.pichangaapp.domain;

public enum EstadoPichanga {
    ABIERTA("Abierta", "success"),
    LLENA("Llena", "warning"),
    CANCELADA("Cancelada", "secondary"),
    FINALIZADA("Finalizada", "dark");

    private final String etiqueta;
    private final String color;

    EstadoPichanga(String etiqueta, String color) {
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
