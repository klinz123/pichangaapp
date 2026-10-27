package pe.pichangaapp.domain;

public enum NivelPichanga {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado"),
    MIXTO("Mixto");

    private final String etiqueta;

    NivelPichanga(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
