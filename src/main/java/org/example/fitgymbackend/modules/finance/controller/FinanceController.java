package org.example.fitgymbackend.modules.finance.controller;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.dto.CajaAperturaRequest;
import org.example.fitgymbackend.modules.finance.dto.TransaccionRequest;
import org.example.fitgymbackend.modules.finance.service.IFinanceService;
import org.example.fitgymbackend.security.JwtUtil;
import org.example.fitgymbackend.modules.finance.service.IFinanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    @Autowired
    private IFinanceService financeService;

    @Autowired
    private IFinanceReportService financeReportService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/reportes/dashboard")
    public ResponseEntity<ApiResponse> obtenerReporteDashboard() {
        ApiResponse response = financeReportService.obtenerDashboardReporte();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reportes/estado-resultados")
    public ResponseEntity<ApiResponse> obtenerEstadoResultados(
            @RequestParam int anio,
            @RequestParam int mes) {
        ApiResponse response = financeReportService.obtenerEstadoResultados(anio, mes);
        return ResponseEntity.ok(response);
    }

    // Métodos auxiliares para obtener ID de usuario
    private Integer getUserIdFromToken(String authHeader) {
        // Por simplificar, si usamos email para obtener id. 
        // Aqui simularemos retornar 1 o extraer el ID real del token.
        return 1; // TO-DO: Extraer correctamente del token
    }

    @PostMapping("/caja/abrir")
    public ResponseEntity<ApiResponse> abrirCaja(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CajaAperturaRequest request) {
        
        Integer usuarioId = getUserIdFromToken(authHeader);
        ApiResponse response = financeService.abrirCaja(usuarioId, request.getSaldoInicial());
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/caja/cerrar")
    public ResponseEntity<ApiResponse> cerrarCaja(
            @RequestHeader("Authorization") String authHeader) {
        
        Integer usuarioId = getUserIdFromToken(authHeader);
        ApiResponse response = financeService.cerrarCaja(usuarioId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/caja/actual")
    public ResponseEntity<ApiResponse> obtenerCajaAbierta() {
        ApiResponse response = financeService.obtenerCajaAbierta();
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(response); // Regresamos 200 pero success false si no hay
        }
    }

    @PostMapping("/transaccion")
    public ResponseEntity<ApiResponse> registrarTransaccion(
            @RequestBody TransaccionRequest request) {
        
        ApiResponse response = financeService.registrarTransaccion(
                request.getTipo(),
                request.getCategoria(),
                request.getMonto(),
                request.getDescripcion(),
                request.getRequiereFactura()
        );
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
