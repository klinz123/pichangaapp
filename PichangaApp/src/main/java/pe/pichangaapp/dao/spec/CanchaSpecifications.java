package pe.pichangaapp.dao.spec;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import pe.pichangaapp.domain.Cancha;
import pe.pichangaapp.dto.BusquedaForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Especificaciones JPA para la busqueda dinamica de canchas (RF04).
 * Evita concatenar SQL y mantiene la consulta tipada y segura.
 */
public final class CanchaSpecifications {

    private CanchaSpecifications() {
    }

    public static Specification<Cancha> conFiltros(BusquedaForm filtros) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.isTrue(root.get("activa")));

            if (StringUtils.isNotBlank(filtros.getTexto())) {
                String patron = "%" + filtros.getTexto().trim().toLowerCase() + "%";
                predicados.add(cb.or(
                        cb.like(cb.lower(root.get("nombre")), patron),
                        cb.like(cb.lower(root.get("direccion")), patron),
                        cb.like(cb.lower(root.get("distrito")), patron)
                ));
            }
            if (StringUtils.isNotBlank(filtros.getDistrito())) {
                predicados.add(cb.equal(cb.lower(root.get("distrito")),
                        filtros.getDistrito().trim().toLowerCase()));
            }
            if (filtros.getSuperficie() != null) {
                predicados.add(cb.equal(root.get("superficie"), filtros.getSuperficie()));
            }
            if (filtros.getPrecioMax() != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("precioHora"), filtros.getPrecioMax()));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
