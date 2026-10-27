package pe.pichangaapp.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import pe.pichangaapp.dao.CalificacionDAO;
import pe.pichangaapp.dao.ReservaDAO;
import pe.pichangaapp.domain.Calificacion;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.domain.EstadoReserva;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.dto.CalificacionForm;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.exception.RecursoNoEncontradoException;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceImplTest {

    @Mock
    private CalificacionDAO calificacionDAO;
    @Mock
    private ReservaDAO reservaDAO;
    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private CalificacionServiceImpl service;

    @Test
    void registrarExitoGuardaCalificacionYNotifica() {
        Usuario usuario = usuario(1L, "Juan");
        Usuario propietario = usuario(2L, "Ana");
        Cancha cancha = cancha(10L, propietario);
        Reserva reserva = reservaCompleta(1L, usuario, cancha, EstadoReserva.COMPLETADA,
                LocalDateTime.now().minusHours(2));
        CalificacionForm form = form(1L, 4, "Buena cancha");

        when(reservaDAO.findCompletaPorId(1L)).thenReturn(Optional.of(reserva));
        when(calificacionDAO.existsByReservaId(1L)).thenReturn(false);
        when(calificacionDAO.saveAndFlush(any(Calificacion.class)))
                .thenAnswer(invocation -> {
                    Calificacion c = invocation.getArgument(0);
                    c.setId(100L);
                    return c;
                });

        Calificacion resultado = service.registrar(1L, form);

        assertThat(resultado.getId()).isEqualTo(100L);
        assertThat(resultado.getPuntaje()).isEqualTo(4);
        assertThat(resultado.getComentario()).isEqualTo("Buena cancha");
        assertThat(resultado.getUsuario()).isSameAs(usuario);
        assertThat(resultado.getCancha()).isSameAs(cancha);
        assertThat(resultado.getReserva()).isSameAs(reserva);
        verify(notificacionService).enviar(eq(propietario),
                eq(pe.pichangaapp.domain.TipoNotificacion.CALIFICACION),
                eq("Nueva calificación"),
                org.mockito.ArgumentMatchers.anyString(),
                eq("/panel/canchas/10/resenas"));
    }

    @Test
    void registrarReservaDeOtroUsuarioLanzaRecursoNoEncontrado() {
        Usuario usuario = usuario(1L, "Juan");
        Usuario otro = usuario(2L, "Pedro");
        Cancha cancha = cancha(10L, otro);
        Reserva reserva = reservaCompleta(1L, otro, cancha, EstadoReserva.COMPLETADA,
                LocalDateTime.now().minusHours(2));
        CalificacionForm form = form(1L, 4, "Comentario");

        when(reservaDAO.findCompletaPorId(1L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> service.registrar(1L, form))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("no existe");

        verifyNoInteractions(calificacionDAO);
        verifyNoInteractions(notificacionService);
    }

    @Test
    void registrarReservaNoJugadaLanzaReglaNegocio() {
        Usuario usuario = usuario(1L, "Juan");
        Cancha cancha = cancha(10L, usuario);
        Reserva reserva = reservaCompleta(1L, usuario, cancha, EstadoReserva.PENDIENTE,
                LocalDateTime.now().minusHours(2));
        CalificacionForm form = form(1L, 4, "Comentario");

        when(reservaDAO.findCompletaPorId(1L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> service.registrar(1L, form))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya se jugó");

        verifyNoInteractions(calificacionDAO);
        verifyNoInteractions(notificacionService);
    }

    @Test
    void registrarReservaFuturaLanzaReglaNegocio() {
        Usuario usuario = usuario(1L, "Juan");
        Cancha cancha = cancha(10L, usuario);
        Reserva reserva = reservaCompleta(1L, usuario, cancha, EstadoReserva.CONFIRMADA,
                LocalDateTime.now().plusHours(2));
        CalificacionForm form = form(1L, 4, "Comentario");

        when(reservaDAO.findCompletaPorId(1L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> service.registrar(1L, form))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("termine tu reserva");

        verifyNoInteractions(calificacionDAO);
        verifyNoInteractions(notificacionService);
    }

    @Test
    void registrarReservaDuplicadaLanzaReglaNegocio() {
        Usuario usuario = usuario(1L, "Juan");
        Cancha cancha = cancha(10L, usuario);
        Reserva reserva = reservaCompleta(1L, usuario, cancha, EstadoReserva.COMPLETADA,
                LocalDateTime.now().minusHours(2));
        CalificacionForm form = form(1L, 4, "Comentario");

        when(reservaDAO.findCompletaPorId(1L)).thenReturn(Optional.of(reserva));
        when(calificacionDAO.existsByReservaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(1L, form))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Ya calificaste");

        verify(calificacionDAO, never()).saveAndFlush(any(Calificacion.class));
        verifyNoInteractions(notificacionService);
    }

    @Test
    void responderExitoGuardaYNotifica() {
        Usuario usuario = usuario(1L, "Juan");
        Usuario propietario = usuario(2L, "Ana");
        Cancha cancha = cancha(10L, propietario);
        Calificacion calificacion = new Calificacion();
        calificacion.setId(50L);
        calificacion.setUsuario(usuario);
        calificacion.setCancha(cancha);

        when(calificacionDAO.findByIdAndCanchaPropietarioId(50L, 2L))
                .thenReturn(Optional.of(calificacion));

        service.responder(50L, 2L, "Gracias por tu opinión");

        assertThat(calificacion.getRespuestaPropietario()).isEqualTo("Gracias por tu opinión");
        assertThat(calificacion.getFechaRespuesta()).isNotNull();
        verify(calificacionDAO).save(calificacion);
        verify(notificacionService).enviar(eq(usuario),
                eq(pe.pichangaapp.domain.TipoNotificacion.CALIFICACION),
                eq("El complejo respondió tu reseña"),
                org.mockito.ArgumentMatchers.anyString(),
                eq("/canchas/10"));
    }

    @Test
    void responderCalificacionNoExistenteLanzaRecursoNoEncontrado() {
        when(calificacionDAO.findByIdAndCanchaPropietarioId(99L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.responder(99L, 2L, "Respuesta"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("no existe");

        verify(calificacionDAO, never()).save(any(Calificacion.class));
        verifyNoInteractions(notificacionService);
    }

    /* ---------- helpers ---------- */

    private CalificacionForm form(Long reservaId, int puntaje, String comentario) {
        CalificacionForm form = new CalificacionForm();
        form.setReservaId(reservaId);
        form.setPuntaje(puntaje);
        form.setComentario(comentario);
        return form;
    }

    private Usuario usuario(Long id, String nombre) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre(nombre);
        u.setEmail(nombre.toLowerCase() + "@test.pe");
        return u;
    }

    private Cancha cancha(Long id, Usuario propietario) {
        Cancha c = new Cancha();
        c.setId(id);
        c.setPropietario(propietario);
        c.setNombre("Cancha " + id);
        return c;
    }

    private Reserva reservaCompleta(Long id, Usuario usuario, Cancha cancha,
                                    EstadoReserva estado, LocalDateTime fechaHoraFin) {
        Reserva r = new Reserva();
        r.setId(id);
        r.setUsuario(usuario);
        r.setCancha(cancha);
        r.setEstado(estado);
        // getFechaHoraFin() combina fecha y horaFin; usamos una fecha/hora pasada o futura
        r.setFecha(fechaHoraFin.toLocalDate());
        r.setHoraFin(fechaHoraFin.toLocalTime());
        return r;
    }
}
