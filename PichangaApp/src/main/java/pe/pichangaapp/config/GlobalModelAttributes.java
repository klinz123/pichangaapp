package pe.pichangaapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.UsuarioService;

import java.time.LocalDate;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    @ModelAttribute
    public void agregarDatosComunes(Model model, Authentication authentication) {
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("appNombre", "PichangaApp");
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UsuarioPrincipal principal) {
            Usuario usuario = usuarioService.buscarPorId(principal.getId());
            model.addAttribute("usuarioActual", usuario);
            model.addAttribute("notificacionesNoLeidas", notificacionService.contarNoLeidas(usuario.getId()));
            model.addAttribute("notificacionesRecientes", notificacionService.ultimas(usuario.getId()));
        } else {
            model.addAttribute("notificacionesNoLeidas", 0L);
        }
    }
}
