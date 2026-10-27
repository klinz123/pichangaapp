package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.NotificacionService;

import java.util.List;

/**
 * Capa web de notificaciones del usuario.
 * - GET  /notificaciones   → listado de notificaciones del usuario autenticado.
 * - POST /notificaciones/{id}/leer → marcar como leída.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public String listado(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        var notificaciones = notificacionService.listar(principal.getId());
        model.addAttribute("notificaciones", notificaciones);
        return "notificaciones/listado";
    }

    @PostMapping("/{id}/leer")
    public String marcarLeida(@PathVariable Long id,
                              @AuthenticationPrincipal UsuarioPrincipal principal,
                              RedirectAttributes redirectAttributes) {
        notificacionService.marcarLeida(id, principal.getId());
        redirectAttributes.addFlashAttribute("mensajeExito", "Notificación marcada como leída.");
        return "redirect:/notificaciones";
    }
}
