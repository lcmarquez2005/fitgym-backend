package org.example.fitgymbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.fitgymbackend.model.request.SocioRequest;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.SocioResponse;
import org.example.fitgymbackend.service.ISocioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/socios")
@RequiredArgsConstructor
public class SocioController {

    private final ISocioService socioService;

    @PostMapping
    public ResponseEntity<ApiResponse> registrar(@RequestBody SocioRequest request) {
        SocioResponse response = socioService.registrar(request);
        return ResponseEntity.ok(new ApiResponse("Socio registrado exitosamente", true, response));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse> buscar(@RequestParam String q) {
        List<SocioResponse> resultados = socioService.buscar(q);
        return ResponseEntity.ok(new ApiResponse("OK", true, resultados));
    }

    // PUT /socios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id, @RequestBody SocioRequest request) {
        SocioResponse response = socioService.actualizar(id, request);
        return ResponseEntity.ok(new ApiResponse("Socio actualizado exitosamente", true, response));
    }

    // DELETE /socios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        socioService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("Socio eliminado exitosamente", true, null));
    }
}