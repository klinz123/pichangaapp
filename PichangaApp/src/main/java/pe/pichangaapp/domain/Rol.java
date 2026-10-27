package pe.pichangaapp.domain;

public enum Rol {
    JUGADOR("Jugador"),
    PROPIETARIO("Propietario"),
    ADMIN("Administrador");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
