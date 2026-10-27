package pe.pichangaapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.dto.RegistroForm;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.UsuarioService;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.util.List;
import java.util.Locale;

/**
 * Vistas de autenticacion. El POST de /login lo procesa Spring Security
 * (ver SecurityConfig); aqui solo se sirve el formulario.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private static final List<Rol> ROLES_PUBLICOS = List.of(Rol.JUGADOR, Rol.PROPIETARIO);

    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UsuarioPrincipal principal) {
            return switch (principal.getRol()) {
                case ADMIN -> "redirect:/admin";
                case PROPIETARIO -> "redirect:/panel";
                case JUGADOR -> "redirect:/reservas/mis-reservas";
            };
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String formularioRegistro(Model model) {
        if (!model.containsAttribute("registroForm")) {
            model.addAttribute("registroForm", new RegistroForm());
        }
        model.addAttribute("roles", ROLES_PUBLICOS);
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registroForm") RegistroForm form,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        model.addAttribute("roles", ROLES_PUBLICOS);

        if (!form.isPasswordsCoinciden()) {
            result.rejectValue("confirmarPassword", "registro.password",
                    "Las contraseñas no coinciden.");
        }
        if (result.hasErrors()) {
            return "auth/registro";
        }

        try {
            usuarioService.registrar(form);
        } catch (ReglaNegocioException excepcion) {
            String mensaje = excepcion.getMessage();
            if (mensaje != null && mensaje.toLowerCase(Locale.ROOT).contains("correo")) {
                result.rejectValue("email", "registro.email", mensaje);
            } else {
                result.reject("registro.error", mensaje == null
                        ? "No pudimos crear tu cuenta. Revisa los datos e intenta nuevamente."
                        : mensaje);
            }
            return "auth/registro";
        }

        redirectAttributes.addFlashAttribute("mensajeExito",
                "Tu cuenta fue creada correctamente. Ya puedes iniciar sesión.");
        return "redirect:/login?registrado";
    }
}
