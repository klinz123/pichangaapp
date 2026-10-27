package pe.pichangaapp.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.*;
import pe.pichangaapp.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Datos de demostracion para el entorno local. Solo se ejecuta si la base
 * esta vacia, por lo que es seguro reiniciar la aplicacion.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UsuarioDAO usuarioDAO;
    private final CanchaDAO canchaDAO;
    private final HorarioDAO horarioDAO;
    private final ReservaDAO reservaDAO;
    private final PagoDAO pagoDAO;
    private final CalificacionDAO calificacionDAO;
    private final PichangaDAO pichangaDAO;
    private final PichangaParticipanteDAO participanteDAO;
    private final NotificacionDAO notificacionDAO;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo.seed:true}")
    private boolean seedActivo;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedActivo) {
            log.info("Seed de demostracion desactivado.");
            return;
        }
        if (usuarioDAO.count() > 0) {
            log.info("La base ya contiene datos; no se ejecuta el seed.");
            return;
        }
        log.info("Cargando datos de demostracion de PichangaApp...");

        Usuario admin = usuario("Administrador PichangaApp", "admin@pichangaapp.pe", "Admin123!", Rol.ADMIN,
                "999111222", LocalDate.of(1990, 1, 15), true);
        Usuario juan = usuario("Juan Pérez", "juan.perez@pichangaapp.pe", "Dueno123!", Rol.PROPIETARIO,
                "987654321", LocalDate.of(1988, 4, 12), true);
        Usuario maria = usuario("María Rodríguez", "maria.rodriguez@pichangaapp.pe", "Dueno123!",
                Rol.PROPIETARIO, "987111222", LocalDate.of(1992, 8, 20), false);
        maria.setEstadoVerificacion(EstadoVerificacion.PENDIENTE);
        maria.setDocumentoTipo(TipoDocumento.RUC);
        maria.setDocumentoNumero("20512345678");
        usuarioDAO.save(maria);

        Usuario carlos = usuario("Carlos Mendoza", "carlos@pichangaapp.pe", "Jugador123!", Rol.JUGADOR,
                "912345678", LocalDate.of(1999, 3, 8), false);
        Usuario luis = usuario("Luis Ramírez", "luis@pichangaapp.pe", "Jugador123!", Rol.JUGADOR,
                "923456789", LocalDate.of(2001, 7, 22), false);
        Usuario andrea = usuario("Andrea Paredes", "andrea@pichangaapp.pe", "Jugador123!", Rol.JUGADOR,
                "934567890", LocalDate.of(1997, 11, 30), false);
        Usuario diego = usuario("Diego Salazar", "diego@pichangaapp.pe", "Jugador123!", Rol.JUGADOR,
                "945678901", LocalDate.of(2003, 2, 14), false);

        Cancha laCancha = cancha(juan, "Complejo Deportivo La Cancha", "San Borja",
                "Av. Aviación 2450", TipoSuperficie.CESPED_SINTETICO, 10, "80",
                LocalTime.of(7, 0), LocalTime.of(23, 0),
                "Cancha de césped sintético de última generación con iluminación LED, vestuarios y estacionamiento.");
        Cancha mirador = cancha(juan, "Campo Mirador FC", "Miraflores",
                "Jr. Las Flores 150", TipoSuperficie.CESPED_SINTETICO, 10, "60",
                LocalTime.of(8, 0), LocalTime.of(23, 0),
                "Fútbol 7 en el corazón de Miraflores. Incluye chalecos, balón y agua para los jugadores.");
        Cancha bombonera = cancha(maria, "La Bombonera Surco", "Santiago de Surco",
                "Av. Caminos del Inca 320", TipoSuperficie.CESPED_SINTETICO, 14, "95",
                LocalTime.of(7, 0), LocalTime.of(23, 30),
                "Fútbol 9 con grass sintético premium y tribuna para 60 personas.");
        Cancha sanMiguel = cancha(maria, "Estadio San Miguel", "San Miguel",
                "Av. La Marina 1800", TipoSuperficie.LOSA, 12, "70",
                LocalTime.of(8, 0), LocalTime.of(22, 0),
                "Losa deportiva techada, ideal para fútbol 8 y básquet.");
        Cancha golazo = cancha(juan, "Cancha El Golazo", "La Molina",
                "Av. Raúl Ferrero 410", TipoSuperficie.CESPED_SINTETICO, 10, "110",
                LocalTime.of(7, 0), LocalTime.of(23, 0),
                "Complejo con dos canchas, cafetería y zona de calentamiento.");
        Cancha jesusMaria = cancha(maria, "Jesús María Sports", "Jesús María",
                "Av. Salaverry 1200", TipoSuperficie.CESPED_NATURAL, 14, "120",
                LocalTime.of(8, 0), LocalTime.of(22, 0),
                "Cancha de césped natural mantenida profesionalmente para fútbol 9.");

        List<Cancha> canchas = List.of(laCancha, mirador, bombonera, sanMiguel, golazo, jesusMaria);
        for (Cancha cancha : canchas) {
            for (int dia = 1; dia <= 7; dia++) {
                Horario horario = new Horario();
                horario.setCancha(cancha);
                horario.setDiaSemana(dia);
                horario.setHoraApertura(cancha.getHoraApertura());
                horario.setHoraCierre(dia == 7 && cancha.getHoraCierre().isAfter(LocalTime.of(20, 0))
                        ? LocalTime.of(20, 0) : cancha.getHoraCierre());
                horario.setActivo(true);
                horarioDAO.save(horario);
            }
        }

        LocalDate hoy = LocalDate.now();

        Reserva r1 = reserva(carlos, laCancha, hoy.minusDays(4), LocalTime.of(18, 0),
                EstadoReserva.COMPLETADA, "80", "PIC-DEMO-0001");
        pago(r1, MetodoPago.YAPE, "80", "YP-DEMO-0001");
        calificacion(carlos, laCancha, r1, 5, "Excelente cancha y muy buena atención. El césped está impecable.");

        Reserva r2 = reserva(luis, laCancha, hoy.minusDays(6), LocalTime.of(20, 0),
                EstadoReserva.COMPLETADA, "80", "PIC-DEMO-0002");
        pago(r2, MetodoPago.PLIN, "80", "PL-DEMO-0002");
        calificacion(luis, laCancha, r2, 4, "Buena cancha, aunque faltó un poco de iluminación en la esquina.");

        Reserva r3 = reserva(andrea, mirador, hoy.minusDays(8), LocalTime.of(19, 0),
                EstadoReserva.COMPLETADA, "60", "PIC-DEMO-0003");
        pago(r3, MetodoPago.MERCADO_PAGO, "60", "MP-DEMO-0003");
        calificacion(andrea, mirador, r3, 5, "Súper recomendado. Nos prestaron chalecos y balón.");

        Reserva r4 = reserva(diego, bombonera, hoy.minusDays(3), LocalTime.of(21, 0),
                EstadoReserva.COMPLETADA, "95", "PIC-DEMO-0004");
        pago(r4, MetodoPago.YAPE, "95", "YP-DEMO-0004");
        calificacion(diego, bombonera, r4, 4, "Cancha grande y bien mantenida. Volveremos.");

        Reserva r5 = reserva(carlos, laCancha, hoy.plusDays(2), LocalTime.of(20, 0),
                EstadoReserva.CONFIRMADA, "80", "PIC-DEMO-0005");
        pago(r5, MetodoPago.YAPE, "80", "YP-DEMO-0005");

        Reserva r6 = reserva(luis, mirador, hoy.plusDays(3), LocalTime.of(19, 0),
                EstadoReserva.CONFIRMADA, "60", "PIC-DEMO-0006");
        pago(r6, MetodoPago.PLIN, "60", "PL-DEMO-0006");

        Reserva r7 = reserva(andrea, bombonera, hoy.plusDays(1), LocalTime.of(21, 0),
                EstadoReserva.PENDIENTE, "95", "PIC-DEMO-0007");
        r7.setExpiraEn(LocalDateTime.now().plusMinutes(15));
        reservaDAO.save(r7);

        Reserva r8 = reserva(diego, sanMiguel, hoy.plusDays(4), LocalTime.of(18, 0),
                EstadoReserva.CONFIRMADA, "70", "PIC-DEMO-0008");
        pago(r8, MetodoPago.MERCADO_PAGO, "70", "MP-DEMO-0008");

        Pichanga pichanga = new Pichanga();
        pichanga.setOrganizador(carlos);
        pichanga.setCancha(laCancha);
        pichanga.setReserva(r5);
        pichanga.setFecha(r5.getFecha());
        pichanga.setHoraInicio(r5.getHoraInicio());
        pichanga.setHoraFin(r5.getHoraFin());
        pichanga.setCuposTotal(10);
        pichanga.setCuposOcupados(3);
        pichanga.setNivel(NivelPichanga.INTERMEDIO);
        pichanga.setDescripcion("Pichanga de los jueves. Fútbol 7, buen nivel y luego una gaseosa. "
                + "Lleva tu camiseta azul.");
        pichanga.setPrecioParticipante(new BigDecimal("10"));
        pichanga.setEstado(EstadoPichanga.ABIERTA);
        pichanga = pichangaDAO.save(pichanga);
        participanteDAO.save(new PichangaParticipante(pichanga, carlos));
        participanteDAO.save(new PichangaParticipante(pichanga, luis));
        participanteDAO.save(new PichangaParticipante(pichanga, andrea));

        notificacion(carlos, TipoNotificacion.PAGO, "Pago confirmado",
                "Tu reserva PIC-DEMO-0005 quedó confirmada para el " + r5.getFecha() + " a las 20:00.",
                "/reservas/" + r5.getId());
        notificacion(carlos, TipoNotificacion.PICHANGA, "Tu pichanga está publicada",
                "Ya tienes 3 jugadores confirmados. Comparte el enlace para completar los 10 cupos.",
                "/pichangas/" + pichanga.getId());
        notificacion(luis, TipoNotificacion.RECORDATORIO, "Recordatorio de tu pichanga",
                "Tienes una pichanga el " + pichanga.getFecha() + " a las " + pichanga.getHoraInicio()
                        + " en " + laCancha.getNombre() + ".",
                "/pichangas/" + pichanga.getId());
        notificacion(admin, TipoNotificacion.SISTEMA, "Solicitud de verificación pendiente",
                "María Rodríguez solicita el sello de verificación de su complejo.",
                "/admin/usuarios");

        log.info("Seed completado: {} usuarios, {} canchas, {} reservas, {} pichangas.",
                usuarioDAO.count(), canchaDAO.count(), reservaDAO.count(), pichangaDAO.count());
        log.info("Credenciales demo -> admin@pichangaapp.pe / Admin123! | juan.perez@pichangaapp.pe / Dueno123! | carlos@pichangaapp.pe / Jugador123!");
    }

    private Usuario usuario(String nombre, String email, String password, Rol rol, String telefono,
                            LocalDate nacimiento, boolean verificado) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setTelefono(telefono);
        usuario.setFechaNacimiento(nacimiento);
        usuario.setVerificado(verificado);
        usuario.setEstadoVerificacion(verificado ? EstadoVerificacion.APROBADA : EstadoVerificacion.NO_SOLICITADA);
        usuario.setActivo(true);
        return usuarioDAO.save(usuario);
    }

    private Cancha cancha(Usuario propietario, String nombre, String distrito, String direccion,
                          TipoSuperficie superficie, int capacidad, String precio,
                          LocalTime apertura, LocalTime cierre, String descripcion) {
        Cancha cancha = new Cancha();
        cancha.setPropietario(propietario);
        cancha.setNombre(nombre);
        cancha.setDistrito(distrito);
        cancha.setDireccion(direccion);
        cancha.setSuperficie(superficie);
        cancha.setCapacidad(capacidad);
        cancha.setPrecioHora(new BigDecimal(precio));
        cancha.setHoraApertura(apertura);
        cancha.setHoraCierre(cierre);
        cancha.setDescripcion(descripcion);
        cancha.setActiva(true);
        return canchaDAO.save(cancha);
    }

    private Reserva reserva(Usuario usuario, Cancha cancha, LocalDate fecha, LocalTime hora,
                            EstadoReserva estado, String precio, String codigo) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(hora);
        reserva.setHoraFin(hora.plusHours(1));
        reserva.setEstado(estado);
        reserva.setPrecioTotal(new BigDecimal(precio));
        reserva.setCodigo(codigo);
        return reservaDAO.save(reserva);
    }

    private void pago(Reserva reserva, MetodoPago metodo, String monto, String transaccion) {
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMetodo(metodo);
        pago.setMonto(new BigDecimal(monto));
        pago.setMontoReembolsado(BigDecimal.ZERO);
        pago.setEstado(EstadoPago.PAGADO);
        pago.setTransaccionId(transaccion);
        pago.setFechaPago(reserva.getFecha().atTime(reserva.getHoraInicio()).minusDays(1));
        pagoDAO.save(pago);
    }

    private void calificacion(Usuario usuario, Cancha cancha, Reserva reserva, int puntaje, String comentario) {
        Calificacion calificacion = new Calificacion();
        calificacion.setUsuario(usuario);
        calificacion.setCancha(cancha);
        calificacion.setReserva(reserva);
        calificacion.setPuntaje(puntaje);
        calificacion.setComentario(comentario);
        calificacionDAO.save(calificacion);
    }

    private void notificacion(Usuario usuario, TipoNotificacion tipo, String titulo,
                              String contenido, String enlace) {
        notificacionDAO.save(new Notificacion(usuario, tipo, titulo, contenido, enlace));
    }
}
