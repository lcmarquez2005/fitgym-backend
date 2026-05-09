package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.modules.finance.dto.EstadoResultadosDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteBalanceGeneralDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteFlujoEfectivoDTO;

public interface IReportesService {
    EstadoResultadosDTO generarEstadoResultados(int mes, int anio);
    ReporteBalanceGeneralDTO generarBalanceGeneral();
    ReporteFlujoEfectivoDTO generarFlujoEfectivo(int mes, int anio);
    
    // Métodos para futuras exportaciones (Retornan array de bytes del archivo)
    byte[] exportarEstadoResultadosPdf(int mes, int anio);
    byte[] exportarEstadoResultadosXls(int mes, int anio);
    
    byte[] exportarBalanceGeneralPdf();
    byte[] exportarBalanceGeneralXls();
    
    byte[] exportarFlujoEfectivoPdf(int mes, int anio);
    byte[] exportarFlujoEfectivoXls(int mes, int anio);

    // --- Bloque 2 y 3 ---
    org.example.fitgymbackend.modules.finance.dto.ReporteOperacionesDiariasDTO generarOperacionesDiarias();
    org.example.fitgymbackend.modules.finance.dto.ReporteCuentasCobrarDTO generarCuentasPorCobrar();
    org.example.fitgymbackend.modules.finance.dto.ReporteMembresiasDTO generarAnalisisMembresias(int mes, int anio);
    org.example.fitgymbackend.modules.finance.dto.ReporteVentasCategoriaDTO generarVentasPorCategoria(int mes, int anio);
    org.example.fitgymbackend.modules.finance.dto.ReportePredictivoDTO generarKPIsPredictivos(int mes, int anio);

    byte[] exportarOperacionesDiariasPdf();
    byte[] exportarOperacionesDiariasXls();
    byte[] exportarCuentasPorCobrarPdf();
    byte[] exportarCuentasPorCobrarXls();
    byte[] exportarAnalisisMembresiasPdf(int mes, int anio);
    byte[] exportarAnalisisMembresiasXls(int mes, int anio);
    byte[] exportarVentasPorCategoriaPdf(int mes, int anio);
    byte[] exportarVentasPorCategoriaXls(int mes, int anio);
    byte[] exportarKPIsPredictivosPdf(int mes, int anio);
    byte[] exportarKPIsPredictivosXls(int mes, int anio);
}
