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
import pe.pichangaapp.service.PagoService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Capa web de reservas.
 * - GET  /reservas/nueva/{canchaId}  -> formulario de confirmacion de reserva.
 * - POST /reservas                   -> crea la reserva (estado PENDIENTE) y deriva al pago.
 * - GET  /reservas/mis-reservas      -> listado de reservas del usuario autenticado.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final CanchaService canchaService;
    private final PagoService pagoService;

    @GetMapping("/nueva/{canchaId}")
    public String formulario(@PathVariable Long canchaId,
                             @RequestParam LocalDate fecha,
                             @RequestParam LocalTime hora,
                             Model model) {
        var cancha = canchaService.obtener(canchaId);
        model.addAttribute("cancha", cancha);
        model.addAttribute("fecha", fecha);
        model.addAttribute("hora", hora);
        model.addAttribute("precioTotal", cancha.getPrecioHora());
        return "reservas/formulario";
    }

    @PostMapping
    public String crear(@RequestParam Long canchaId,
                        @RequestParam LocalDate fecha,
                        @RequestParam LocalTime horaInicio,
                        @AuthenticationPrincipal UsuarioPrincipal principal,
                        RedirectAttributes redirectAttributes) {
        try {
            var reserva = reservaService.crear(principal.getId(), canchaId, fecha, horaInicio);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Reserva " + reserva.getCodigo() + " creada. Tienes 15 minutos para completar el pago.");
            return "redirect:/pagos/" + reserva.getId();
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/canchas/" + canchaId + "?fecha=" + fecha;
        }
    }

    @GetMapping("/mis-reservas")
    public String misReservas(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        var reservas = reservaService.delUsuario(principal.getId());
        model.addAttribute("reservas", reservas);
        model.addAttribute("pagoService", pagoService);
        model.addAttribute("calificables", reservaService.reservasCalificables(principal.getId())
                .stream().map(r -> r.getId()).toList());
        return "reservas/mis-reservas";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id,
                          @AuthenticationPrincipal UsuarioPrincipal principal,
                          Model model) {
        var reserva = reservaService.obtenerDetalle(id, principal.getId(), false);
        model.addAttribute("reserva", reserva);
        model.addAttribute("pagoService", pagoService);
        model.addAttribute("puedeCalificar", reservaService.puedeCalificar(id, principal.getId()));
        return "reservas/detalle";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           @AuthenticationPrincipal UsuarioPrincipal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            reservaService.cancelarPorJugador(id, principal.getId());
            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva cancelada correctamente.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/reservas/mis-reservas";
    }
}
