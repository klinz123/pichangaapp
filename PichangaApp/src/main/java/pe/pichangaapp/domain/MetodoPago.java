package pe.pichangaapp.domain;

public enum MetodoPago {
    YAPE("Yape", "bi-phone", "Pago con QR desde tu aplicación Yape"),
    PLIN("Plin", "bi-qr-code", "Pago con QR desde tu aplicación Plin"),
    MERCADO_PAGO("Mercado Pago", "bi-credit-card", "Tarjeta o saldo de Mercado Pago"),
    PAYPAL("PayPal", "bi-paypal", "Pago internacional con PayPal");

    private final String etiqueta;
    private final String icono;
    private final String descripcion;

    MetodoPago(String etiqueta, String icono, String descripcion) {
        this.etiqueta = etiqueta;
        this.icono = icono;
        this.descripcion = descripcion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getIcono() {
        return icono;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
