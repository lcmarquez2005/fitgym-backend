package org.example.fitgymbackend.modules.reservas.controller;

import org.example.fitgymbackend.modules.reservas.entity.CatalogoClase;
import org.example.fitgymbackend.modules.reservas.entity.ClaseProgramada;
import org.example.fitgymbackend.modules.reservas.entity.Reserva;
import org.example.fitgymbackend.modules.reservas.entity.Salon;
import org.example.fitgymbackend.modules.reservas.service.IReservasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservasController {

    @Autowired
    private IReservasService reservasService;

    // ==========================================
    // SALONES Y CATÁLOGOS
    // ==========================================

    @GetMapping("/salones")
    public ResponseEntity<List<Salon>> obtenerSalones() {
        return ResponseEntity.ok(reservasService.obtenerSalonesActivos());
    }

    @PostMapping("/salones")
    public ResponseEntity<Salon> guardarSalon(@RequestBody Salon salon) {
        return ResponseEntity.ok(reservasService.guardarSalon(salon));
    }

    @GetMapping("/catalogo")
    public ResponseEntity<List<CatalogoClase>> obtenerCatalogo() {
        return ResponseEntity.ok(reservasService.obtenerCatalogoActivo());
    }

    @PostMapping("/catalogo")
    public ResponseEntity<CatalogoClase> guardarCatalogo(@RequestBody CatalogoClase catalogo) {
        return ResponseEntity.ok(reservasService.guardarCatalogoClase(catalogo));
    }

    @DeleteMapping("/salones/{id}")
    public ResponseEntity<?> eliminarSalon(@PathVariable Long id) {
        try {
            reservasService.eliminarSalon(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Salón eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/catalogo/{id}")
    public ResponseEntity<?> eliminarCatalogo(@PathVariable Long id) {
        try {
            reservasService.eliminarCatalogoClase(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Clase eliminada del catálogo exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ==========================================
    // PROGRAMACIÓN (HORARIOS)
    // ==========================================

    @GetMapping("/clases")
    public ResponseEntity<List<ClaseProgramada>> obtenerHorario(
            @RequestParam String inicio,
            @RequestParam String fin) {
        LocalDate start = LocalDate.parse(inicio);
        LocalDate end = LocalDate.parse(fin);
        return ResponseEntity.ok(reservasService.obtenerHorario(start, end));
    }

    @PostMapping("/clases")
    public ResponseEntity<?> programarClase(@RequestBody ClaseProgramada clase) {
        try {
            return ResponseEntity.ok(reservasService.programarClase(clase));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/clases/{id}/cancelar")
    public ResponseEntity<?> cancelarClase(@PathVariable Long id) {
        try {
            reservasService.cancelarClaseProgramada(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Clase y sus reservas canceladas exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ==========================================
    // OPERATIVA DE RESERVAS (SOCIOS)
    // ==========================================

    @GetMapping("/clases/{id}/reservas")
    public ResponseEntity<List<Reserva>> obtenerReservasClase(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.obtenerReservasPorClase(id));
    }

    @GetMapping("/socio/{socioId}")
    public ResponseEntity<List<Reserva>> obtenerReservasSocio(@PathVariable Long socioId) {
        return ResponseEntity.ok(reservasService.obtenerReservasPorSocio(socioId));
    }

    @PostMapping("/clases/{claseId}/reservar")
    public ResponseEntity<?> reservarClase(@PathVariable Long claseId, @RequestBody Map<String, Long> payload) {
        try {
            Long socioId = payload.get("socioId");
            Reserva reserva = reservasService.reservarClase(claseId, socioId);
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/reservas/{reservaId}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long reservaId) {
        try {
            return ResponseEntity.ok(reservasService.cancelarReserva(reservaId));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/reservas/{reservaId}/checkin")
    public ResponseEntity<?> registrarAsistencia(@PathVariable Long reservaId, @RequestBody Map<String, Boolean> payload) {
        try {
            Boolean asistio = payload.getOrDefault("asistio", true);
            return ResponseEntity.ok(reservasService.registrarAsistencia(reservaId, asistio));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
