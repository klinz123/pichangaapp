package pe.pichangaapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pe.pichangaapp.dao.CalificacionDAO;
import pe.pichangaapp.dao.CanchaDAO;
import pe.pichangaapp.dao.PagoDAO;
import pe.pichangaapp.dao.PichangaDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.dao.UsuarioDAO;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.domain.EstadoReserva;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.domain.TipoNotificacion;
import pe.pichangaapp.domain.TipoSuperficie;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.service.DisponibilidadService;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaDAO reservaDAO;
    @Mock
    private CanchaDAO canchaDAO;
    @Mock
    private UsuarioDAO usuarioDAO;
    @Mock
    private PagoDAO pagoDAO;
    @Mock
    private CalificacionDAO calificacionDAO;
    @Mock
    private PichangaDAO pichangaDAO;
    @Mock
    private DisponibilidadService disponibilidadService;
    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private ReservaServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "horasMinimasCancelacion", 2);
    }

    @Test
    void cancelarPorJugadorBloqueaSiLaReservaTienePichangaActiva() {
        Usuario jugador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario propietario = usuario(2L, "Juan", Rol.PROPIETARIO);
        Cancha cancha = cancha(1L, propietario);
        Reserva reserva = reserva(5L, jugador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0));

        when(reservaDAO.findCompletaPorId(5L)).thenReturn(Optional.of(reserva));
        when(pichangaDAO.existsByReservaIdAndEstadoIn(eq(5L), anyList())).thenReturn(true);

        assertThatThrownBy(() -> service.cancelarPorJugador(5L, 4L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("pichanga activa");

        verify(reservaDAO, never()).save(any(Reserva.class));
        verifyNoInteractions(pagoDAO, notificacionService);
    }

    @Test
    void cancelarPorPropietarioBloqueaSiLaReservaTienePichangaActiva() {
        Usuario jugador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario propietario = usuario(2L, "Juan", Rol.PROPIETARIO);
        Cancha cancha = cancha(1L, propietario);
        Reserva reserva = reserva(5L, jugador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0));

        when(reservaDAO.findCompletaPorId(5L)).thenReturn(Optional.of(reserva));
        when(pichangaDAO.existsByReservaIdAndEstadoIn(eq(5L), anyList())).thenReturn(true);

        assertThatThrownBy(() -> service.cancelarPorPropietario(5L, 2L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("pichanga activa");

        verify(reservaDAO, never()).save(any(Reserva.class));
        verifyNoInteractions(pagoDAO, notificacionService);
    }

    @Test
    void cancelarPorJugadorCancelaCuandoNoHayPichangaActiva() {
        Usuario jugador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario propietario = usuario(2L, "Juan", Rol.PROPIETARIO);
        Cancha cancha = cancha(1L, propietario);
        Reserva reserva = reserva(5L, jugador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0));

        when(reservaDAO.findCompletaPorId(5L)).thenReturn(Optional.of(reserva));
        when(pichangaDAO.existsByReservaIdAndEstadoIn(eq(5L), anyList())).thenReturn(false);
        when(pagoDAO.findByReservaId(5L)).thenReturn(Optional.empty());

        service.cancelarPorJugador(5L, 4L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        verify(reservaDAO).save(reserva);
        verify(notificacionService, times(2)).enviar(any(Usuario.class), eq(TipoNotificacion.RESERVA),
                anyString(), anyString(), anyString());
    }

    @Test
    void reservasParaPichangaFiltraPasadasYLasQueYaTienenPichanga() {
        Usuario jugador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario propietario = usuario(2L, "Juan", Rol.PROPIETARIO);
        Cancha cancha = cancha(1L, propietario);
        Reserva futura = reserva(15L, jugador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0));
        Reserva pasada = reserva(16L, jugador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().minusDays(1), LocalTime.of(20, 0));
        Reserva yaPublicada = reserva(17L, jugador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().plusDays(2), LocalTime.of(20, 0));

        when(reservaDAO.findByUsuarioIdAndEstadoInOrderByFechaAscHoraInicioAsc(
                4L, List.of(EstadoReserva.CONFIRMADA)))
                .thenReturn(List.of(futura, pasada, yaPublicada));
        when(pichangaDAO.existsByReservaId(15L)).thenReturn(false);
        when(pichangaDAO.existsByReservaId(17L)).thenReturn(true);

        List<Reserva> resultado = service.reservasParaPichanga(4L);

        assertThat(resultado).containsExactly(futura);
    }

    private Usuario usuario(Long id, String nombre, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(nombre.toLowerCase().replace(" ", ".") + "@test.pe");
        usuario.setPasswordHash("hash");
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setFechaNacimiento(LocalDate.now().minusYears(30));
        return usuario;
    }

    private Cancha cancha(Long id, Usuario propietario) {
        Cancha cancha = new Cancha();
        cancha.setId(id);
        cancha.setPropietario(propietario);
        cancha.setNombre("Cancha " + id);
        cancha.setDireccion("Av. Siempre Viva 123");
        cancha.setDistrito("Surco");
        cancha.setSuperficie(TipoSuperficie.CESPED_SINTETICO);
        cancha.setCapacidad(10);
        cancha.setPrecioHora(BigDecimal.valueOf(60));
        cancha.setHoraApertura(LocalTime.of(7, 0));
        cancha.setHoraCierre(LocalTime.of(23, 0));
        cancha.setActiva(true);
        return cancha;
    }

    private Reserva reserva(Long id, Usuario usuario, Cancha cancha, EstadoReserva estado,
                            LocalDate fecha, LocalTime horaInicio) {
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setCodigo("PIC-" + id);
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaInicio.plusHours(1));
        reserva.setEstado(estado);
        reserva.setPrecioTotal(BigDecimal.valueOf(60));
        return reserva;
    }
}
