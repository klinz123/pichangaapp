package pe.pichangaapp.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.pichangaapp.config.SecurityConfig;
import pe.pichangaapp.domain.NivelPichanga;
import pe.pichangaapp.domain.Pichanga;
import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.dto.PichangaForm;
import pe.pichangaapp.security.LoginSuccessHandler;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.security.UsuarioUserDetailsService;
import pe.pichangaapp.service.NotificacionService;
import pe.pichangaapp.service.PichangaService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.UsuarioService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas de la capa de seguridad sobre {@link PichangaController}.
 *
 * <p>Se carga solo este controlador ({@code @WebMvcTest}) junto a {@link SecurityConfig}
 * real, de modo que se comprueba de verdad la cadena de filtros de Spring Security:
 * acceso anónimo, CSRF y autenticación.</p>
 *
 * <p>Además del controlador, el slice incluye los {@code @ControllerAdvice}
 * ({@code GlobalModelAttributes} y {@code GlobalExceptionHandler}), por eso se mockean
 * también {@code UsuarioService} y {@code NotificacionService}.</p>
 */
@WebMvcTest(controllers = PichangaController.class)
@Import(SecurityConfig.class)
class PichangaControllerSecurityTest {

    private static final Long USUARIO_ID = 7L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PichangaService pichangaService;
    @MockBean
    private ReservaService reservaService;
    @MockBean
    private UsuarioUserDetailsService userDetailsService;
    @MockBean
    private LoginSuccessHandler loginSuccessHandler;
    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private NotificacionService notificacionService;

    // ------------------------------------------------------------------
    // GET /pichangas/crear
    // ------------------------------------------------------------------

    @Test
    void getCrearSinSesionRedirigeAlLogin() throws Exception {
        mockMvc.perform(get("/pichangas/crear"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verifyNoInteractions(pichangaService, reservaService);
    }

    @Test
    void getCrearAutenticadoMuestraElFormulario() throws Exception {
        when(usuarioService.buscarPorId(USUARIO_ID)).thenReturn(usuario());
        when(notificacionService.contarNoLeidas(USUARIO_ID)).thenReturn(0L);
        when(notificacionService.ultimas(USUARIO_ID)).thenReturn(List.of());
        when(reservaService.reservasParaPichanga(USUARIO_ID)).thenReturn(List.of());

        mockMvc.perform(get("/pichangas/crear").with(user(usuarioPrincipal())))
                .andExpect(status().isOk())
                .andExpect(view().name("pichangas/crear"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Crear pichanga")));

        verify(reservaService).reservasParaPichanga(USUARIO_ID);
    }

    // ------------------------------------------------------------------
    // POST /pichangas/crear
    // ------------------------------------------------------------------

    @Test
    void postCrearSinTokenCsrfDevuelve403() throws Exception {
        mockMvc.perform(post("/pichangas/crear")
                        .with(user(usuarioPrincipal()))
                        .param("reservaId", "5")
                        .param("cuposTotal", "10")
                        .param("nivel", "MIXTO")
                        .param("precioParticipante", "10.00"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pichangaService);
    }

    @Test
    void postCrearConTokenCsrfRedirigeAlDetalleDeLaPichanga() throws Exception {
        when(usuarioService.buscarPorId(USUARIO_ID)).thenReturn(usuario());
        when(notificacionService.contarNoLeidas(USUARIO_ID)).thenReturn(0L);
        when(notificacionService.ultimas(USUARIO_ID)).thenReturn(List.of());
        when(pichangaService.crear(eq(USUARIO_ID), any(PichangaForm.class)))
                .thenReturn(pichangaPublicada());

        mockMvc.perform(post("/pichangas/crear")
                        .with(user(usuarioPrincipal()))
                        .with(csrf())
                        .param("reservaId", "5")
                        .param("cuposTotal", "12")
                        .param("nivel", "MIXTO")
                        .param("precioParticipante", "15.50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pichangas/1"))
                .andExpect(flash().attribute("mensajeExito",
                        containsString("Pichanga publicada")));

        ArgumentCaptor<PichangaForm> captor = ArgumentCaptor.forClass(PichangaForm.class);
        verify(pichangaService).crear(eq(USUARIO_ID), captor.capture());

        PichangaForm enviado = captor.getValue();
        assertThat(enviado.getReservaId()).isEqualTo(5L);
        assertThat(enviado.getCuposTotal()).isEqualTo(12);
        assertThat(enviado.getNivel()).isEqualTo(NivelPichanga.MIXTO);
        assertThat(enviado.getPrecioParticipante()).isEqualByComparingTo("15.50");
    }

    @Test
    void postCrearSinSesionRedirigeAlLogin() throws Exception {
        mockMvc.perform(post("/pichangas/crear")
                        .with(csrf())
                        .param("reservaId", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verifyNoInteractions(pichangaService);
    }

    // ------------------------------------------------------------------
    // GET /pichangas (listado público)
    // ------------------------------------------------------------------

    @Test
    void getListadoPublicoMuestraLaVistaListado() throws Exception {
        when(pichangaService.listarPublicadas(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/pichangas").param("fecha", "2026-11-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("pichangas/listado"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Pichangas abiertas")));

        verify(pichangaService).listarPublicadas(isNull(), eq(LocalDate.parse("2026-11-01")),
                isNull(), isNull());
    }

    // ------------------------------------------------------------------
    // Datos de prueba
    // ------------------------------------------------------------------

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(USUARIO_ID);
        usuario.setNombre("Carlos Perez");
        usuario.setEmail("carlos@pichangaapp.pe");
        usuario.setPasswordHash("$2a$12$abcdefghijklmnopqrstuv");
        usuario.setRol(Rol.JUGADOR);
        usuario.setVerificado(true);
        usuario.setActivo(true);
        return usuario;
    }

    private UsuarioPrincipal usuarioPrincipal() {
        return new UsuarioPrincipal(usuario());
    }

    private Pichanga pichangaPublicada() {
        Pichanga pichanga = new Pichanga();
        pichanga.setId(1L);
        return pichanga;
    }
}
