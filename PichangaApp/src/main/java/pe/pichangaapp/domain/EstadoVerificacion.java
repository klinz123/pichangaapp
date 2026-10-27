package pe.pichangaapp.domain;

public enum EstadoVerificacion {
    NO_SOLICITADA("Sin solicitar", "secondary"),
    PENDIENTE("En revisión", "warning"),
    APROBADA("Verificado", "success"),
    RECHAZADA("Rechazado", "danger");

    private final String etiqueta;
    private final String color;

    EstadoVerificacion(String etiqueta, String color) {
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
