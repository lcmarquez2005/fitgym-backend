package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.model.response.ApiResponse;

public interface IFinanceReportService {
    ApiResponse obtenerDashboardReporte();
    ApiResponse obtenerEstadoResultados(int anio, int mes);
}
