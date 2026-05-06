package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.entity.Socio;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.dto.EstadoResultadosDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteDashboardDTO;
import org.example.fitgymbackend.modules.finance.entity.CorteCaja;
import org.example.fitgymbackend.modules.finance.entity.Transaccion;
import org.example.fitgymbackend.modules.finance.repository.CorteCajaRepository;
import org.example.fitgymbackend.modules.finance.repository.TransaccionRepository;
import org.example.fitgymbackend.repository.ISocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceReportServiceImpl implements IFinanceReportService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private ISocioRepository socioRepository;

    @Autowired
    private CorteCajaRepository corteCajaRepository;

    // ═══════════════════════════════════════════════════════════
    // DASHBOARD DIARIO ENRIQUECIDO
    // ═══════════════════════════════════════════════════════════

    @Override
    public ApiResponse obtenerDashboardReporte() {
        ReporteDashboardDTO dto = new ReporteDashboardDTO();

        LocalDate hoy = LocalDate.now();
        LocalDateTime hoyInicio = LocalDateTime.of(hoy, LocalTime.MIN);
        LocalDateTime hoyFin = LocalDateTime.of(hoy, LocalTime.MAX);

        // ── KPIs del día ──────────────────────────────────────
        BigDecimal ingresosHoy = sumarPorTipo("INGRESO", hoyInicio, hoyFin);
        BigDecimal egresosHoy  = sumarPorTipo("EGRESO",  hoyInicio, hoyFin);
        BigDecimal utilidadHoy = ingresosHoy.subtract(egresosHoy);

        dto.setIngresosHoy(ingresosHoy);
        dto.setEgresosHoy(egresosHoy);
        dto.setUtilidadHoy(utilidadHoy);

        // Caja abierta y saldo actual
        Optional<CorteCaja> cajaOpt = corteCajaRepository.findFirstByEstadoOrderByIdDesc("ABIERTA");
        if (cajaOpt.isPresent()) {
            CorteCaja caja = cajaOpt.get();
            BigDecimal cajaActual = caja.getSaldoInicial().add(ingresosHoy).subtract(egresosHoy);
            dto.setCajaActual(cajaActual);
            dto.setCajaAbierta(true);
        } else {
            dto.setCajaActual(BigDecimal.ZERO);
            dto.setCajaAbierta(false);
        }

        // ── Comparativa semanal ───────────────────────────────
        LocalDateTime semanaActualInicio = hoyInicio.minusDays(hoy.getDayOfWeek().getValue() - 1);
        LocalDateTime semanaPasadaInicio = semanaActualInicio.minusWeeks(1);
        LocalDateTime semanaPasadaFin    = semanaActualInicio.minusSeconds(1);

        BigDecimal semActual   = sumarPorTipo("INGRESO", semanaActualInicio, hoyFin);
        BigDecimal semPasada   = sumarPorTipo("INGRESO", semanaPasadaInicio, semanaPasadaFin);
        BigDecimal variacion   = semActual.subtract(semPasada);

        dto.setIngresosSemanaActual(semActual);
        dto.setIngresosSemanaPasada(semPasada);
        dto.setVariacionSemanal(variacion);

        // ── Hora pico (mes completo para tener datos suficientes) ─
        LocalDateTime inicioMes = LocalDateTime.of(hoy.withDayOfMonth(1), LocalTime.MIN);
        List<Transaccion> txMes = transaccionRepository.findByTipoAndFechaBetween("INGRESO", inicioMes, hoyFin);
        dto.setHoraPico(calcularHoraPico(txMes));

        // ── Socios ───────────────────────────────────────────
        List<Socio> todosSocios = socioRepository.findAll();
        LocalDate manana = hoy.plusDays(1);

        List<ReporteDashboardDTO.SocioInfoDTO> conDeuda     = new ArrayList<>();
        List<ReporteDashboardDTO.SocioInfoDTO> vencenManana = new ArrayList<>();
        Map<String, Integer> membresiasActivas = new HashMap<>();

        int vencenHoy   = 0;
        int deudaMas5   = 0;

        for (Socio s : todosSocios) {
            LocalDate fechaFin = s.getFechaFin();
            boolean isActivo = "ACTIVO".equalsIgnoreCase(s.getEstatus());

            if (isActivo && s.getTipoMembresia() != null) {
                membresiasActivas.merge(s.getTipoMembresia(), 1, Integer::sum);
            }

            if (fechaFin != null) {
                // Vencen hoy
                if (isActivo && fechaFin.isEqual(hoy)) vencenHoy++;

                // Vencen mañana
                if (isActivo && fechaFin.isEqual(manana)) vencenManana.add(mapSocioToInfo(s));

                // Deuda: fecha ya expiró
                if (fechaFin.isBefore(hoy)) {
                    long diasDeuda = ChronoUnit.DAYS.between(fechaFin, hoy);
                    ReporteDashboardDTO.SocioInfoDTO info = mapSocioToInfo(s);
                    info.setDiasDeuda(diasDeuda);
                    conDeuda.add(info);
                    if (diasDeuda > 5) deudaMas5++;
                }
            } else if (!isActivo) {
                conDeuda.add(mapSocioToInfo(s));
            }
        }

        dto.setSociosQueVencenHoy(vencenHoy);
        dto.setSociosConDeudaMas5Dias(deudaMas5);
        dto.setSociosConDeuda(conDeuda);
        dto.setProximosVencimientos(vencenManana);

        // Membresía favorita
        String favorita = membresiasActivas.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");
        dto.setMembresiaFavorita(favorita);

        // ── Últimas 5 transacciones del día (más recientes primero) ─
        List<Transaccion> txHoy = transaccionRepository.findByTipoAndFechaBetween("INGRESO", hoyInicio, hoyFin);
        List<Transaccion> egHoy = transaccionRepository.findByTipoAndFechaBetween("EGRESO", hoyInicio, hoyFin);

        List<Transaccion> todasHoy = new ArrayList<>();
        todasHoy.addAll(txHoy);
        todasHoy.addAll(egHoy);
        todasHoy.sort(Comparator.comparing(Transaccion::getFechaHora).reversed());

        List<ReporteDashboardDTO.TransaccionResumenDTO> ultimas = todasHoy.stream()
                .limit(5)
                .map(this::mapTransaccionToResumen)
                .collect(Collectors.toList());

        dto.setUltimasTransacciones(ultimas);

        return new ApiResponse("Dashboard financiero generado", true, dto);
    }

    // ═══════════════════════════════════════════════════════════
    // ESTADO DE RESULTADOS MENSUAL (P&L)
    // ═══════════════════════════════════════════════════════════

    @Override
    public ApiResponse obtenerEstadoResultados(int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin    = inicio.withDayOfMonth(inicio.lengthOfMonth());
        LocalDateTime dtInicio = LocalDateTime.of(inicio, LocalTime.MIN);
        LocalDateTime dtFin    = LocalDateTime.of(fin,    LocalTime.MAX);

        List<Transaccion> ingresos = transaccionRepository.findByTipoAndFechaBetween("INGRESO", dtInicio, dtFin);
        List<Transaccion> egresos  = transaccionRepository.findByTipoAndFechaBetween("EGRESO",  dtInicio, dtFin);

        EstadoResultadosDTO er = new EstadoResultadosDTO();
        er.setPeriodo(inicio.getMonth().getDisplayName(java.time.format.TextStyle.FULL,
                new java.util.Locale("es", "MX")) + " " + anio);
        er.setFechaInicio(inicio.toString());
        er.setFechaFin(fin.toString());
        er.setTotalTransacciones(ingresos.size() + egresos.size());

        // ── INGRESOS por categoría ────────────────────────────
        BigDecimal porMembresia  = sumarCategoria(ingresos, "MEMBRESIA");
        BigDecimal porSuplemento = sumarCategoria(ingresos, "SUPLEMENTO");
        BigDecimal porClases     = sumarCategoria(ingresos, "CLASE");
        BigDecimal otrosIngresos = ingresos.stream()
                .filter(t -> !categoriaEn(t, "MEMBRESIA", "SUPLEMENTO", "CLASE"))
                .map(Transaccion::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalIngresos = porMembresia.add(porSuplemento).add(porClases).add(otrosIngresos);

        er.setIngresosTotales(totalIngresos);
        er.setIngresosPorMembresias(porMembresia);
        er.setIngresosPorSuplementos(porSuplemento);
        er.setIngresosPorClases(porClases);
        er.setOtrosIngresos(otrosIngresos);

        // ── COSTOS (egresos por categoría) ───────────────────
        BigDecimal costosNomina    = sumarCategoria(egresos, "NOMINA");
        BigDecimal costosProductos = sumarCategoria(egresos, "INVENTARIO").add(sumarCategoria(egresos, "SUPLEMENTO"));
        BigDecimal gastosRenta     = sumarCategoria(egresos, "RENTA");
        BigDecimal gastosServicios = sumarCategoria(egresos, "SERVICIOS");
        BigDecimal gastosMantenimiento = sumarCategoria(egresos, "MANTENIMIENTO");
        BigDecimal otrosCostos     = egresos.stream()
                .filter(t -> !categoriaEn(t, "NOMINA", "INVENTARIO", "SUPLEMENTO",
                        "RENTA", "SERVICIOS", "MANTENIMIENTO"))
                .map(Transaccion::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal costosTotales = costosNomina.add(costosProductos).add(otrosCostos);
        BigDecimal gastosOperativos = gastosRenta.add(gastosServicios).add(gastosMantenimiento);

        er.setCostosTotales(costosTotales);
        er.setCostosNomina(costosNomina);
        er.setCostosProductos(costosProductos);
        er.setOtrosCostos(otrosCostos);
        er.setGastosOperativos(gastosOperativos);

        // ── UTILIDADES ───────────────────────────────────────
        BigDecimal utilidadBruta     = totalIngresos.subtract(costosTotales);
        BigDecimal utilidadOperativa = utilidadBruta.subtract(gastosOperativos);
        BigDecimal isr = utilidadOperativa.compareTo(BigDecimal.ZERO) > 0
                ? utilidadOperativa.multiply(new BigDecimal("0.30"))
                : BigDecimal.ZERO;
        BigDecimal utilidadNeta = utilidadOperativa.subtract(isr);

        er.setUtilidadBruta(utilidadBruta);
        er.setUtilidadOperativa(utilidadOperativa);
        er.setIsr(isr);
        er.setUtilidadNeta(utilidadNeta);

        // Margen neto (%)
        if (totalIngresos.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = utilidadNeta
                    .divide(totalIngresos, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            er.setMargenNeto(margen);
        } else {
            er.setMargenNeto(BigDecimal.ZERO);
        }

        return new ApiResponse("Estado de resultados generado", true, er);
    }

    // ═══════════════════════════════════════════════════════════
    // Utilidades privadas
    // ═══════════════════════════════════════════════════════════

    private BigDecimal sumarPorTipo(String tipo, LocalDateTime start, LocalDateTime end) {
        return transaccionRepository.findByTipoAndFechaBetween(tipo, start, end)
                .stream().map(Transaccion::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumarCategoria(List<Transaccion> txs, String categoria) {
        return txs.stream()
                .filter(t -> categoria.equalsIgnoreCase(t.getCategoria()))
                .map(Transaccion::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean categoriaEn(Transaccion t, String... cats) {
        for (String c : cats) if (c.equalsIgnoreCase(t.getCategoria())) return true;
        return false;
    }

    private String calcularHoraPico(List<Transaccion> transacciones) {
        if (transacciones.isEmpty()) return "N/A";
        Map<Integer, BigDecimal> sumaPorHora = new HashMap<>();
        for (Transaccion t : transacciones) {
            int h = t.getFechaHora().getHour();
            sumaPorHora.merge(h, t.getMonto(), BigDecimal::add);
        }
        int horaPico = sumaPorHora.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0);
        return String.format("%02d:00 - %02d:00", horaPico, horaPico + 1);
    }

    private ReporteDashboardDTO.SocioInfoDTO mapSocioToInfo(Socio s) {
        ReporteDashboardDTO.SocioInfoDTO info = new ReporteDashboardDTO.SocioInfoDTO();
        info.setId(s.getId());
        info.setNombreCompleto(s.getNombreCompleto());
        info.setTipoMembresia(s.getTipoMembresia());
        info.setFechaFin(s.getFechaFin() != null ? s.getFechaFin().toString() : "N/A");
        info.setDiasDeuda(0L);
        return info;
    }

    private ReporteDashboardDTO.TransaccionResumenDTO mapTransaccionToResumen(Transaccion t) {
        ReporteDashboardDTO.TransaccionResumenDTO r = new ReporteDashboardDTO.TransaccionResumenDTO();
        r.setHora(String.format("%02d:%02d", t.getFechaHora().getHour(), t.getFechaHora().getMinute()));
        r.setTipo(t.getTipo());
        r.setCategoria(t.getCategoria());
        r.setDescripcion(t.getDescripcion());
        r.setMonto(t.getMonto());
        return r;
    }
}
