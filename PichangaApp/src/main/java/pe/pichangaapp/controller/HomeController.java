package pe.pichangaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.domain.Pichanga;
import pe.pichangaapp.dto.view.CanchaTarjeta;
import pe.pichangaapp.service.CanchaService;
import pe.pichangaapp.service.PichangaService;

import java.util.Comparator;
import java.util.List;

/**
 * Pagina de inicio publica: hero, metricas reales del catalogo y una
 * seleccion de canchas y pichangas abiertas.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private static final int CANCHAS_DESTACADAS = 6;
    private static final int PICHANGAS_DESTACADAS = 3;

    private final CanchaService canchaService;
    private final PichangaService pichangaService;

    @GetMapping("/")
    public String inicio(Model model) {
        List<Cancha> activas = canchaService.activas();

        List<CanchaTarjeta> tarjetas = activas.stream()
                .map(cancha -> CanchaTarjeta.de(cancha,
                        canchaService.promedioCalificacion(cancha.getId()),
                        canchaService.cantidadResenas(cancha.getId())))
                .sorted(Comparator.comparingDouble(CanchaTarjeta::promedio).reversed()
                        .thenComparing(CanchaTarjeta::nombre, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<Pichanga> pichangas = pichangaService.listarPublicadas(null, null, null, null);

        double promedioGeneral = tarjetas.stream()
                .filter(tarjeta -> !tarjeta.isSinResenas())
                .mapToDouble(CanchaTarjeta::promedio)
                .average()
                .orElse(0);

        model.addAttribute("canchasDestacadas", tarjetas.stream().limit(CANCHAS_DESTACADAS).toList());
        model.addAttribute("pichangasDestacadas", pichangas.stream().limit(PICHANGAS_DESTACADAS).toList());
        model.addAttribute("totalCanchas", activas.size());
        model.addAttribute("distritos", canchaService.distritos());
        model.addAttribute("totalDistritos", canchaService.distritos().size());
        model.addAttribute("totalPichangas", pichangas.size());
        model.addAttribute("promedioGeneral", Math.round(promedioGeneral * 10) / 10.0);
        return "home";
    }
}
