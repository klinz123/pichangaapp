package pe.pichangaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.UsuarioDAO;
import pe.pichangaapp.domain.*;
import pe.pichangaapp.dto.CambioPasswordForm;
import pe.pichangaapp.dto.PerfilForm;
import pe.pichangaapp.dto.RegistroForm;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.UsuarioService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private static final Logger seguridad = LoggerFactory.getLogger("pe.pichangaapp.seguridad");

    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;
    private final NotificacionService notificacionService;

    @Value("${app.seguridad.intentos-maximos:5}")
    private int intentosMaximos;

    @Value("${app.seguridad.minutos-bloqueo:15}")
    private int minutosBloqueo;

    @Override
    @Transactional
    public Usuario registrar(RegistroForm form) {
        String email = normalizarEmail(form.getEmail());
        if (usuarioDAO.existsByEmailIgnoreCase(email)) {
            throw new ReglaNegocioException("Ya existe una cuenta registrada con ese correo electrónico.");
        }
        if (!form.isPasswordsCoinciden()) {
            throw new ReglaNegocioException("Las contraseñas no coinciden.");
        }
        if (form.getRol() == Rol.ADMIN) {
            throw new ReglaNegocioException("No es posible registrarse como administrador.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(form.getNombre().trim());
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        usuario.setRol(form.getRol());
        usuario.setTelefono(form.getTelefono());
        usuario.setFechaNacimiento(form.getFechaNacimiento());
        usuario.setActivo(true);
        usuario.setVerificado(false);
        usuario.setEstadoVerificacion(EstadoVerificacion.NO_SOLICITADA);
        usuario = usuarioDAO.save(usuario);

        notificacionService.enviar(usuario, TipoNotificacion.SISTEMA, "¡Bienvenido a PichangaApp!",
                "Tu cuenta fue creada correctamente. Explora las canchas y reserva tu primera pichanga.",
                "/canchas");
        seguridad.info("Registro exitoso email={} rol={} id={}", email, usuario.getRol(), usuario.getId());
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioDAO.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    @Override
    @Transactional
    public void actualizarPerfil(Long id, PerfilForm form) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombre(form.getNombre().trim());
        usuario.setTelefono(form.getTelefono());
        usuario.setFechaNacimiento(form.getFechaNacimiento());
        usuarioDAO.save(usuario);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long id, CambioPasswordForm form) {
        Usuario usuario = buscarPorId(id);
        if (!passwordEncoder.matches(form.getPasswordActual(), usuario.getPasswordHash())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta.");
        }
        if (!form.isPasswordsCoinciden()) {
            throw new ReglaNegocioException("La nueva contraseña y su confirmación no coinciden.");
        }
        if (passwordEncoder.matches(form.getPasswordNueva(), usuario.getPasswordHash())) {
            throw new ReglaNegocioException("La nueva contraseña debe ser distinta a la actual.");
        }
        usuario.setPasswordHash(passwordEncoder.encode(form.getPasswordNueva()));
        usuarioDAO.save(usuario);
        notificacionService.enviar(usuario, TipoNotificacion.SISTEMA, "Contraseña actualizada",
                "Tu contraseña fue cambiada correctamente. Si no fuiste tú, contacta a soporte.", "/perfil");
        seguridad.info("Cambio de contraseña usuario={}", usuario.getEmail());
    }

    @Override
    @Transactional
    public void solicitarVerificacion(Long id, TipoDocumento tipo, String numero) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getEstadoVerificacion() == EstadoVerificacion.PENDIENTE) {
            throw new ReglaNegocioException("Ya tienes una solicitud de verificación en revisión.");
        }
        if (tipo == null || numero == null || numero.trim().length() < 6) {
            throw new ReglaNegocioException("Ingresa un tipo y número de documento válido.");
        }
        usuario.setDocumentoTipo(tipo);
        usuario.setDocumentoNumero(numero.trim().toUpperCase(Locale.ROOT));
        usuario.setEstadoVerificacion(EstadoVerificacion.PENDIENTE);
        usuario.setVerificado(false);
        usuarioDAO.save(usuario);

        notificacionService.enviar(usuario, TipoNotificacion.SISTEMA, "Solicitud recibida",
                "Estamos revisando tu documentación. La verificación demora como máximo 48 horas.", "/perfil");
        usuarioDAO.findByRolOrderByNombreAsc(Rol.ADMIN).forEach(admin ->
                notificacionService.enviar(admin, TipoNotificacion.SISTEMA, "Nueva solicitud de verificación",
                        usuario.getNombre() + " solicita el sello de verificación.", "/admin/usuarios"));
        seguridad.info("Solicitud de verificacion usuario={} documento={}", usuario.getEmail(), tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioDAO.findAllByOrderByFechaRegistroDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> solicitudesPendientes() {
        return usuarioDAO.findByEstadoVerificacionOrderByFechaRegistroAsc(EstadoVerificacion.PENDIENTE);
    }

    @Override
    @Transactional
    public void resolverSolicitud(Long id, boolean aprobar) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getEstadoVerificacion() != EstadoVerificacion.PENDIENTE) {
            throw new ReglaNegocioException("La solicitud ya fue atendida.");
        }
        usuario.setEstadoVerificacion(aprobar ? EstadoVerificacion.APROBADA : EstadoVerificacion.RECHAZADA);
        usuario.setVerificado(aprobar);
        usuarioDAO.save(usuario);
        notificacionService.enviar(usuario, TipoNotificacion.SISTEMA,
                aprobar ? "Perfil verificado" : "Verificación rechazada",
                aprobar ? "Tu perfil ya muestra el sello de verificación."
                        : "La documentación no pudo validarse. Puedes volver a solicitar la verificación.",
                "/perfil");
        seguridad.info("Verificacion resuelta usuario={} aprobada={}", usuario.getEmail(), aprobar);
    }

    @Override
    @Transactional
    public void alternarActivo(Long id) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getRol() == Rol.ADMIN) {
            throw new ReglaNegocioException("No puedes desactivar una cuenta administradora.");
        }
        usuario.setActivo(!usuario.isActivo());
        usuarioDAO.save(usuario);
        seguridad.info("Cuenta {} usuario={}", usuario.isActivo() ? "activada" : "desactivada", usuario.getEmail());
    }

    @Override
    @Transactional
    public void registrarIntentoFallido(String email) {
        usuarioDAO.findByEmailIgnoreCase(normalizarEmail(email)).ifPresent(usuario -> {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= intentosMaximos) {
                usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(minutosBloqueo));
                usuario.setIntentosFallidos(0);
                seguridad.warn("Cuenta bloqueada por intentos fallidos email={}", email);
            }
            usuarioDAO.save(usuario);
        });
        seguridad.warn("Intento de autenticacion fallido email={}", email);
    }

    @Override
    @Transactional
    public void resetearIntentos(String email) {
        usuarioDAO.findByEmailIgnoreCase(normalizarEmail(email)).ifPresent(usuario -> {
            if (usuario.getIntentosFallidos() > 0 || usuario.getBloqueadoHasta() != null) {
                usuario.setIntentosFallidos(0);
                usuario.setBloqueadoHasta(null);
                usuarioDAO.save(usuario);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorRol(Rol rol) {
        return usuarioDAO.countByRol(rol);
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
