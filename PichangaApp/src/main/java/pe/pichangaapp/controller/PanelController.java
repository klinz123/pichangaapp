package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.domain.EstadoReserva;
import pe.pichangaapp.domain.Reserva;
import pe.pichangaapp.domain.Rol;
import pe.pichangaapp.domain.TipoSuperficie;
import pe.pichangaapp.dto.CanchaForm;
import pe.pichangaapp.dto.view.FilaRanking;
import pe.pichangaapp.dto.view.PuntoSerie;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.CanchaService;
import pe.pichangaapp.service.DisponibilidadService;
import pe.pichangaapp.service.PagoService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.UsuarioService;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Panel del propietario de canchas.
 * - GET /panel                          → dashboard con métricas.
 * - GET /panel/canchas                  → listado de canchas del propietario.
 * - GET /panel/canchas/nueva            → formulario crear cancha.
 * - POST /panel/canchas                 → crear cancha.
 * - GET /panel/canchas/{id}/editar      → formulario editar cancha.
 * - POST /panel/canchas/{id}            → actualizar cancha.
 * - POST /panel/canchas/{id}/estado     → activar/desactivar cancha.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/panel")
public class PanelController {

    private final CanchaService canchaService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final PagoService pagoService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        Long propietarioId = principal.getId();
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        var canchas = canchaService.delPropietario(propietarioId);
        List<Reserva> reservasMes = reservaService.delPropietario(propietarioId, inicioMes, finMes).stream()
                .filter(this::esContable)
                .toList();
        long reservasHoy = reservasMes.stream().filter(r -> r.getFecha().equals(hoy)).count();
        BigDecimal ingresosMes = pagoService.ingresosNetos(propietarioId, inicioMes, finMes);

        long canchasActivas = canchas.stream().filter(Cancha::isActiva).count();
        double horasDisponibles = canchas.stream().filter(Cancha::isActiva)
                .mapToDouble(c -> {
                    if (c.getHoraApertura() == null || c.getHoraCierre() == null) {
                        return 0;
                    }
                    return Math.max(0, Duration.between(c.getHoraApertura(), c.getHoraCierre()).toMinutes() / 60.0);
                })
                .sum();
        int ocupacionHoy = horasDisponibles <= 0 ? 0
                : (int) Math.min(100, Math.round(reservasHoy * 100.0 / horasDisponibles));
        int franjasHoy = (int) Math.round(horasDisponibles);

        LocalDate desdeSerie = hoy.minusDays(6);
        Map<LocalDate, Long> porDia = reservaService.delPropietario(propietarioId, desdeSerie, hoy).stream()
                .filter(this::esContable)
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

        LocalDateTime ahora = LocalDateTime.now();
        List<Reserva> proximas = reservaService.delPropietario(propietarioId, hoy, hoy.plusDays(14)).stream()
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA
                        || r.getEstado() == EstadoReserva.PENDIENTE)
                .filter(r -> !r.getFechaHoraFin().isBefore(ahora))
                .sorted(Comparator.comparing(Reserva::getFechaHoraInicio))
                .limit(5)
                .toList();

        Map<Long, List<Reserva>> porCancha = reservasMes.stream()
                .collect(Collectors.groupingBy(r -> r.getCancha().getId()));
        List<FilaRanking> rankingBase = canchas.stream()
                .map(c -> {
                    List<Reserva> reservas = porCancha.getOrDefault(c.getId(), List.of());
                    BigDecimal ingresos = reservas.stream()
                            .map(Reserva::getPrecioTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new FilaRanking(c.getNombre(), c.getUbicacion(), reservas.size(), ingresos, 0);
                })
                .sorted(Comparator.comparingLong(FilaRanking::reservas).reversed())
                .limit(5)
                .toList();
        long maxRanking = Math.max(1, rankingBase.stream().mapToLong(FilaRanking::reservas).max().orElse(1));
        List<FilaRanking> ranking = rankingBase.stream()
                .map(f -> new FilaRanking(f.titulo(), f.subtitulo(), f.reservas(), f.ingresos(),
                        (int) Math.round(f.reservas() * 100.0 / maxRanking)))
                .toList();

        model.addAttribute("canchas", canchas);
        model.addAttribute("totalCanchas", canchas.size());
        model.addAttribute("canchasActivas", canchasActivas);
        model.addAttribute("reservasHoy", reservasHoy);
        model.addAttribute("reservasMes", reservasMes.size());
        model.addAttribute("reservasSemana", reservasSemana);
        model.addAttribute("ingresosMes", ingresosMes);
        model.addAttribute("ocupacionHoy", ocupacionHoy);
        model.addAttribute("franjasHoy", franjasHoy);
        model.addAttribute("serie", serie);
        model.addAttribute("proximas", proximas);
        model.addAttribute("ranking", ranking);
        model.addAttribute("pagoService", pagoService);
        model.addAttribute("hoy", hoy);
        model.addAttribute("rol", principal.getRol());
        return "panel/dashboard";
    }

    private boolean esContable(Reserva reserva) {
        return reserva.getEstado() == EstadoReserva.CONFIRMADA
                || reserva.getEstado() == EstadoReserva.COMPLETADA;
    }

    @GetMapping("/canchas")
    public String misCanchas(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        var canchas = canchaService.delPropietario(principal.getId());
        model.addAttribute("canchas", canchas);
        return "panel/canchas/listado";
    }

    @GetMapping("/canchas/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("canchaForm", new CanchaForm());
        model.addAttribute("superficies", TipoSuperficie.values());
        model.addAttribute("distritos", canchaService.distritos());
        return "panel/canchas/formulario";
    }

    @PostMapping("/canchas")
    public String crear(@ModelAttribute("canchaForm") CanchaForm form,
                        @AuthenticationPrincipal UsuarioPrincipal principal,
                        RedirectAttributes redirectAttributes) {
        try {
            canchaService.crear(principal.getId(), form);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cancha registrada correctamente.");
            return "redirect:/panel/canchas";
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/panel/canchas/nueva";
        }
    }

    @GetMapping("/canchas/{id}/editar")
    public String formularioEditar(@PathVariable Long id,
                                   @AuthenticationPrincipal UsuarioPrincipal principal,
                                   Model model) {
        var cancha = canchaService.obtener(id);
        if (!canchaService.perteneceA(id, principal.getId())) {
            return "redirect:/panel/canchas";
        }
        model.addAttribute("cancha", cancha);
        model.addAttribute("canchaForm", canchaService.formularioDe(cancha));
        model.addAttribute("superficies", TipoSuperficie.values());
        model.addAttribute("distritos", canchaService.distritos());
        return "panel/canchas/editar";
    }

    @PostMapping("/canchas/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("canchaForm") CanchaForm form,
                             @AuthenticationPrincipal UsuarioPrincipal principal,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!canchaService.perteneceA(id, principal.getId())) {
            return "redirect:/panel/canchas";
        }
        try {
            canchaService.actualizar(id, principal.getId(), form);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cancha actualizada correctamente.");
            return "redirect:/panel/canchas";
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            modelAttributes(model, id, principal, form);
            return "panel/canchas/editar";
        }
    }

    @PostMapping("/canchas/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam boolean activa,
                                @AuthenticationPrincipal UsuarioPrincipal principal,
                                RedirectAttributes redirectAttributes) {
        if (!canchaService.perteneceA(id, principal.getId())) {
            return "redirect:/panel/canchas";
        }
        canchaService.cambiarEstadoPropietario(id, principal.getId(), activa);
        redirectAttributes.addFlashAttribute("mensajeExito",
                activa ? "Cancha activada." : "Cancha desactivada.");
        return "redirect:/panel/canchas";
    }

    private void modelAttributes(Model model, Long id, UsuarioPrincipal principal, CanchaForm form) {
        model.addAttribute("cancha", canchaService.obtener(id));
        model.addAttribute("canchaForm", form);
        model.addAttribute("superficies", TipoSuperficie.values());
        model.addAttribute("distritos", canchaService.distritos());
    }
}
