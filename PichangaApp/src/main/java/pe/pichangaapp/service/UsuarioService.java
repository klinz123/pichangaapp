package pe.pichangaapp.service;

import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.domain.TipoDocumento;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.dto.CambioPasswordForm;
import pe.pichangaapp.dto.PerfilForm;
import pe.pichangaapp.dto.RegistroForm;

import java.util.List;

public interface UsuarioService {

    Usuario registrar(RegistroForm form);

    Usuario buscarPorId(Long id);

    Usuario buscarPorEmail(String email);

    void actualizarPerfil(Long id, PerfilForm form);

    void cambiarPassword(Long id, CambioPasswordForm form);

    void solicitarVerificacion(Long id, TipoDocumento tipo, String numero);

    List<Usuario> listarTodos();

    List<Usuario> solicitudesPendientes();

    void resolverSolicitud(Long id, boolean aprobar);

    void alternarActivo(Long id);

    void registrarIntentoFallido(String email);

    void resetearIntentos(String email);

    long contarPorRol(Rol rol);
}
