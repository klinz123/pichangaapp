package pe.pichangaapp.service.exception;

/** Recurso inexistente o no visible para el usuario autenticado. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
