package pe.pichangaapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Entity
@Table(name = "horario")
@Getter
@Setter
@NoArgsConstructor
public class Horario {

    private static final Locale ESPANOL = Locale.forLanguageTag("es-PE");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;

    /** 1 = lunes ... 7 = domingo (ISO-8601). */
    @Column(name = "dia_semana", nullable = false)
    private int diaSemana;

    @Column(name = "hora_apertura", nullable = false)
    private LocalTime horaApertura;

    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;

    @Column(nullable = false)
    private boolean activo = true;

    @Transient
    public String getDiaNombre() {
        return java.time.DayOfWeek.of(diaSemana).getDisplayName(TextStyle.FULL, ESPANOL);
    }
}
