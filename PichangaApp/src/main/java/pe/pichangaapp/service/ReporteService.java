package pe.pichangaapp.service;

import pe.pichangaapp.dto.view.ReporteResumen;
import pe.pichangaapp.dto.view.ResumenAdmin;
import pe.pichangaapp.dto.view.ResumenPanel;

import java.time.LocalDate;

public interface ReporteService {

    ResumenPanel resumenPropietario(Long propietarioId);

    ResumenAdmin resumenAdmin();

    ReporteResumen reportePropietario(Long propietarioId, LocalDate desde, LocalDate hasta);

    byte[] exportarExcel(Long propietarioId, LocalDate desde, LocalDate hasta);

    byte[] exportarCsv(Long propietarioId, LocalDate desde, LocalDate hasta);
}
