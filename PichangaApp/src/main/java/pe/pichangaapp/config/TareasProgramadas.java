package pe.pichangaapp.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.pichangaapp.dao.NotificacionDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.domain.EstadoReserva;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.domain.TipoNotificacion;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.PichangaService;
import pe.pichangaapp.service.ReservaService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tareas de mantenimiento del dominio: expiracion de reservas sin pago,
 * cierre de reservas jugadas y recordatorios de partidos (RF09).
 */
@Component
@RequiredArgsConstructor
public class TareasProgramadas {

    private static final Logger log = LoggerFactory.getLogger(TareasProgramadas.class);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final ReservaService reservaService;
    private final PichangaService pichangaService;
    private final ReservaDAO reservaDAO;
    private final NotificacionService notificacionService;
    private final NotificacionDAO notificacionDAO;

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void expirarReservasPendientes() {
        int expiradas = reservaService.expirarPendientes();
        if (expiradas > 0) {
            log.info("Tarea programada: {} reservas expiradas", expiradas);
        }
    }

    @Scheduled(cron = "0 5 * * * *")
    public void completarReservasJugadas() {
        int completadas = reservaService.completarFinalizadas();
        if (completadas > 0) {
            log.info("Tarea programada: {} reservas marcadas como completadas", completadas);
        }
    }

    @Scheduled(fixedDelay = 300_000, initialDelay = 45_000)
    public void finalizarPichangasVencidas() {
        int finalizadas = pichangaService.finalizarVencidas();
        if (finalizadas > 0) {
            log.info("Tarea programada: {} pichangas finalizadas", finalizadas);
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void enviarRecordatorios() {
        LocalDate manana = LocalDate.now().plusDays(1);
        List<Reserva> reservas = reservaDAO.listarConfirmadasEnRango(manana, manana);
        for (Reserva reserva : reservas) {
            String enlace = "/reservas/" + reserva.getId();
            boolean yaEnviado = notificacionDAO.existsByUsuarioIdAndTipoAndEnlace(
                    reserva.getUsuario().getId(), TipoNotificacion.RECORDATORIO, enlace);
            if (!yaEnviado) {
                notificacionService.enviar(reserva.getUsuario(), TipoNotificacion.RECORDATORIO,
                        "Recordatorio de tu pichanga",
                        "Mañana " + reserva.getFecha() + " a las " + reserva.getHoraInicio().format(HORA)
                                + " juegas en " + reserva.getCancha().getNombre() + ".",
                        enlace);
            }
        }
        if (!reservas.isEmpty()) {
            log.info("Tarea programada: recordatorios procesados para {} reservas", reservas.size());
        }
    }
}
