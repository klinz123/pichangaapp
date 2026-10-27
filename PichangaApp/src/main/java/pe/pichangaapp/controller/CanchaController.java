package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.pichangaapp.dto.BusquedaForm;
import pe.pichangaapp.dto.view.CanchaTarjeta;
import pe.pichangaapp.dto.view.FranjaHoraria;
import pe.pichangaapp.service.CanchaService;
import pe.pichangaapp.service.DisponibilidadService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Capa web del catalogo de canchas.
 * - GET /canchas              -> listado con filtros (distrito, fecha, hora).
 * - GET /canchas/{id}         -> detalle de una cancha con parrilla de horarios.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/canchas")
public class CanchaController {

    private final CanchaService canchaService;
    private final DisponibilidadService disponibilidadService;

    @GetMapping
    public String listado(@ModelAttribute("filtros") BusquedaForm filtros, Model model) {
        List<CanchaTarjeta> tarjetas = canchaService.buscar(filtros).stream()
                .map(c -> CanchaTarjeta.de(c,
                        canchaService.promedioCalificacion(c.getId()),
                        canchaService.cantidadResenas(c.getId())))
                .collect(Collectors.toList());

        model.addAttribute("tarjetas", tarjetas);
        model.addAttribute("distritos", canchaService.distritos());
        model.addAttribute("total", tarjetas.size());
        return "canchas/listado";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id,
                          @RequestParam(required = false) LocalDate fecha,
                          Model model) {
        var cancha = canchaService.obtener(id);

        LocalDate fechaElegida = (fecha != null && !fecha.isBefore(LocalDate.now()))
                ? fecha
                : LocalDate.now().plusDays(1);

        List<FranjaHoraria> franjas = disponibilidadService.franjasDelDia(id, fechaElegida);

        model.addAttribute("cancha", cancha);
        model.addAttribute("fecha", fechaElegida);
        model.addAttribute("franjas", franjas);
        model.addAttribute("horasDisponibles", disponibilidadService.horasDisponibles(id, fechaElegida));
        return "canchas/detalle";
    }
}
