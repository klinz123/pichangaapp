package pe.pichangaapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pichanga_participante")
@Getter
@Setter
@NoArgsConstructor
public class PichangaParticipante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pichanga_id", nullable = false)
    private Pichanga pichanga;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_union", nullable = false)
    private LocalDateTime fechaUnion;

    public PichangaParticipante(Pichanga pichanga, Usuario usuario) {
        this.pichanga = pichanga;
        this.usuario = usuario;
        this.fechaUnion = LocalDateTime.now();
    }
}
