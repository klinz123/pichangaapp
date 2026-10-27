package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.CanchaService;
import pe.pichangaapp.service.ReservaService;

import java.time.LocalDate;
import java.util.List;

/**
 * Reservas del propietario.
 * - GET /panel/reservas → listado de reservas de las canchas del propietario.
 * - POST /panel/reservas/{id}/cancelar → cancelar reserva por el propietario.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/panel/reservas")
public class ReservaPropietarioController {

    private final ReservaService reservaService;
    private final CanchaService canchaService;

    @GetMapping
    public String reservas(@AuthenticationPrincipal UsuarioPrincipal principal,
                           @RequestParam(required = false) LocalDate desde,
                           @RequestParam(required = false) LocalDate hasta,
                           Model model) {
        Long propietarioId = principal.getId();
        LocalDate desdeEfectiva = (desde != null) ? desde : LocalDate.now().minusDays(30);
        LocalDate hastaEfectiva = (hasta != null) ? hasta : LocalDate.now().plusDays(30);

        var reservas = reservaService.delPropietario(propietarioId, desdeEfectiva, hastaEfectiva);
        model.addAttribute("reservas", reservas);
        model.addAttribute("desde", desdeEfectiva);
        model.addAttribute("hasta", hastaEfectiva);
        return "panel/reservas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           @AuthenticationPrincipal UsuarioPrincipal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            reservaService.cancelarPorPropietario(id, principal.getId());
            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva cancelada.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/panel/reservas";
    }
}
