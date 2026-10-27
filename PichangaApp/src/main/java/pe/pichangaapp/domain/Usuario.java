package pe.pichangaapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(length = 20)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private boolean verificado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_verificacion", nullable = false, length = 20)
    private EstadoVerificacion estadoVerificacion = EstadoVerificacion.NO_SOLICITADA;

    @Enumerated(EnumType.STRING)
    @Column(name = "documento_tipo", length = 30)
    private TipoDocumento documentoTipo;

    @Column(name = "documento_numero", length = 30)
    private String documentoNumero;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    void antesDeGuardar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estadoVerificacion == null) {
            estadoVerificacion = EstadoVerificacion.NO_SOLICITADA;
        }
        if (email != null) {
            email = email.trim().toLowerCase(Locale.ROOT);
        }
    }

    @Transient
    public String getIniciales() {
        if (StringUtils.isBlank(nombre)) {
            return "?";
        }
        String[] partes = nombre.trim().split("\\s+");
        String iniciales = partes[0].substring(0, 1);
        if (partes.length > 1) {
            iniciales += partes[1].substring(0, 1);
        }
        return iniciales.toUpperCase(Locale.ROOT);
    }

    @Transient
    public String getPrimerNombre() {
        return StringUtils.defaultIfBlank(nombre, "").split("\\s+")[0];
    }

    @Transient
    public boolean isBloqueado() {
        return bloqueadoHasta != null && bloqueadoHasta.isAfter(LocalDateTime.now());
    }

    @Transient
    public boolean isVerificacionPendiente() {
        return estadoVerificacion == EstadoVerificacion.PENDIENTE;
    }

    @Transient
    public boolean esPropietario() {
        return rol == Rol.PROPIETARIO;
    }

    @Transient
    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }
}
