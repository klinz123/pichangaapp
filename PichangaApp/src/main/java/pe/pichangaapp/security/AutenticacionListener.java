package pe.pichangaapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import pe.pichangaapp.service.UsuarioService;

@Component
@RequiredArgsConstructor
public class AutenticacionListener {

    private final UsuarioService usuarioService;

    @EventListener
    public void alFallar(AbstractAuthenticationFailureEvent evento) {
        if (evento.getException() instanceof BadCredentialsException) {
            Object principal = evento.getAuthentication().getPrincipal();
            if (principal instanceof String email) {
                usuarioService.registrarIntentoFallido(email);
            }
        }
    }

    @EventListener
    public void alAutenticar(AuthenticationSuccessEvent evento) {
        if (evento.getAuthentication().getPrincipal() instanceof UsuarioPrincipal principal) {
            usuarioService.resetearIntentos(principal.getUsername());
        }
    }
}
