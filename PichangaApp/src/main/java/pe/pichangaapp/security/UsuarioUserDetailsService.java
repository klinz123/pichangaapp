package pe.pichangaapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.UsuarioDAO;
import pe.pichangaapp.domain.Usuario;

@Service
@RequiredArgsConstructor
public class UsuarioUserDetailsService implements UserDetailsService {

    private final UsuarioDAO usuarioDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioDAO.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
        if (!usuario.isActivo()) {
            throw new DisabledException("La cuenta está desactivada");
        }
        if (usuario.isBloqueado()) {
            throw new LockedException("La cuenta está bloqueada temporalmente");
        }
        return new UsuarioPrincipal(usuario);
    }
}
