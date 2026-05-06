package org.example.fitgymbackend.modules.finance.controller;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.dto.GenerarNominaRequest;
import org.example.fitgymbackend.modules.finance.service.INominaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/nomina")
public class NominaController {

    @Autowired
    private INominaService nominaService;

    @PostMapping("/generar")
    public ResponseEntity<ApiResponse> generarNominaQuincenal(@RequestBody GenerarNominaRequest request) {
        ApiResponse response = nominaService.generarNominaMasiva(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
