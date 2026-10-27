package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.domain.Usuario;
import pe.pichangaapp.dto.view.FilaRanking;
import pe.pichangaapp.dto.view.PuntoSerie;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.CanchaService;
import pe.pichangaapp.service.PagoService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.UsuarioService;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Administración del sistema (rol ADMIN).
 * - GET  /admin                          → dashboard con métricas globales.
 * - GET  /admin/usuarios                 → listado de usuarios.
 * - GET  /admin/canchas                  → listado de todas las canchas.
 * - POST /admin/canchas/{id}/estado      → activar/desactivar cancha (admin).
 * - POST /admin/usuarios/{id}/estado     → activar/desactivar usuario.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final CanchaService canchaService;
    private final ReservaService reservaService;
    private final PagoService pagoService;

    @GetMapping
    public String dashboard(Model model) {
        var usuarios = usuarioService.listarTodos();
        var canchas = canchaService.todas();
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        long usuariosTotal = usuarios.size();
        long jugadores = usuarios.stream().filter(u -> u.getRol() == Rol.JUGADOR).count();
        long propietarios = usuarios.stream().filter(u -> u.getRol() == Rol.PROPIETARIO).count();
        long admins = usuarios.stream().filter(u -> u.getRol() == Rol.ADMIN).count();
        long solicitudesPendientes = usuarioService.solicitudesPendientes().size();

        List<Reserva> reservasMes = reservaService.reservasEnRango(inicioMes, finMes);
        long reservasHoy = reservasMes.stream().filter(r -> r.getFecha().equals(hoy)).count();
        BigDecimal ingresosMes = pagoService.ingresosNetosGlobales(inicioMes, finMes);

        LocalDate desdeSerie = hoy.minusDays(6);
        Map<LocalDate, Long> porDia = reservaService.reservasEnRango(desdeSerie, hoy).stream()
                .collect(Collectors.groupingBy(Reserva::getFecha, Collectors.counting()));
        long maxDia = Math.max(1, porDia.values().stream().mapToLong(Long::longValue).max().orElse(0));
        DateTimeFormatter diaCorto = DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es-PE"));
        List<PuntoSerie> serie = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    LocalDate dia = desdeSerie.plusDays(i);
                    long valor = porDia.getOrDefault(dia, 0L);
                    int altura = (int) Math.max(6, Math.round(valor * 100.0 / maxDia));
                    return new PuntoSerie(dia.format(diaCorto), valor, altura, dia.equals(hoy));
                })
                .toList();
        long reservasSemana = serie.stream().mapToLong(PuntoSerie::valor).sum();

        Map<Long, List<Reserva>> porCancha = reservasMes.stream()
                .collect(Collectors.groupingBy(r -> r.getCancha().getId()));
        List<FilaRanking> rankingBase = porCancha.values().stream()
                .map(reservas -> {
                    var cancha = reservas.get(0).getCancha();
                    BigDecimal ingresos = reservas.stream()
                            .map(Reserva::getPrecioTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new FilaRanking(cancha.getNombre(), cancha.getPropietario().getNombre(),
                            reservas.size(), ingresos, 0);
                })
                .sorted(Comparator.comparingLong(FilaRanking::reservas).reversed())
                .limit(5)
                .toList();
        long maxRanking = Math.max(1, rankingBase.stream().mapToLong(FilaRanking::reservas).max().orElse(1));
        List<FilaRanking> topCanchas = rankingBase.stream()
                .map(f -> new FilaRanking(f.titulo(), f.subtitulo(), f.reservas(), f.ingresos(),
                        (int) Math.round(f.reservas() * 100.0 / maxRanking)))
                .toList();

        List<Usuario> ultimosUsuarios = usuarios.stream()
                .sorted(Comparator.comparing(Usuario::getId).reversed())
                .limit(5)
                .toList();

        model.addAttribute("usuariosTotal", usuariosTotal);
        model.addAttribute("jugadores", jugadores);
        model.addAttribute("propietarios", propietarios);
        model.addAttribute("admins", admins);
        model.addAttribute("canchasTotal", canchas.size());
        model.addAttribute("canchasActivas", canchas.stream().filter(Cancha::isActiva).count());
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
        model.addAttribute("reservasTotal", reservaService.contarTodas());
        model.addAttribute("reservasMes", reservasMes.size());
        model.addAttribute("reservasHoy", reservasHoy);
        model.addAttribute("reservasSemana", reservasSemana);
        model.addAttribute("ingresosMes", ingresosMes);
        model.addAttribute("serie", serie);
        model.addAttribute("topCanchas", topCanchas);
        model.addAttribute("ultimosUsuarios", ultimosUsuarios);
        model.addAttribute("hoy", hoy);
        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        var usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

    @GetMapping("/canchas")
    public String canchas(Model model) {
        var canchas = canchaService.todas();
        model.addAttribute("canchas", canchas);
        return "admin/canchas";
    }

    @PostMapping("/canchas/{id}/estado")
    public String cambiarEstadoCancha(@PathVariable Long id,
                                      @RequestParam boolean activa,
                                      RedirectAttributes redirectAttributes) {
        canchaService.cambiarEstadoAdmin(id, activa);
        redirectAttributes.addFlashAttribute("mensajeExito",
                activa ? "Cancha activada." : "Cancha desactivada.");
        return "redirect:/admin/canchas";
    }

    @PostMapping("/usuarios/{id}/estado")
    public String cambiarEstadoUsuario(@PathVariable Long id,
                                       @RequestParam boolean activo,
                                       @AuthenticationPrincipal UsuarioPrincipal principal,
                                       RedirectAttributes redirectAttributes) {
        if (principal.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("mensajeError", "No puedes desactivar tu propia cuenta.");
            return "redirect:/admin/usuarios";
        }
        try {
            usuarioService.alternarActivo(id);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    activo ? "Usuario activado." : "Usuario desactivado.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
