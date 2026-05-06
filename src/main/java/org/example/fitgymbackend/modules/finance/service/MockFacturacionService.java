package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.modules.finance.entity.Factura;
import org.example.fitgymbackend.modules.finance.repository.FacturaRepository;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockFacturacionService implements IFacturacionService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Override
    public ApiResponse timbrarFactura(Factura factura) {
        // Simulamos retardo de red de API externa
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simular respuesta exitosa de un PAC
        factura.setStatus("TIMBRADA");
        factura.setFolioFiscal(UUID.randomUUID().toString());
        facturaRepository.save(factura);

        return new ApiResponse("Factura timbrada exitosamente (Mock)", true, factura.getFolioFiscal());
    }

    @Override
    public ApiResponse cancelarFactura(String folioFiscal) {
        return new ApiResponse("Factura cancelada exitosamente (Mock)", true, null);
    }
}
