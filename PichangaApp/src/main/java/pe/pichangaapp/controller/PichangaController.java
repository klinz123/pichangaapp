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
import pe.pichangaapp.domain.EstadoPichanga;
import pe.pichangaapp.domain.NivelPichanga;
import pe.pichangaapp.security.UsuarioPrincipal;
import pe.pichangaapp.service.PichangaService;
import pe.pichangaapp.service.ReservaService;
import pe.pichangaapp.service.exception.ReglaNegocioException;
import pe.pichangaapp.dto.PichangaForm;

import java.time.LocalDate;
import java.util.List;

/**
 * Módulo de pichangas.
 * - GET  /pichangas                          → listado público de pichangas abiertas.
 * - GET  /pichangas/{id}                     → detalle de una pichanga.
 * - GET  /pichangas/crear                    → formulario para crear pichanga (requiere login).
 * - POST /pichangas/crear                    → crear pichanga (requiere login).
 * - POST /pichangas/{id}/unirse              → unirse a una pichanga (requiere login).
 * - POST /pichangas/{id}/salir               → salir de una pichanga (requiere login).
 * - POST /pichangas/{id}/cancelar            → cancelar pichanga (organizador, requiere login).
 * - GET  /pichangas/mis-pichangas            → pichangas del usuario (requiere login).
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/pichangas")
public class PichangaController {

    private final PichangaService pichangaService;
    private final ReservaService reservaService;

    private Long usuarioId(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return (principal != null) ? principal.getId() : null;
    }

    @GetMapping
    public String listado(@RequestParam(required = false) String distrito,
                          @RequestParam(required = false) LocalDate fecha,
                          @RequestParam(required = false) NivelPichanga nivel,
                          @RequestParam(required = false) String texto,
                          Model model) {
        var pichangas = pichangaService.listarPublicadas(distrito, fecha, nivel, texto);
        model.addAttribute("pichangas", pichangas);
        model.addAttribute("distritos", List.of("San Borja", "Miraflores", "Surco", "La Molina", "San Isidro"));
        model.addAttribute("niveles", NivelPichanga.values());
        model.addAttribute("filtroDistrito", distrito);
        model.addAttribute("filtroFecha", fecha);
        model.addAttribute("filtroNivel", nivel);
        model.addAttribute("filtroTexto", texto);
        return "pichangas/listado";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id,
                          @AuthenticationPrincipal UsuarioPrincipal principal,
                          Model model) {
        Long usuarioId = usuarioId(principal);
        var pichanga = pichangaService.obtenerCompleta(id);
        boolean esParticipante = usuarioId != null && pichangaService.esParticipante(id, usuarioId);
        boolean esOrganizador = usuarioId != null && pichanga.getOrganizador().getId().equals(usuarioId);
        var participantes = pichangaService.participantes(id);

        model.addAttribute("pichanga", pichanga);
        model.addAttribute("esParticipante", esParticipante);
        model.addAttribute("esOrganizador", esOrganizador);
        model.addAttribute("participantes", participantes);
        model.addAttribute("cuposDisponibles", pichanga.getCuposDisponibles());
        return "pichangas/detalle";
    }

    @GetMapping("/crear")
    public String formularioCrear(@AuthenticationPrincipal UsuarioPrincipal principal,
                                  Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        var reservas = reservaService.reservasParaPichanga(principal.getId());
        model.addAttribute("reservas", reservas);
        model.addAttribute("pichangaForm", new PichangaForm());
        model.addAttribute("niveles", NivelPichanga.values());
        return "pichangas/crear";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute("pichangaForm") PichangaForm form,
                        @AuthenticationPrincipal UsuarioPrincipal principal,
                        RedirectAttributes redirectAttributes) {
        try {
            var pichanga = pichangaService.crear(principal.getId(), form);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Pichanga publicada correctamente. Comparte el enlace para completar los cupos.");
            return "redirect:/pichangas/" + pichanga.getId();
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/pichangas/crear";
        }
    }

    @PostMapping("/{id}/unirse")
    public String unirse(@PathVariable Long id,
                         @AuthenticationPrincipal UsuarioPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            pichangaService.unirse(id, principal.getId());
            redirectAttributes.addFlashAttribute("mensajeExito", "Te uniste a la pichanga.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/pichangas/" + id;
    }

    @PostMapping("/{id}/salir")
    public String salir(@PathVariable Long id,
                        @AuthenticationPrincipal UsuarioPrincipal principal,
                        RedirectAttributes redirectAttributes) {
        try {
            pichangaService.salir(id, principal.getId());
            redirectAttributes.addFlashAttribute("mensajeExito", "Saliste de la pichanga.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/pichangas/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           @AuthenticationPrincipal UsuarioPrincipal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            pichangaService.cancelar(id, principal.getId());
            redirectAttributes.addFlashAttribute("mensajeExito", "Pichanga cancelada.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/pichangas/mis-pichangas";
    }

    @GetMapping("/mis-pichangas")
    public String misPichangas(@AuthenticationPrincipal UsuarioPrincipal principal,
                               Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        Long usuarioId = principal.getId();
        var organizadas = pichangaService.organizadasPor(usuarioId);
        var participando = pichangaService.delUsuario(usuarioId).stream()
                .filter(p -> !p.getOrganizador().getId().equals(usuarioId))
                .toList();
        model.addAttribute("organizadas", organizadas);
        model.addAttribute("participando", participando);
        return "pichangas/mis-pichangas";
    }
}
