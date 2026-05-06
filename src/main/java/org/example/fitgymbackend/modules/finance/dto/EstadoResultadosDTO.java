package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EstadoResultadosDTO {

    private String periodo;          // "Mayo 2026"
    private String fechaInicio;
    private String fechaFin;

    // ── (+) INGRESOS ──────────────────────────────────────────
    private BigDecimal ingresosTotales;
    private BigDecimal ingresosPorMembresias;
    private BigDecimal ingresosPorSuplementos;
    private BigDecimal ingresosPorClases;
    private BigDecimal otrosIngresos;

    // ── (-) COSTOS DIRECTOS ───────────────────────────────────
    private BigDecimal costosTotales;   // suma de todos los egresos por categoría
    private BigDecimal costosNomina;    // egresos cat. NOMINA
    private BigDecimal costosProductos; // egresos cat. INVENTARIO / SUPLEMENTO
    private BigDecimal otrosCostos;     // resto de egresos

    // ── (=) UTILIDADES ───────────────────────────────────────
    private BigDecimal utilidadBruta;       // ingresosTotales - costosTotales
    private BigDecimal gastosOperativos;    // egresos cat. MANTENIMIENTO, RENTA, SERVICIOS
    private BigDecimal utilidadOperativa;   // utilidadBruta - gastosOperativos
    private BigDecimal isr;                 // 30% sobre utilidadOperativa (si > 0)
    private BigDecimal utilidadNeta;        // utilidadOperativa - isr

    // ── Indicadores ──────────────────────────────────────────
    private BigDecimal margenNeto;          // (utilidadNeta / ingresosTotales) * 100
    private Integer totalTransacciones;
}
