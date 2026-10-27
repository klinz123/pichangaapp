package pe.pichangaapp.service.exception;

/** Se lanza cuando una operacion incumple una regla de negocio (R01-R10). */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
