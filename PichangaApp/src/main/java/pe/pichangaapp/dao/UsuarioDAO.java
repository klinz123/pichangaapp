package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.pichangaapp.domain.EstadoVerificacion;
import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.domain.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioDAO extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByRolOrderByNombreAsc(Rol rol);

    List<Usuario> findByEstadoVerificacionOrderByFechaRegistroAsc(EstadoVerificacion estado);

    List<Usuario> findAllByOrderByFechaRegistroDesc();

    long countByRol(Rol rol);
}
