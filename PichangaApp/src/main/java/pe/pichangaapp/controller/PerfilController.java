package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.UsuarioService;
import pe.pichangaapp.service.exception.ReglaNegocioException;
import pe.pichangaapp.dto.CambioPasswordForm;
import pe.pichangaapp.dto.PerfilForm;

/**
 * Capa web de perfil de usuario.
 * - GET  /perfil                       → ver perfil.
 * - POST  /perfil                      → editar perfil.
 * - POST /perfil/cambiar-password      → cambiar contraseña.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String ver(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        var usuario = usuarioService.buscarPorId(principal.getId());
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfilForm", new PerfilForm());
        return "perfil/ver";
    }

    @PostMapping
    public String editar(@ModelAttribute("perfilForm") PerfilForm form,
                         @AuthenticationPrincipal UsuarioPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            usuarioService.actualizarPerfil(principal.getId(), form);
            redirectAttributes.addFlashAttribute("mensajeExito", "Perfil actualizado correctamente.");
            return "redirect:/perfil";
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/perfil";
        }
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@ModelAttribute("cambioPasswordForm") CambioPasswordForm form,
                                  @AuthenticationPrincipal UsuarioPrincipal principal,
                                  RedirectAttributes redirectAttributes) {
        if (!form.isPasswordsCoinciden()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Las contraseñas no coinciden.");
            return "redirect:/perfil";
        }
        try {
            usuarioService.cambiarPassword(principal.getId(), form);
            redirectAttributes.addFlashAttribute("mensajeExito", "Contraseña cambiada correctamente.");
            return "redirect:/perfil";
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/perfil";
        }
    }
}
