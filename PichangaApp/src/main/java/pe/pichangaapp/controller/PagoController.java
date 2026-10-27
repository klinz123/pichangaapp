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
import pe.pichangaapp.domain.MetodoPago;
import pe.pichangaapp.dto.PagoForm;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.PagoService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.exception.ReglaNegocioException;

/**
 * Capa web de pagos.
 * - GET  /pagos/{reservaId}   -> pantalla de seleccion de metodo de pago (Yape, Plin, etc.).
 * - POST /pagos/procesar      -> registra el pago y confirma la reserva.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final ReservaService reservaService;

    @GetMapping("/{reservaId}")
    public String pago(@PathVariable Long reservaId,
                       @AuthenticationPrincipal UsuarioPrincipal principal,
                       Model model) {
        var reserva = reservaService.obtenerDetalle(reservaId, principal.getId(), false);
        model.addAttribute("reserva", reserva);
        model.addAttribute("pagoForm", new PagoForm());
        model.addAttribute("metodos", MetodoPago.values());
        return "pagos/pago";
    }

    @PostMapping("/procesar")
    public String procesar(@ModelAttribute("pagoForm") PagoForm form,
                           @RequestParam Long reservaId,
                           @AuthenticationPrincipal UsuarioPrincipal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            pagoService.registrarPago(reservaId, principal.getId(), form.getMetodo(), form.getReferencia());
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Pago confirmado. Tu reserva queda asegurada.");
            return "redirect:/reservas/mis-reservas";
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/pagos/" + reservaId;
        }
    }
}
