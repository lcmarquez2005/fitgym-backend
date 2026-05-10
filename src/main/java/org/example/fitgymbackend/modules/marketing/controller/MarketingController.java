// MarketingController.java
package org.example.fitgymbackend.modules.marketing.controller;

import lombok.RequiredArgsConstructor;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.marketing.dto.*;
import org.example.fitgymbackend.modules.marketing.service.IMarketingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marketing")
@RequiredArgsConstructor
public class MarketingController {

    private final IMarketingService marketingService;

    // ── Dashboard ──────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboard() {
        return ResponseEntity.ok(marketingService.obtenerDashboard());
    }

    // ── Leads ──────────────────────────────────────────────────────
    @GetMapping("/leads")
    public ResponseEntity<ApiResponse> getLeads() {
        return ResponseEntity.ok(marketingService.obtenerLeads());
    }

    @GetMapping("/leads/{id}")
    public ResponseEntity<ApiResponse> getLeadPorId(@PathVariable Long id) {
        ApiResponse res = marketingService.obtenerLeadPorId(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    @PostMapping("/leads")
    public ResponseEntity<ApiResponse> crearLead(@RequestBody LeadRequest request) {
        return ResponseEntity.ok(marketingService.crearLead(request));
    }

    @PatchMapping("/leads/{id}/etapa")
    public ResponseEntity<ApiResponse> actualizarEtapa(
            @PathVariable Long id,
            @RequestBody ActualizarEtapaRequest request) {
        ApiResponse res = marketingService.actualizarEtapaLead(id, request);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @PostMapping("/leads/{id}/convertir")
    public ResponseEntity<ApiResponse> convertirLead(@PathVariable Long id) {
        ApiResponse res = marketingService.convertirLeadASocio(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @DeleteMapping("/leads/{id}")
    public ResponseEntity<ApiResponse> eliminarLead(@PathVariable Long id) {
        ApiResponse res = marketingService.eliminarLead(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    @GetMapping("/leads/{id}/seguimientos")
    public ResponseEntity<ApiResponse> getSeguimientos(@PathVariable Long id) {
        return ResponseEntity.ok(marketingService.obtenerSeguimientosLead(id));
    }

    @PostMapping("/leads/{id}/seguimientos")
    public ResponseEntity<ApiResponse> agregarSeguimiento(
            @PathVariable Long id,
            @RequestBody SeguimientoLeadRequest request) {
        ApiResponse res = marketingService.agregarSeguimiento(id, request);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    // ── Campañas ───────────────────────────────────────────────────
    @GetMapping("/campanas")
    public ResponseEntity<ApiResponse> getCampanas() {
        return ResponseEntity.ok(marketingService.obtenerCampanas());
    }

    @GetMapping("/campanas/{id}")
    public ResponseEntity<ApiResponse> getCampanaPorId(@PathVariable Long id) {
        ApiResponse res = marketingService.obtenerCampanaPorId(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    @PostMapping("/campanas")
    public ResponseEntity<ApiResponse> crearCampana(@RequestBody CampanaRequest request) {
        return ResponseEntity.ok(marketingService.crearCampana(request));
    }

    @PostMapping("/campanas/{id}/enviar")
    public ResponseEntity<ApiResponse> enviarCampana(@PathVariable Long id) {
        ApiResponse res = marketingService.enviarCampana(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @DeleteMapping("/campanas/{id}")
    public ResponseEntity<ApiResponse> eliminarCampana(@PathVariable Long id) {
        ApiResponse res = marketingService.eliminarCampana(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    // ── Campañas automatizadas ─────────────────────────────────────
    @PostMapping("/automatizadas/bienvenida/{socioId}")
    public ResponseEntity<ApiResponse> campBienvenida(@PathVariable Long socioId) {
        return ResponseEntity.ok(marketingService.ejecutarCampanaBienvenida(socioId));
    }

    @PostMapping("/automatizadas/pre-vencimiento")
    public ResponseEntity<ApiResponse> campPreVencimiento() {
        return ResponseEntity.ok(marketingService.ejecutarCampanaPreVencimiento());
    }

    @PostMapping("/automatizadas/recuperacion")
    public ResponseEntity<ApiResponse> campRecuperacion() {
        return ResponseEntity.ok(marketingService.ejecutarCampanaRecuperacion());
    }

    @PostMapping("/automatizadas/cumpleanios")
    public ResponseEntity<ApiResponse> campCumpleanios() {
        return ResponseEntity.ok(marketingService.ejecutarCampanaCumpleanios());
    }

    // ── Promociones ────────────────────────────────────────────────
    @GetMapping("/promociones")
    public ResponseEntity<ApiResponse> getPromociones() {
        return ResponseEntity.ok(marketingService.obtenerPromociones());
    }

    @PostMapping("/promociones")
    public ResponseEntity<ApiResponse> crearPromocion(@RequestBody PromocionRequest request) {
        ApiResponse res = marketingService.crearPromocion(request);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @GetMapping("/promociones/validar/{codigo}")
    public ResponseEntity<ApiResponse> validarCodigo(@PathVariable String codigo) {
        ApiResponse res = marketingService.validarCodigo(codigo);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @PostMapping("/promociones/usar/{codigo}")
    public ResponseEntity<ApiResponse> usarCodigo(@PathVariable String codigo) {
        ApiResponse res = marketingService.usarCodigo(codigo);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @PatchMapping("/promociones/{id}/toggle")
    public ResponseEntity<ApiResponse> togglePromocion(@PathVariable Long id) {
        ApiResponse res = marketingService.togglePromocion(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/promociones/{id}")
    public ResponseEntity<ApiResponse> eliminarPromocion(@PathVariable Long id) {
        ApiResponse res = marketingService.eliminarPromocion(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    // ── Segmentos ──────────────────────────────────────────────────
    @GetMapping("/segmentos")
    public ResponseEntity<ApiResponse> getSegmentos() {
        return ResponseEntity.ok(marketingService.obtenerSegmentos());
    }

    @PostMapping("/segmentos")
    public ResponseEntity<ApiResponse> crearSegmento(@RequestBody SegmentoRequest request) {
        return ResponseEntity.ok(marketingService.crearSegmento(request));
    }

    @PostMapping("/segmentos/{id}/ejecutar")
    public ResponseEntity<ApiResponse> ejecutarSegmento(@PathVariable Long id) {
        ApiResponse res = marketingService.ejecutarSegmento(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }
}