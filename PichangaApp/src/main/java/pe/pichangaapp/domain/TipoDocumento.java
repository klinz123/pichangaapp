package pe.pichangaapp.domain;

public enum TipoDocumento {
    DNI("DNI"),
    CARNET_EXTRANJERIA("Carné de extranjería"),
    RUC("RUC"),
    LICENCIA_FUNCIONAMIENTO("Licencia de funcionamiento");

    private final String etiqueta;

    TipoDocumento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
