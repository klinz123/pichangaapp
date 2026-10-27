package pe.pichangaapp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pe.pichangaapp.domain.Rol;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String destino = "/reservas/mis-reservas";
        if (authentication.getPrincipal() instanceof UsuarioPrincipal principal) {
            if (principal.getRol() == Rol.ADMIN) {
                destino = "/admin";
            } else if (principal.getRol() == Rol.PROPIETARIO) {
                destino = "/panel";
            }
        }
        response.sendRedirect(request.getContextPath() + destino);
    }
}
