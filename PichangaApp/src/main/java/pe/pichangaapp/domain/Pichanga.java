package pe.pichangaapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "pichanga")
@Getter
@Setter
@NoArgsConstructor
public class Pichanga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Usuario organizador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "cupos_total", nullable = false)
    private int cuposTotal;

    @Column(name = "cupos_ocupados", nullable = false)
    private int cuposOcupados;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelPichanga nivel;

    @Column(length = 800)
    private String descripcion;

    @Column(name = "precio_participante", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioParticipante = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPichanga estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    void antesDeGuardar() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPichanga.ABIERTA;
        }
        if (precioParticipante == null) {
            precioParticipante = BigDecimal.ZERO;
        }
    }

    public int getCuposDisponibles() {
        return Math.max(0, cuposTotal - cuposOcupados);
    }

    public int getPorcentajeOcupado() {
        if (cuposTotal <= 0) {
            return 0;
        }
        return (int) Math.round(cuposOcupados * 100.0 / cuposTotal);
    }

    public LocalDateTime getFechaHoraInicio() {
        return LocalDateTime.of(fecha, horaInicio);
    }

    public LocalDateTime getFechaHoraFin() {
        return LocalDateTime.of(fecha, horaFin);
    }

    public boolean isAbierta() {
        return estado == EstadoPichanga.ABIERTA;
    }
}
