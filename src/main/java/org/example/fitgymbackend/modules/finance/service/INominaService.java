package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.dto.GenerarNominaRequest;

public interface INominaService {
    ApiResponse generarNominaMasiva(GenerarNominaRequest request);
}
