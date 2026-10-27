package pe.pichangaapp.domain;

public enum TipoSuperficie {
    CESPED_SINTETICO("Césped sintético"),
    CESPED_NATURAL("Césped natural"),
    LOSA("Losa deportiva"),
    OTRO("Otra superficie");

    private final String etiqueta;

    TipoSuperficie(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
