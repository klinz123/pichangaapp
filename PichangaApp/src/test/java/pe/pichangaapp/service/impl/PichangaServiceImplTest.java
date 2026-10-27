package pe.pichangaapp.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.pichangaapp.dao.PichangaDAO;
import pe.pichangaapp.dao.PichangaParticipanteDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.dao.UsuarioDAO;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.domain.EstadoPichanga;
import pe.pichangaapp.domain.EstadoReserva;
import pe.pichangaapp.domain.NivelPichanga;
import pe.pichangaapp.domain.Pichanga;
import pe.pichangaapp.domain.PichangaParticipante;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.domain.TipoNotificacion;
import pe.pichangaapp.domain.TipoSuperficie;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.dto.PichangaForm;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.ReservaService;
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
class PichangaServiceImplTest {

    @Mock
    private PichangaDAO pichangaDAO;
    @Mock
    private PichangaParticipanteDAO participanteDAO;
    @Mock
    private ReservaDAO reservaDAO;
    @Mock
    private UsuarioDAO usuarioDAO;
    @Mock
    private ReservaService reservaService;
    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private PichangaServiceImpl service;

    @Test
    void crearPublicaLaPichangaYRegistraAlOrganizador() {
        Usuario organizador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario propietario = usuario(2L, "Juan", Rol.PROPIETARIO);
        Cancha cancha = cancha(1L, propietario, 10);
        Reserva reserva = reserva(5L, organizador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0));
        PichangaForm form = form(5L, 10);

        when(usuarioDAO.findById(4L)).thenReturn(Optional.of(organizador));
        when(reservaService.obtenerDetalle(5L, 4L, false)).thenReturn(reserva);
        when(pichangaDAO.existsByReservaId(5L)).thenReturn(false);
        when(pichangaDAO.save(any(Pichanga.class))).thenAnswer(invocation -> {
            Pichanga pichanga = invocation.getArgument(0);
            pichanga.setId(1L);
            return pichanga;
        });

        Pichanga resultado = service.crear(4L, form);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo(EstadoPichanga.ABIERTA);
        assertThat(resultado.getCuposOcupados()).isEqualTo(1);
        assertThat(resultado.getCuposTotal()).isEqualTo(10);
        assertThat(resultado.getOrganizador()).isSameAs(organizador);
        assertThat(resultado.getCancha()).isSameAs(cancha);
        assertThat(resultado.getReserva()).isSameAs(reserva);
        assertThat(resultado.getNivel()).isEqualTo(NivelPichanga.MIXTO);
        verify(participanteDAO).save(any(PichangaParticipante.class));
    }

    @Test
    void crearRechazaMenorDeEdad() {
        Usuario organizador = usuario(4L, "Carlos", Rol.JUGADOR);
        organizador.setFechaNacimiento(LocalDate.now().minusYears(17));

        when(usuarioDAO.findById(4L)).thenReturn(Optional.of(organizador));

        assertThatThrownBy(() -> service.crear(4L, form(5L, 10)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("mayor de 18");

        verifyNoInteractions(reservaService);
    }

    @Test
    void crearRechazaReservaNoConfirmada() {
        Usuario organizador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario propietario = usuario(2L, "Juan", Rol.PROPIETARIO);
        Cancha cancha = cancha(1L, propietario, 10);
        Reserva reserva = reserva(5L, organizador, cancha, EstadoReserva.COMPLETADA,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0));

        when(usuarioDAO.findById(4L)).thenReturn(Optional.of(organizador));
        when(reservaService.obtenerDetalle(5L, 4L, false)).thenReturn(reserva);

        assertThatThrownBy(() -> service.crear(4L, form(5L, 10)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("reserva confirmada");
    }

    @Test
    void crearRechazaReservaQueYaTienePichanga() {
        Usuario organizador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario propietario = usuario(2L, "Juan", Rol.PROPIETARIO);
        Cancha cancha = cancha(1L, propietario, 10);
        Reserva reserva = reserva(5L, organizador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0));

        when(usuarioDAO.findById(4L)).thenReturn(Optional.of(organizador));
        when(reservaService.obtenerDetalle(5L, 4L, false)).thenReturn(reserva);
        when(pichangaDAO.existsByReservaId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(4L, form(5L, 10)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya tiene una pichanga");
    }

    @Test
    void crearRechazaCuposMayoresQueLaCapacidad() {
        Usuario organizador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario propietario = usuario(2L, "Juan", Rol.PROPIETARIO);
        Cancha cancha = cancha(1L, propietario, 6);
        Reserva reserva = reserva(5L, organizador, cancha, EstadoReserva.CONFIRMADA,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0));

        when(usuarioDAO.findById(4L)).thenReturn(Optional.of(organizador));
        when(reservaService.obtenerDetalle(5L, 4L, false)).thenReturn(reserva);
        when(pichangaDAO.existsByReservaId(5L)).thenReturn(false);

        assertThatThrownBy(() -> service.crear(4L, form(5L, 10)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("capacidad");
    }

    @Test
    void unirseMarcaLaPichangaComoLlena() {
        Usuario organizador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario jugador = usuario(5L, "Luis", Rol.JUGADOR);
        Cancha cancha = cancha(1L, organizador, 10);
        Pichanga pichanga = pichanga(1L, organizador, cancha, EstadoPichanga.ABIERTA,
                2, 1, LocalDate.now().plusDays(1), LocalTime.of(20, 0));

        when(pichangaDAO.findCompletaPorId(1L)).thenReturn(Optional.of(pichanga));
        when(usuarioDAO.findById(5L)).thenReturn(Optional.of(jugador));
        when(participanteDAO.existsByPichangaIdAndUsuarioId(1L, 5L)).thenReturn(false);
        when(participanteDAO.contarConflictosHorario(5L, 1L, pichanga.getFecha(),
                pichanga.getHoraInicio())).thenReturn(0L);
        when(reservaDAO.existsByUsuarioIdAndFechaAndHoraInicioAndEstadoIn(eq(5L),
                eq(pichanga.getFecha()), eq(pichanga.getHoraInicio()), anyList())).thenReturn(false);
        when(participanteDAO.saveAndFlush(any(PichangaParticipante.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(pichangaDAO.saveAndFlush(any(Pichanga.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(participanteDAO.findByPichangaIdOrderByFechaUnionAsc(1L)).thenReturn(List.of());

        service.unirse(1L, 5L);

        assertThat(pichanga.getEstado()).isEqualTo(EstadoPichanga.LLENA);
        assertThat(pichanga.getCuposOcupados()).isEqualTo(2);
        verify(notificacionService, times(2)).enviar(any(Usuario.class),
                eq(TipoNotificacion.PICHANGA), anyString(), anyString(), anyString());
    }

    @Test
    void unirseRechazaUnaPichangaSinCupos() {
        Usuario organizador = usuario(4L, "Carlos", Rol.JUGADOR);
        Usuario jugador = usuario(5L, "Luis", Rol.JUGADOR);
        Cancha cancha = cancha(1L, organizador, 10);
        Pichanga pichanga = pichanga(1L, organizador, cancha, EstadoPichanga.ABIERTA,
                2, 2, LocalDate.now().plusDays(1), LocalTime.of(20, 0));

        when(pichangaDAO.findCompletaPorId(1L)).thenReturn(Optional.of(pichanga));
        when(usuarioDAO.findById(5L)).thenReturn(Optional.of(jugador));

        assertThatThrownBy(() -> service.unirse(1L, 5L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("se llenó");

        assertThat(pichanga.getEstado()).isEqualTo(EstadoPichanga.LLENA);
        verify(pichangaDAO).save(pichanga);
        verify(participanteDAO, never()).saveAndFlush(any(PichangaParticipante.class));
    }

    @Test
    void finalizarVencidasMarcaTodasLasPichangasVencidas() {
        Usuario organizador = usuario(4L, "Carlos", Rol.JUGADOR);
        Cancha cancha = cancha(1L, organizador, 10);
        Pichanga primera = pichanga(1L, organizador, cancha, EstadoPichanga.ABIERTA,
                10, 3, LocalDate.now().minusDays(1), LocalTime.of(20, 0));
        Pichanga segunda = pichanga(2L, organizador, cancha, EstadoPichanga.LLENA,
                10, 10, LocalDate.now().minusDays(2), LocalTime.of(21, 0));

        when(pichangaDAO.findVencidas(anyList(), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(List.of(primera, segunda));

        int total = service.finalizarVencidas();

        assertThat(total).isEqualTo(2);
        assertThat(primera.getEstado()).isEqualTo(EstadoPichanga.FINALIZADA);
        assertThat(segunda.getEstado()).isEqualTo(EstadoPichanga.FINALIZADA);
        verify(pichangaDAO, times(2)).save(any(Pichanga.class));
    }

    private PichangaForm form(Long reservaId, int cuposTotal) {
        PichangaForm form = new PichangaForm();
        form.setReservaId(reservaId);
        form.setCuposTotal(cuposTotal);
        form.setNivel(NivelPichanga.MIXTO);
        form.setDescripcion("Partido de prueba");
        form.setPrecioParticipante(BigDecimal.ZERO);
        return form;
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

    private Cancha cancha(Long id, Usuario propietario, int capacidad) {
        Cancha cancha = new Cancha();
        cancha.setId(id);
        cancha.setPropietario(propietario);
        cancha.setNombre("Cancha " + id);
        cancha.setDireccion("Av. Siempre Viva 123");
        cancha.setDistrito("Surco");
        cancha.setSuperficie(TipoSuperficie.CESPED_SINTETICO);
        cancha.setCapacidad(capacidad);
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

    private Pichanga pichanga(Long id, Usuario organizador, Cancha cancha, EstadoPichanga estado,
                              int cuposTotal, int cuposOcupados, LocalDate fecha, LocalTime horaInicio) {
        Pichanga pichanga = new Pichanga();
        pichanga.setId(id);
        pichanga.setOrganizador(organizador);
        pichanga.setCancha(cancha);
        pichanga.setFecha(fecha);
        pichanga.setHoraInicio(horaInicio);
        pichanga.setHoraFin(horaInicio.plusHours(1));
        pichanga.setCuposTotal(cuposTotal);
        pichanga.setCuposOcupados(cuposOcupados);
        pichanga.setNivel(NivelPichanga.MIXTO);
        pichanga.setPrecioParticipante(BigDecimal.ZERO);
        pichanga.setEstado(estado);
        return pichanga;
    }
}
