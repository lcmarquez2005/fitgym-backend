package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.dto.*;

public interface IImpuestoService {
    ApiResponse crearPeriodoFiscal(PeriodoFiscalRequest request);
    ApiResponse listarPeriodos(String estado);
    ApiResponse calcularIVADelPeriodo(RegistroIVARequest request);
    ApiResponse registrarRetencionISR(RetencionISRRequest request);
    ApiResponse registrarProveedorDIOT(RegistroDIOTRequest request);
    ApiResponse listarDIOTPorMes(String mes);
    ApiResponse marcarDeclaracionPresentada(Long periodoId);
}
