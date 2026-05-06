package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.model.response.ApiResponse;
import java.math.BigDecimal;

public interface IFinanceService {
    ApiResponse abrirCaja(Integer usuarioId, BigDecimal saldoInicial);
    ApiResponse cerrarCaja(Integer usuarioId);
    ApiResponse registrarTransaccion(String tipo, String categoria, BigDecimal monto, String descripcion, Boolean requiereFactura);
    ApiResponse obtenerCajaAbierta();
}
