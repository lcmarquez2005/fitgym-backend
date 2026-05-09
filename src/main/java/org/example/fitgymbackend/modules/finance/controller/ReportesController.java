package org.example.fitgymbackend.modules.finance.controller;

import lombok.RequiredArgsConstructor;
import org.example.fitgymbackend.modules.finance.dto.EstadoResultadosDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteBalanceGeneralDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteFlujoEfectivoDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteOperacionesDiariasDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteCuentasCobrarDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteMembresiasDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteVentasCategoriaDTO;
import org.example.fitgymbackend.modules.finance.dto.ReportePredictivoDTO;
import org.example.fitgymbackend.modules.finance.service.IReportesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/finance/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final IReportesService reportesService;

    @GetMapping("/estado-resultados")
    public ResponseEntity<EstadoResultadosDTO> getEstadoResultados(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();

        return ResponseEntity.ok(reportesService.generarEstadoResultados(mes, anio));
    }

    @GetMapping("/balance-general")
    public ResponseEntity<ReporteBalanceGeneralDTO> getBalanceGeneral() {
        return ResponseEntity.ok(reportesService.generarBalanceGeneral());
    }

    @GetMapping("/flujo-efectivo")
    public ResponseEntity<ReporteFlujoEfectivoDTO> getFlujoEfectivo(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();

        return ResponseEntity.ok(reportesService.generarFlujoEfectivo(mes, anio));
    }

    // --- Endpoints de exportación ---

    @GetMapping("/estado-resultados/export/pdf")
    public ResponseEntity<byte[]> exportEstadoResultadosPdf(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();

        byte[] pdfBytes = reportesService.exportarEstadoResultadosPdf(mes, anio);
        return buildFileResponse(pdfBytes, "estado_resultados_" + mes + "_" + anio + ".pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/estado-resultados/export/xls")
    public ResponseEntity<byte[]> exportEstadoResultadosXls(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();

        byte[] xlsBytes = reportesService.exportarEstadoResultadosXls(mes, anio);
        return buildFileResponse(xlsBytes, "estado_resultados_" + mes + "_" + anio + ".xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/balance-general/export/pdf")
    public ResponseEntity<byte[]> exportBalanceGeneralPdf() {
        byte[] pdfBytes = reportesService.exportarBalanceGeneralPdf();
        return buildFileResponse(pdfBytes, "balance_general.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/balance-general/export/xls")
    public ResponseEntity<byte[]> exportBalanceGeneralXls() {
        byte[] xlsBytes = reportesService.exportarBalanceGeneralXls();
        return buildFileResponse(xlsBytes, "balance_general.xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/flujo-efectivo/export/pdf")
    public ResponseEntity<byte[]> exportFlujoEfectivoPdf(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();

        byte[] pdfBytes = reportesService.exportarFlujoEfectivoPdf(mes, anio);
        return buildFileResponse(pdfBytes, "flujo_efectivo_" + mes + "_" + anio + ".pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/flujo-efectivo/export/xls")
    public ResponseEntity<byte[]> exportFlujoEfectivoXls(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();

        byte[] xlsBytes = reportesService.exportarFlujoEfectivoXls(mes, anio);
        return buildFileResponse(xlsBytes, "flujo_efectivo_" + mes + "_" + anio + ".xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    // --- Bloques 2 y 3: Endpoints REST ---

    @GetMapping("/operaciones-diarias")
    public ResponseEntity<ReporteOperacionesDiariasDTO> getOperacionesDiarias() {
        return ResponseEntity.ok(reportesService.generarOperacionesDiarias());
    }

    @GetMapping("/cuentas-por-cobrar")
    public ResponseEntity<ReporteCuentasCobrarDTO> getCuentasPorCobrar() {
        return ResponseEntity.ok(reportesService.generarCuentasPorCobrar());
    }

    @GetMapping("/membresias")
    public ResponseEntity<ReporteMembresiasDTO> getAnalisisMembresias(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return ResponseEntity.ok(reportesService.generarAnalisisMembresias(mes, anio));
    }

    @GetMapping("/ventas-categoria")
    public ResponseEntity<ReporteVentasCategoriaDTO> getVentasPorCategoria(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return ResponseEntity.ok(reportesService.generarVentasPorCategoria(mes, anio));
    }

    @GetMapping("/kpis-predictivos")
    public ResponseEntity<ReportePredictivoDTO> getKPIsPredictivos(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return ResponseEntity.ok(reportesService.generarKPIsPredictivos(mes, anio));
    }

    // --- Bloques 2 y 3: Exportaciones ---

    @GetMapping("/operaciones-diarias/export/pdf")
    public ResponseEntity<byte[]> exportOperacionesDiariasPdf() {
        return buildFileResponse(reportesService.exportarOperacionesDiariasPdf(), "operaciones_diarias.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/operaciones-diarias/export/xls")
    public ResponseEntity<byte[]> exportOperacionesDiariasXls() {
        return buildFileResponse(reportesService.exportarOperacionesDiariasXls(), "operaciones_diarias.xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/cuentas-por-cobrar/export/pdf")
    public ResponseEntity<byte[]> exportCuentasPorCobrarPdf() {
        return buildFileResponse(reportesService.exportarCuentasPorCobrarPdf(), "cuentas_por_cobrar.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/cuentas-por-cobrar/export/xls")
    public ResponseEntity<byte[]> exportCuentasPorCobrarXls() {
        return buildFileResponse(reportesService.exportarCuentasPorCobrarXls(), "cuentas_por_cobrar.xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/membresias/export/pdf")
    public ResponseEntity<byte[]> exportAnalisisMembresiasPdf(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return buildFileResponse(reportesService.exportarAnalisisMembresiasPdf(mes, anio), "membresias_" + mes + "_" + anio + ".pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/membresias/export/xls")
    public ResponseEntity<byte[]> exportAnalisisMembresiasXls(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return buildFileResponse(reportesService.exportarAnalisisMembresiasXls(mes, anio), "membresias_" + mes + "_" + anio + ".xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/ventas-categoria/export/pdf")
    public ResponseEntity<byte[]> exportVentasPorCategoriaPdf(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return buildFileResponse(reportesService.exportarVentasPorCategoriaPdf(mes, anio), "ventas_categoria_" + mes + "_" + anio + ".pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/ventas-categoria/export/xls")
    public ResponseEntity<byte[]> exportVentasPorCategoriaXls(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return buildFileResponse(reportesService.exportarVentasPorCategoriaXls(mes, anio), "ventas_categoria_" + mes + "_" + anio + ".xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/kpis-predictivos/export/pdf")
    public ResponseEntity<byte[]> exportKPIsPredictivosPdf(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return buildFileResponse(reportesService.exportarKPIsPredictivosPdf(mes, anio), "kpis_" + mes + "_" + anio + ".pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/kpis-predictivos/export/xls")
    public ResponseEntity<byte[]> exportKPIsPredictivosXls(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();
        return buildFileResponse(reportesService.exportarKPIsPredictivosXls(mes, anio), "kpis_" + mes + "_" + anio + ".xlsx", MediaType.APPLICATION_OCTET_STREAM);
    }

    private ResponseEntity<byte[]> buildFileResponse(byte[] content, String filename, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
}
