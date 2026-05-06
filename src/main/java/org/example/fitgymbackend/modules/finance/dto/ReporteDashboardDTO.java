package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReporteDashboardDTO {

    // ── KPIs del día ──────────────────────────────────────────
    private BigDecimal ingresosHoy;
    private BigDecimal egresosHoy;
    private BigDecimal utilidadHoy;
    private BigDecimal cajaActual;          // saldoInicial + ingresos - egresos del día
    private Boolean cajaAbierta;

    // ── Comparativas semanales ────────────────────────────────
    private BigDecimal ingresosSemanaActual;
    private BigDecimal ingresosSemanaPasada;
    private BigDecimal variacionSemanal;    // diferencia absoluta vs semana anterior

    // ── Alertas del día ──────────────────────────────────────
    private Integer sociosQueVencenHoy;
    private Integer sociosConDeudaMas5Dias;

    // ── Socios ───────────────────────────────────────────────
    private List<SocioInfoDTO> sociosConDeuda;
    private List<SocioInfoDTO> proximosVencimientos;   // vencen mañana

    // ── Estadísticas del mes ──────────────────────────────────
    private String membresiaFavorita;
    private String horaPico;

    // ── Últimas 5 transacciones del día ──────────────────────
    private List<TransaccionResumenDTO> ultimasTransacciones;

    // ─────────────────────────────────────────────────────────
    // Inner DTOs
    // ─────────────────────────────────────────────────────────

    @Data
    public static class SocioInfoDTO {
        private Long id;
        private String nombreCompleto;
        private String tipoMembresia;
        private String fechaFin;
        private Long diasDeuda;
    }

    @Data
    public static class TransaccionResumenDTO {
        private String hora;
        private String tipo;
        private String categoria;
        private String descripcion;
        private BigDecimal monto;
    }
}
