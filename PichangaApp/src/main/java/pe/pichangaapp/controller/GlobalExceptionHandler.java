package pe.pichangaapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

/**
 * Manejo global de excepciones para devolver respuestas HTTP apropiadas
 * en lugar de errores 500 genéricos.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String manejarRecursoNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        model.addAttribute("codigo", 404);
        model.addAttribute("titulo", "No encontrado");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public String manejarReglaNegocio(ReglaNegocioException ex, Model model) {
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        model.addAttribute("codigo", 400);
        model.addAttribute("titulo", "Solicitud inválida");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String manejarAccesoDenegado(AccessDeniedException ex, Model model) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        model.addAttribute("codigo", 403);
        model.addAttribute("titulo", "Acceso denegado");
        model.addAttribute("mensaje", "No tiene permisos para acceder a este recurso.");
        return "error/403";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String manejarIllegalArgument(IllegalArgumentException ex, Model model) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        model.addAttribute("codigo", 400);
        model.addAttribute("titulo", "Solicitud inválida");
        model.addAttribute("mensaje", ex.getMessage());
        return "error/400";
    }
}
