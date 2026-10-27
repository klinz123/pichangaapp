package pe.pichangaapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.pichangaapp.domain.Pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PagoDAO extends JpaRepository<Pago, Long> {

    Optional<Pago> findByReservaId(Long reservaId);

    Optional<Pago> findByTransaccionId(String transaccionId);

    boolean existsByTransaccionId(String transaccionId);

    List<Pago> findByReservaCanchaPropietarioId(Long propietarioId);

    @Query("""
            select coalesce(sum(p.monto - p.montoReembolsado), 0) from Pago p
            where p.reserva.cancha.propietario.id = :propietarioId
              and p.estado in (pe.pichangaapp.domain.EstadoPago.PAGADO,
                               pe.pichangaapp.domain.EstadoPago.REEMBOLSADO_PARCIAL)
              and p.reserva.fecha between :desde and :hasta
            """)
    BigDecimal sumarPagosNetos(
            @Param("propietarioId") Long propietarioId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    @Query("""
            select coalesce(sum(p.monto - p.montoReembolsado), 0) from Pago p
            where p.estado in (pe.pichangaapp.domain.EstadoPago.PAGADO,
                               pe.pichangaapp.domain.EstadoPago.REEMBOLSADO_PARCIAL)
            """)
    BigDecimal sumarPagosNetosGlobal();

    @Query("""
            select coalesce(sum(p.monto - p.montoReembolsado), 0) from Pago p
            where p.estado in (pe.pichangaapp.domain.EstadoPago.PAGADO,
                               pe.pichangaapp.domain.EstadoPago.REEMBOLSADO_PARCIAL)
              and p.reserva.fecha between :desde and :hasta
            """)
    BigDecimal sumarPagosNetosGlobalEnRango(@Param("desde") LocalDate desde,
                                            @Param("hasta") LocalDate hasta);
}
