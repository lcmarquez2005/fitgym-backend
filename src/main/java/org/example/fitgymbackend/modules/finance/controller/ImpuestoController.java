package org.example.fitgymbackend.modules.finance.controller;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.dto.*;
import org.example.fitgymbackend.modules.finance.service.IImpuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/impuestos")
public class ImpuestoController {

    @Autowired
    private IImpuestoService impuestoService;

    // — Períodos Fiscales —
    @PostMapping("/periodos")
    public ResponseEntity<ApiResponse> crearPeriodo(@RequestBody PeriodoFiscalRequest req) {
        ApiResponse r = impuestoService.crearPeriodoFiscal(req);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    @GetMapping("/periodos")
    public ResponseEntity<ApiResponse> listarPeriodos(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(impuestoService.listarPeriodos(estado));
    }

    @PatchMapping("/periodos/{id}/presentada")
    public ResponseEntity<ApiResponse> marcarPresentada(@PathVariable Long id) {
        ApiResponse r = impuestoService.marcarDeclaracionPresentada(id);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    // — IVA —
    @PostMapping("/iva/calcular")
    public ResponseEntity<ApiResponse> calcularIVA(@RequestBody RegistroIVARequest req) {
        ApiResponse r = impuestoService.calcularIVADelPeriodo(req);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    // — ISR Retenciones —
    @PostMapping("/isr/retenciones")
    public ResponseEntity<ApiResponse> registrarRetencion(@RequestBody RetencionISRRequest req) {
        ApiResponse r = impuestoService.registrarRetencionISR(req);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    // — DIOT —
    @PostMapping("/diot")
    public ResponseEntity<ApiResponse> registrarDIOT(@RequestBody RegistroDIOTRequest req) {
        ApiResponse r = impuestoService.registrarProveedorDIOT(req);
        return r.isSuccess() ? ResponseEntity.ok(r) : ResponseEntity.badRequest().body(r);
    }

    @GetMapping("/diot")
    public ResponseEntity<ApiResponse> listarDIOT(@RequestParam String mes) {
        return ResponseEntity.ok(impuestoService.listarDIOTPorMes(mes));
    }
}
