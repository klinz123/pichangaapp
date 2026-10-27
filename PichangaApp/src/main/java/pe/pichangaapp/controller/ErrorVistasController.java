package pe.pichangaapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Sustituye al BasicErrorController de Spring Boot para servir las vistas
 * propias de error. Al implementar ErrorController, la autoconfiguracion
 * retira su controlador y se evita un mapeo ambiguo sobre /error.
 */
@Controller
public class ErrorVistasController implements ErrorController {

    @RequestMapping("/error")
    public String manejarError(HttpServletRequest request, Model model) {
        int codigo = codigoDeEstado(request);

        model.addAttribute("codigo", codigo);
        model.addAttribute("ruta", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

        if (codigo == 403) {
            return "error/403";
        }
        if (codigo == 404) {
            return "error/404";
        }
        return "error/5xx";
    }

    @RequestMapping("/error/403")
    public String accesoDenegado(Model model) {
        model.addAttribute("codigo", 403);
        return "error/403";
    }

    private int codigoDeEstado(HttpServletRequest request) {
        Object atributo = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int codigo = atributo instanceof Integer valor ? valor : 500;
        return codigo >= 400 ? codigo : 500;
    }
}
