package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.CanchaService;
import pe.pichangaapp.service.DisponibilidadService;
import pe.pichangaapp.service.exception.ReglaNegocioException;
import pe.pichangaapp.dto.BloqueoForm;

/**
 * Bloqueo y desbloqueo de horarios del propietario.
 * - POST /panel/canchas/{id}/bloquear    → bloquear un horario.
 * - POST /panel/canchas/{id}/desbloquear → desbloquear un horario.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/panel/canchas")
public class BloqueoController {

    private final CanchaService canchaService;
    private final DisponibilidadService disponibilidadService;

    @PostMapping("/{id}/bloquear")
    public String bloquear(@PathVariable Long id,
                           @ModelAttribute("bloqueoForm") BloqueoForm form,
                           @AuthenticationPrincipal UsuarioPrincipal principal,
                           RedirectAttributes redirectAttributes) {
        if (!canchaService.perteneceA(id, principal.getId())) {
            return "redirect:/panel/canchas";
        }
        try {
            disponibilidadService.bloquear(id, principal.getId(), form);
            redirectAttributes.addFlashAttribute("mensajeExito", "Horario bloqueado correctamente.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/panel/canchas/" + id + "/horarios";
    }

    @PostMapping("/{id}/desbloquear")
    public String desbloquear(@PathVariable Long id,
                              @RequestParam java.time.LocalDate fecha,
                              @RequestParam java.time.LocalTime horaInicio,
                              @AuthenticationPrincipal UsuarioPrincipal principal,
                              RedirectAttributes redirectAttributes) {
        if (!canchaService.perteneceA(id, principal.getId())) {
            return "redirect:/panel/canchas";
        }
        try {
            disponibilidadService.desbloquear(id, principal.getId(), fecha, horaInicio);
            redirectAttributes.addFlashAttribute("mensajeExito", "Horario desbloqueado.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/panel/canchas/" + id + "/horarios?fecha=" + fecha;
    }
}
