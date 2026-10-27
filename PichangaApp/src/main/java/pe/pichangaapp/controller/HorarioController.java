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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.CanchaService;
import pe.pichangaapp.service.DisponibilidadService;
import pe.pichangaapp.service.exception.ReglaNegocioException;
import pe.pichangaapp.dto.HorarioForm;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Gestión de horarios del propietario.
 * - GET  /panel/canchas/{id}/horarios        → ver horarios de una cancha.
 * - POST /panel/canchas/{id}/horarios        → guardar/actualizar horario.
 * - POST /panel/canchas/{id}/horarios/{dia}/eliminar → eliminar horario.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/panel/canchas")
public class HorarioController {

    private final CanchaService canchaService;
    private final DisponibilidadService disponibilidadService;

    @GetMapping("/{id}/horarios")
    public String horarios(@PathVariable Long id,
                           @AuthenticationPrincipal UsuarioPrincipal principal,
                           Model model) {
        if (!canchaService.perteneceA(id, principal.getId())) {
            return "redirect:/panel/canchas";
        }
        var cancha = canchaService.obtener(id);
        var horarios = disponibilidadService.horarios(id);

        // Días de la semana para el formulario
        List<String> dias = List.of(
                "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

        model.addAttribute("cancha", cancha);
        model.addAttribute("horarios", horarios);
        model.addAttribute("dias", dias);
        model.addAttribute("horarioForm", new HorarioForm());
        return "panel/horarios";
    }

    @PostMapping("/{id}/horarios")
    public String guardarHorario(@PathVariable Long id,
                                 @ModelAttribute("horarioForm") HorarioForm form,
                                 @AuthenticationPrincipal UsuarioPrincipal principal,
                                 RedirectAttributes redirectAttributes) {
        if (!canchaService.perteneceA(id, principal.getId())) {
            return "redirect:/panel/canchas";
        }
        try {
            disponibilidadService.guardarHorario(id, principal.getId(), form);
            redirectAttributes.addFlashAttribute("mensajeExito", "Horario guardado correctamente.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/panel/canchas/" + id + "/horarios";
    }

    @PostMapping("/{id}/horarios/{diaSemana}/eliminar")
    public String eliminarHorario(@PathVariable Long id,
                                  @PathVariable int diaSemana,
                                  @AuthenticationPrincipal UsuarioPrincipal principal,
                                  RedirectAttributes redirectAttributes) {
        if (!canchaService.perteneceA(id, principal.getId())) {
            return "redirect:/panel/canchas";
        }
        try {
            disponibilidadService.eliminarHorario(id, principal.getId(), diaSemana);
            redirectAttributes.addFlashAttribute("mensajeExito", "Horario eliminado.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/panel/canchas/" + id + "/horarios";
    }
}
