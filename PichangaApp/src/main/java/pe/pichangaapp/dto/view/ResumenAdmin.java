package pe.pichangaapp.dto.view;

import java.math.BigDecimal;

public record ResumenAdmin(
        long usuarios,
        long jugadores,
        long propietarios,
        long canchas,
        long reservas,
        long pichangas,
        long solicitudesPendientes,
        BigDecimal ingresosGlobales
) {
}
