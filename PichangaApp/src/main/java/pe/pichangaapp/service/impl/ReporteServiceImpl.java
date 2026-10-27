package pe.pichangaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.pichangaapp.dao.*;
import pe.pichangaapp.domain.*;
import pe.pichangaapp.dto.view.ReporteResumen;
import pe.pichangaapp.dto.view.ResumenAdmin;
import pe.pichangaapp.dto.view.ResumenPanel;
import pe.pichangaapp.service.DisponibilidadService;
import pe.pichangaapp.service.ReporteService;
import pe.pichangaapp.service.exception.ReglaNegocioException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DIA_CORTO = DateTimeFormatter.ofPattern("EEE d", new Locale("es", "PE"));
    private static final List<EstadoReserva> CONTABLES =
            List.of(EstadoReserva.CONFIRMADA, EstadoReserva.COMPLETADA);
    private static final List<EstadoPago> PAGOS_NETOS =
            List.of(EstadoPago.PAGADO, EstadoPago.REEMBOLSADO_PARCIAL);

    private final CanchaDAO canchaDAO;
    private final ReservaDAO reservaDAO;
    private final PagoDAO pagoDAO;
    private final PichangaDAO pichangaDAO;
    private final CalificacionDAO calificacionDAO;
    private final UsuarioDAO usuarioDAO;
    private final HorarioDAO horarioDAO;
    private final DisponibilidadService disponibilidadService;

    @Override
    @Transactional(readOnly = true)
    public ResumenPanel resumenPropietario(Long propietarioId) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        List<Cancha> canchas = canchaDAO.findByPropietarioIdOrderByNombreAsc(propietarioId);
        List<Cancha> activas = canchas.stream().filter(Cancha::isActiva).toList();

        BigDecimal ingresos = pagoDAO.sumarPagosNetos(propietarioId, inicioMes, hoy);
        Double promedio = calificacionDAO.promedioDelPropietario(propietarioId);

        long franjasTotales = 0;
        long franjasOcupadas = 0;
        for (Cancha cancha : activas) {
            for (var franja : disponibilidadService.franjasDelDia(cancha.getId(), hoy)) {
                if (franja.getEstado() == pe.pichangaapp.dto.view.EstadoFranja.PASADA
                        || franja.getEstado() == pe.pichangaapp.dto.view.EstadoFranja.BLOQUEADA) {
                    continue;
                }
                franjasTotales++;
                if (franja.getEstado() == pe.pichangaapp.dto.view.EstadoFranja.RESERVADA) {
                    franjasOcupadas++;
                }
            }
        }
        double ocupacion = franjasTotales == 0 ? 0
                : Math.round(franjasOcupadas * 1000.0 / franjasTotales) / 10.0;

        List<Reserva> proximas = reservaDAO.listarDelPropietarioEnRango(
                        propietarioId, hoy, hoy.plusDays(7),
                        List.of(EstadoReserva.CONFIRMADA))
                .stream().limit(6).toList();

        LocalDate lunes = hoy.with(java.time.DayOfWeek.MONDAY);
        List<Reserva> semana = reservaDAO.listarDelPropietarioEnRango(
                propietarioId, lunes, lunes.plusDays(6), CONTABLES);
        Map<LocalDate, Long> conteo = semana.stream()
                .collect(Collectors.groupingBy(Reserva::getFecha, Collectors.counting()));
        Map<String, Long> serieSemana = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate dia = lunes.plusDays(i);
            serieSemana.put(dia.format(DIA_CORTO), conteo.getOrDefault(dia, 0L));
        }

        return new ResumenPanel(
                reservaDAO.contarDelDia(propietarioId, hoy),
                ingresos,
                activas.size(),
                ocupacion,
                promedio == null ? 0 : Math.round(promedio * 10) / 10.0,
                pichangaDAO.countByCanchaPropietarioIdAndEstado(propietarioId, EstadoPichanga.ABIERTA),
                proximas,
                serieSemana
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenAdmin resumenAdmin() {
        long reservas = reservaDAO.count();
        return new ResumenAdmin(
                usuarioDAO.count(),
                usuarioDAO.countByRol(Rol.JUGADOR),
                usuarioDAO.countByRol(Rol.PROPIETARIO),
                canchaDAO.count(),
                reservas,
                pichangaDAO.count(),
                usuarioDAO.findByEstadoVerificacionOrderByFechaRegistroAsc(EstadoVerificacion.PENDIENTE).size(),
                pagoDAO.sumarPagosNetosGlobal()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResumen reportePropietario(Long propietarioId, LocalDate desde, LocalDate hasta) {
        Rango rango = normalizar(desde, hasta);
        List<Reserva> reservas = reservaDAO.listarDelPropietarioEnRango(
                propietarioId, rango.desde(), rango.hasta(), List.of(EstadoReserva.values()));
        List<Pago> pagos = pagoDAO.findByReservaCanchaPropietarioId(propietarioId).stream()
                .filter(p -> !p.getReserva().getFecha().isBefore(rango.desde())
                        && !p.getReserva().getFecha().isAfter(rango.hasta()))
                .toList();

        BigDecimal ingresos = pagos.stream()
                .filter(p -> PAGOS_NETOS.contains(p.getEstado()))
                .map(p -> p.getMonto().subtract(p.getMontoReembolsado()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long contables = reservas.stream().filter(r -> CONTABLES.contains(r.getEstado())).count();
        long canceladas = reservas.stream().filter(r -> r.getEstado() == EstadoReserva.CANCELADA).count();
        double calificacion = Optional.ofNullable(
                        calificacionDAO.promedioDelPropietario(propietarioId)).orElse(0.0);
        calificacion = Math.round(calificacion * 10) / 10.0;

        double ocupacion = calcularOcupacion(propietarioId, rango, reservas);
        List<Cancha> canchas = canchaDAO.findByPropietarioIdOrderByNombreAsc(propietarioId);

        Map<LocalDate, List<Reserva>> porFecha = reservas.stream()
                .filter(r -> CONTABLES.contains(r.getEstado()))
                .collect(Collectors.groupingBy(Reserva::getFecha));
        Map<LocalDate, BigDecimal> ingresosPorFecha = pagos.stream()
                .filter(p -> PAGOS_NETOS.contains(p.getEstado()))
                .collect(Collectors.groupingBy(p -> p.getReserva().getFecha(),
                        Collectors.reducing(BigDecimal.ZERO,
                                p -> p.getMonto().subtract(p.getMontoReembolsado()), BigDecimal::add)));

        List<ReporteResumen.SerieDia> serie = new ArrayList<>();
        LocalDate cursor = rango.desde();
        while (!cursor.isAfter(rango.hasta())) {
            List<Reserva> delDia = porFecha.getOrDefault(cursor, List.of());
            serie.add(new ReporteResumen.SerieDia(cursor, cursor.format(DIA_CORTO), delDia.size(),
                    ingresosPorFecha.getOrDefault(cursor, BigDecimal.ZERO)));
            cursor = cursor.plusDays(1);
        }

        Map<Long, List<Reserva>> porCancha = reservas.stream()
                .filter(r -> CONTABLES.contains(r.getEstado()))
                .collect(Collectors.groupingBy(r -> r.getCancha().getId()));
        List<ReporteResumen.IngresoCancha> ingresosCancha = canchas.stream().map(c -> {
            List<Reserva> lista = porCancha.getOrDefault(c.getId(), List.of());
            BigDecimal total = lista.stream().map(Reserva::getPrecioTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new ReporteResumen.IngresoCancha(c.getNombre(), lista.size(), total);
        }).toList();

        Map<String, Long> metodos = new LinkedHashMap<>();
        for (MetodoPago metodo : MetodoPago.values()) {
            long cantidad = pagos.stream()
                    .filter(p -> PAGOS_NETOS.contains(p.getEstado()) && p.getMetodo() == metodo)
                    .count();
            if (cantidad > 0) {
                metodos.put(metodo.getEtiqueta(), cantidad);
            }
        }

        return new ReporteResumen(rango.desde(), rango.hasta(), ingresos, contables, canceladas,
                ocupacion, calificacion, serie, ingresosCancha, metodos);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarExcel(Long propietarioId, LocalDate desde, LocalDate hasta) {
        Rango rango = normalizar(desde, hasta);
        ReporteResumen resumen = reportePropietario(propietarioId, rango.desde(), rango.hasta());
        List<Reserva> reservas = reservaDAO.listarDelPropietarioEnRango(
                propietarioId, rango.desde(), rango.hasta(), List.of(EstadoReserva.values()));

        try (XSSFWorkbook libro = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            CellStyle titulo = libro.createCellStyle();
            Font fuenteTitulo = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 16);
            titulo.setFont(fuenteTitulo);

            CellStyle cabecera = libro.createCellStyle();
            Font fuenteCabecera = libro.createFont();
            fuenteCabecera.setBold(true);
            fuenteCabecera.setColor(IndexedColors.WHITE.getIndex());
            cabecera.setFont(fuenteCabecera);
            cabecera.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            cabecera.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cabecera.setAlignment(HorizontalAlignment.LEFT);

            CellStyle moneda = libro.createCellStyle();
            moneda.setDataFormat(libro.createDataFormat().getFormat("\"S/ \"#,##0.00"));

            Sheet hoja = libro.createSheet("Resumen");
            int fila = 0;
            fila = tituloHoja(hoja, fila, "PichangaApp · Reporte de ingresos", titulo);
            fila = filaTexto(hoja, fila, "Periodo", rango.desde().format(FECHA) + " al " + rango.hasta().format(FECHA));
            fila = filaMoneda(hoja, fila, "Ingresos netos", resumen.ingresos(), moneda);
            fila = filaTexto(hoja, fila, "Reservas confirmadas", String.valueOf(resumen.reservas()));
            fila = filaTexto(hoja, fila, "Reservas canceladas", String.valueOf(resumen.canceladas()));
            fila = filaTexto(hoja, fila, "Ocupación promedio", resumen.ocupacion() + " %");
            fila = filaTexto(hoja, fila, "Calificación promedio", resumen.calificacionPromedio() + " / 5");
            fila++;
            fila = tablaIngresos(hoja, fila, resumen, cabecera, moneda);
            hoja.setColumnWidth(0, 30 * 256);
            hoja.setColumnWidth(1, 22 * 256);
            hoja.setColumnWidth(2, 18 * 256);

            Sheet detalle = libro.createSheet("Reservas");
            String[] cabeceras = {"Código", "Fecha", "Hora", "Cancha", "Cliente", "Estado", "Total", "Pagado"};
            Row filaCabecera = detalle.createRow(0);
            for (int i = 0; i < cabeceras.length; i++) {
                Cell celda = filaCabecera.createCell(i);
                celda.setCellValue(cabeceras[i]);
                celda.setCellStyle(cabecera);
            }
            int f = 1;
            for (Reserva reserva : reservas) {
                Row row = detalle.createRow(f++);
                row.createCell(0).setCellValue(reserva.getCodigo());
                row.createCell(1).setCellValue(reserva.getFecha().format(FECHA));
                row.createCell(2).setCellValue(reserva.getHoraInicio() + " - " + reserva.getHoraFin());
                row.createCell(3).setCellValue(reserva.getCancha().getNombre());
                row.createCell(4).setCellValue(reserva.getUsuario().getNombre());
                row.createCell(5).setCellValue(reserva.getEstado().getEtiqueta());
                Cell total = row.createCell(6);
                total.setCellValue(reserva.getPrecioTotal().doubleValue());
                total.setCellStyle(moneda);
                Cell pagado = row.createCell(7);
                pagado.setCellValue(reserva.estaVigente() ? "Sí" : "No");
            }
            for (int i = 0; i < cabeceras.length; i++) {
                detalle.autoSizeColumn(i);
            }

            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el reporte Excel.", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarCsv(Long propietarioId, LocalDate desde, LocalDate hasta) {
        Rango rango = normalizar(desde, hasta);
        List<Reserva> reservas = reservaDAO.listarDelPropietarioEnRango(
                propietarioId, rango.desde(), rango.hasta(), List.of(EstadoReserva.values()));

        CSVFormat formato = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader("Codigo", "Fecha", "Hora inicio", "Hora fin", "Cancha", "Cliente",
                        "Estado", "Total", "Pagado")
                .setRecordSeparator("\r\n")
                .build();

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(salida, StandardCharsets.UTF_8)) {
            salida.write('\uFEFF');
            try (CSVPrinter printer = new CSVPrinter(writer, formato)) {
                for (Reserva reserva : reservas) {
                    printer.printRecord(
                            reserva.getCodigo(),
                            reserva.getFecha().format(FECHA),
                            reserva.getHoraInicio(),
                            reserva.getHoraFin(),
                            reserva.getCancha().getNombre(),
                            reserva.getUsuario().getNombre(),
                            reserva.getEstado().getEtiqueta(),
                            reserva.getPrecioTotal().toPlainString(),
                            reserva.estaVigente() ? "Si" : "No");
                }
            }
            return salida.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el reporte CSV.", ex);
        }
    }

    private int tituloHoja(Sheet hoja, int fila, String texto, CellStyle estilo) {
        Row row = hoja.createRow(fila);
        Cell cell = row.createCell(0);
        cell.setCellValue(texto);
        cell.setCellStyle(estilo);
        return fila + 2;
    }

    private int filaTexto(Sheet hoja, int fila, String etiqueta, String valor) {
        Row row = hoja.createRow(fila);
        row.createCell(0).setCellValue(etiqueta);
        row.createCell(1).setCellValue(valor);
        return fila + 1;
    }

    private int filaMoneda(Sheet hoja, int fila, String etiqueta, BigDecimal valor, CellStyle estilo) {
        Row row = hoja.createRow(fila);
        row.createCell(0).setCellValue(etiqueta);
        Cell cell = row.createCell(1);
        cell.setCellValue(valor.doubleValue());
        cell.setCellStyle(estilo);
        return fila + 1;
    }

    private int tablaIngresos(Sheet hoja, int fila, ReporteResumen resumen,
                              CellStyle cabecera, CellStyle moneda) {
        Row titulo = hoja.createRow(fila++);
        Cell celda = titulo.createCell(0);
        celda.setCellValue("Ingresos por cancha");
        celda.setCellStyle(cabecera);

        Row cab = hoja.createRow(fila++);
        cab.createCell(0).setCellValue("Cancha");
        cab.createCell(1).setCellValue("Reservas");
        cab.createCell(2).setCellValue("Ingresos");
        for (int i = 0; i < 3; i++) {
            cab.getCell(i).setCellStyle(cabecera);
        }
        for (ReporteResumen.IngresoCancha item : resumen.porCancha()) {
            Row row = hoja.createRow(fila++);
            row.createCell(0).setCellValue(item.cancha());
            row.createCell(1).setCellValue(item.reservas());
            Cell monto = row.createCell(2);
            monto.setCellValue(item.ingresos().doubleValue());
            monto.setCellStyle(moneda);
        }
        return fila;
    }

    private double calcularOcupacion(Long propietarioId, Rango rango, List<Reserva> reservas) {
        List<Cancha> canchas = canchaDAO.findByPropietarioIdOrderByNombreAsc(propietarioId).stream()
                .filter(Cancha::isActiva).toList();
        if (canchas.isEmpty()) {
            return 0;
        }
        Map<Long, Map<Integer, Horario>> horarios = new HashMap<>();
        for (Cancha cancha : canchas) {
            Map<Integer, Horario> porDia = new HashMap<>();
            for (Horario horario : horarioDAO.findByCanchaIdOrderByDiaSemanaAsc(cancha.getId())) {
                porDia.put(horario.getDiaSemana(), horario);
            }
            horarios.put(cancha.getId(), porDia);
        }

        long horasDisponibles = 0;
        LocalDate cursor = rango.desde();
        while (!cursor.isAfter(rango.hasta())) {
            for (Cancha cancha : canchas) {
                Horario horario = horarios.get(cancha.getId()).get(cursor.getDayOfWeek().getValue());
                if (horario != null) {
                    if (horario.isActivo()) {
                        horasDisponibles += Duration.between(
                                horario.getHoraApertura(), horario.getHoraCierre()).toHours();
                    }
                } else {
                    horasDisponibles += Duration.between(
                            cancha.getHoraApertura(), cancha.getHoraCierre()).toHours();
                }
            }
            cursor = cursor.plusDays(1);
        }
        if (horasDisponibles <= 0) {
            return 0;
        }
        long horasReservadas = reservas.stream()
                .filter(r -> CONTABLES.contains(r.getEstado()))
                .mapToLong(r -> Duration.between(r.getHoraInicio(), r.getHoraFin()).toHours())
                .sum();
        double porcentaje = horasReservadas * 100.0 / horasDisponibles;
        return Math.round(Math.min(porcentaje, 100) * 10) / 10.0;
    }

    private Rango normalizar(LocalDate desde, LocalDate hasta) {
        LocalDate hoy = LocalDate.now();
        LocalDate fin = hasta == null ? hoy : hasta;
        LocalDate inicio = desde == null ? fin.minusDays(29) : desde;
        if (inicio.isAfter(fin)) {
            LocalDate temporal = inicio;
            inicio = fin;
            fin = temporal;
        }
        if (Duration.between(inicio.atStartOfDay(), fin.atStartOfDay()).toDays() > 365) {
            throw new ReglaNegocioException("El rango del reporte no puede superar 365 días.");
        }
        if (fin.isAfter(hoy.plusYears(1))) {
            throw new ReglaNegocioException("El rango del reporte no es válido.");
        }
        return new Rango(inicio, fin);
    }

    private record Rango(LocalDate desde, LocalDate hasta) {
    }
}
