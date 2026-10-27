package pe.pichangaapp.domain;

public enum TipoNotificacion {
    RESERVA("bi-calendar-check", "primary"),
    PAGO("bi-credit-card", "success"),
    PICHANGA("bi-people", "info"),
    RECORDATORIO("bi-alarm", "warning"),
    CALIFICACION("bi-star", "warning"),
    SISTEMA("bi-info-circle", "secondary");

    private final String icono;
    private final String color;

    TipoNotificacion(String icono, String color) {
        this.icono = icono;
        this.color = color;
    }

    public String getIcono() {
        return icono;
    }

    public String getColor() {
        return color;
    }
}
