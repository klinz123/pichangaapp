package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.CalificacionService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.exception.ReglaNegocioException;
import pe.pichangaapp.dto.CalificacionForm;

import java.util.List;

/**
 * Capa web de calificaciones de canchas.
 * - GET  /calificaciones/{reservaId}   → formulario para calificar una reserva completada.
 * - POST /calificaciones               → registrar calificación.
 * - POST /calificaciones/{id}/responder → responder una calificación (propietario).
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;
    private final ReservaService reservaService;

    @GetMapping("/{reservaId}")
    public String formulario(@PathVariable Long reservaId,
                             @AuthenticationPrincipal UsuarioPrincipal principal,
                             Model model) {
        var reserva = reservaService.obtenerDetalle(reservaId, principal.getId(), false);
        model.addAttribute("reserva", reserva);
        model.addAttribute("calificacionForm", new CalificacionForm());
        model.addAttribute("reservaId", reservaId);
        return "calificaciones/formulario";
    }

    @PostMapping
    public String registrar(@ModelAttribute("calificacionForm") CalificacionForm form,
                            @AuthenticationPrincipal UsuarioPrincipal principal,
                            RedirectAttributes redirectAttributes) {
        try {
            calificacionService.registrar(principal.getId(), form);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Gracias por calificar la cancha.");
            return "redirect:/reservas/mis-reservas";
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/calificaciones/" + form.getReservaId();
        }
    }

    @PostMapping("/{id}/responder")
    public String responder(@PathVariable Long id,
                            @AuthenticationPrincipal UsuarioPrincipal principal,
                            @RequestParam String respuesta,
                            RedirectAttributes redirectAttributes) {
        try {
            calificacionService.responder(id, principal.getId(), respuesta);
            redirectAttributes.addFlashAttribute("mensajeExito", "Respuesta publicada.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/panel/canchas";
    }
}
