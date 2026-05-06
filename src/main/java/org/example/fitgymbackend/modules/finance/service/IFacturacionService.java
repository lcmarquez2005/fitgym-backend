package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.modules.finance.entity.Factura;
import org.example.fitgymbackend.model.response.ApiResponse;

public interface IFacturacionService {
    ApiResponse timbrarFactura(Factura factura);
    ApiResponse cancelarFactura(String folioFiscal);
}
