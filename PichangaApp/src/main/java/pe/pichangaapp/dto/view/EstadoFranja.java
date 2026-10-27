package pe.pichangaapp.dto.view;

public enum EstadoFranja {
    LIBRE("Disponible", "success"),
    RESERVADA("Reservada", "danger"),
    BLOQUEADA("Bloqueada", "secondary"),
    PASADA("No disponible", "light");

    private final String etiqueta;
    private final String color;

    EstadoFranja(String etiqueta, String color) {
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
