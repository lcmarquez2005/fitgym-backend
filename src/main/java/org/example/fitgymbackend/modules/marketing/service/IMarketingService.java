//IMarketingService.java
package org.example.fitgymbackend.modules.marketing.service;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.marketing.dto.*;

public interface IMarketingService {

    // ── Leads ──────────────────────────────────────────────────────
    ApiResponse crearLead(LeadRequest request);

    ApiResponse obtenerLeads();

    ApiResponse obtenerLeadPorId(Long id);

    ApiResponse actualizarEtapaLead(Long id, ActualizarEtapaRequest request);

    ApiResponse convertirLeadASocio(Long id);

    ApiResponse eliminarLead(Long id);

    ApiResponse agregarSeguimiento(Long leadId, SeguimientoLeadRequest request);

    ApiResponse obtenerSeguimientosLead(Long leadId);

    // ── Campañas ───────────────────────────────────────────────────
    ApiResponse crearCampana(CampanaRequest request);

    ApiResponse obtenerCampanas();

    ApiResponse obtenerCampanaPorId(Long id);

    ApiResponse enviarCampana(Long id);

    ApiResponse eliminarCampana(Long id);

    // ── Campañas automatizadas ─────────────────────────────────────
    ApiResponse ejecutarCampanaBienvenida(Long socioId);

    ApiResponse ejecutarCampanaPreVencimiento();

    ApiResponse ejecutarCampanaRecuperacion();

    ApiResponse ejecutarCampanaCumpleanios();

    // ── Promociones ────────────────────────────────────────────────
    ApiResponse crearPromocion(PromocionRequest request);

    ApiResponse obtenerPromociones();

    ApiResponse validarCodigo(String codigo);

    ApiResponse usarCodigo(String codigo);

    ApiResponse togglePromocion(Long id);

    ApiResponse eliminarPromocion(Long id);

    // ── Segmentos ──────────────────────────────────────────────────
    ApiResponse crearSegmento(SegmentoRequest request);

    ApiResponse obtenerSegmentos();

    ApiResponse ejecutarSegmento(Long id);

    // ── Dashboard ──────────────────────────────────────────────────
    ApiResponse obtenerDashboard();
}