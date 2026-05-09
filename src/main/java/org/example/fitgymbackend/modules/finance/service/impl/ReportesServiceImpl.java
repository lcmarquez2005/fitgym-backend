package org.example.fitgymbackend.modules.finance.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.fitgymbackend.modules.finance.dto.EstadoResultadosDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteBalanceGeneralDTO;
import org.example.fitgymbackend.modules.finance.dto.ReporteFlujoEfectivoDTO;
import org.example.fitgymbackend.modules.finance.entity.Transaccion;
import org.example.fitgymbackend.modules.finance.repository.TransaccionRepository;
import org.example.fitgymbackend.modules.finance.service.IReportesService;
import org.example.fitgymbackend.modules.inventario.entity.Suplemento;
import org.example.fitgymbackend.modules.inventario.repository.SuplementoRepository;
import org.example.fitgymbackend.repository.ISocioRepository;
import org.example.fitgymbackend.entity.Socio;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ReportesServiceImpl implements IReportesService {

    private final TransaccionRepository transaccionRepository;
    private final SuplementoRepository suplementoRepository;
    private final ISocioRepository socioRepository;

    @Override
    public EstadoResultadosDTO generarEstadoResultados(int mes, int anio) {
        LocalDateTime startDate = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);

        List<Transaccion> ingresosList = transaccionRepository.findByTipoAndFechaBetween("INGRESO", startDate, endDate);
        List<Transaccion> egresosList = transaccionRepository.findByTipoAndFechaBetween("EGRESO", startDate, endDate);

        BigDecimal ingresosPorMembresias = BigDecimal.ZERO;
        BigDecimal ingresosPorSuplementos = BigDecimal.ZERO;
        BigDecimal ingresosPorClases = BigDecimal.ZERO;
        BigDecimal otrosIngresos = BigDecimal.ZERO;

        for (Transaccion t : ingresosList) {
            BigDecimal monto = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
            if ("MEMBRESIA".equalsIgnoreCase(t.getCategoria())) ingresosPorMembresias = ingresosPorMembresias.add(monto);
            else if ("SUPLEMENTO".equalsIgnoreCase(t.getCategoria())) ingresosPorSuplementos = ingresosPorSuplementos.add(monto);
            else if ("CLASE".equalsIgnoreCase(t.getCategoria())) ingresosPorClases = ingresosPorClases.add(monto);
            else otrosIngresos = otrosIngresos.add(monto);
        }

        BigDecimal ingresosTotales = ingresosPorMembresias.add(ingresosPorSuplementos).add(ingresosPorClases).add(otrosIngresos);

        BigDecimal costosNomina = BigDecimal.ZERO;
        BigDecimal costosProductos = BigDecimal.ZERO;
        BigDecimal gastosOperativos = BigDecimal.ZERO;
        BigDecimal otrosCostos = BigDecimal.ZERO;

        for (Transaccion t : egresosList) {
            BigDecimal monto = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
            if ("NOMINA".equalsIgnoreCase(t.getCategoria())) costosNomina = costosNomina.add(monto);
            else if ("SUPLEMENTO".equalsIgnoreCase(t.getCategoria()) || "INVENTARIO".equalsIgnoreCase(t.getCategoria())) costosProductos = costosProductos.add(monto);
            else if ("MANTENIMIENTO".equalsIgnoreCase(t.getCategoria()) || "SERVICIOS".equalsIgnoreCase(t.getCategoria()) || "RENTA".equalsIgnoreCase(t.getCategoria())) gastosOperativos = gastosOperativos.add(monto);
            else otrosCostos = otrosCostos.add(monto);
        }

        BigDecimal costosTotales = costosNomina.add(costosProductos).add(otrosCostos).add(gastosOperativos);
        BigDecimal utilidadBruta = ingresosTotales.subtract(costosNomina).subtract(costosProductos).subtract(otrosCostos);
        BigDecimal utilidadOperativa = utilidadBruta.subtract(gastosOperativos);
        
        BigDecimal isr = BigDecimal.ZERO;
        if (utilidadOperativa.compareTo(BigDecimal.ZERO) > 0) {
            isr = utilidadOperativa.multiply(new BigDecimal("0.30")); // 30% ISR
        }
        BigDecimal utilidadNeta = utilidadOperativa.subtract(isr);

        EstadoResultadosDTO dto = new EstadoResultadosDTO();
        dto.setPeriodo(mes + "/" + anio);
        dto.setFechaInicio(startDate.toString());
        dto.setFechaFin(endDate.minusDays(1).toString());
        
        dto.setIngresosTotales(ingresosTotales);
        dto.setIngresosPorMembresias(ingresosPorMembresias);
        dto.setIngresosPorSuplementos(ingresosPorSuplementos);
        dto.setIngresosPorClases(ingresosPorClases);
        dto.setOtrosIngresos(otrosIngresos);

        dto.setCostosTotales(costosTotales);
        dto.setCostosNomina(costosNomina);
        dto.setCostosProductos(costosProductos);
        dto.setOtrosCostos(otrosCostos);

        dto.setUtilidadBruta(utilidadBruta);
        dto.setGastosOperativos(gastosOperativos);
        dto.setUtilidadOperativa(utilidadOperativa);
        dto.setIsr(isr);
        dto.setUtilidadNeta(utilidadNeta);

        if (ingresosTotales.compareTo(BigDecimal.ZERO) > 0) {
            dto.setMargenNeto(utilidadNeta.divide(ingresosTotales, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
        } else {
            dto.setMargenNeto(BigDecimal.ZERO);
        }
        
        dto.setTotalTransacciones(ingresosList.size() + egresosList.size());

        return dto;
    }

    @Override
    public ReporteBalanceGeneralDTO generarBalanceGeneral() {
        ReporteBalanceGeneralDTO dto = new ReporteBalanceGeneralDTO();
        dto.setPeriodo("Actual");
        dto.setFechaGeneracion(LocalDate.now().format(DateTimeFormatter.ISO_DATE));

        // 1. Activo Circulante Real
        // 1.1 Caja Total (Ingresos - Egresos históricos)
        List<Transaccion> allIngresos = transaccionRepository.findByTipoAndFechaBetween("INGRESO", LocalDateTime.MIN, LocalDateTime.now());
        List<Transaccion> allEgresos = transaccionRepository.findByTipoAndFechaBetween("EGRESO", LocalDateTime.MIN, LocalDateTime.now());
        
        BigDecimal totalIngresos = allIngresos.stream()
                .map(t -> t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEgresos = allEgresos.stream()
                .map(t -> t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal cajaActual = totalIngresos.subtract(totalEgresos);

        // 1.2 Inventario Actual
        List<Suplemento> suplementos = suplementoRepository.findByActivoTrue();
        BigDecimal inventarioValor = suplementos.stream()
                .map(s -> {
                    BigDecimal precio = s.getPrecioCompra() != null ? s.getPrecioCompra() : BigDecimal.ZERO;
                    return precio.multiply(new BigDecimal(s.getStock() != null ? s.getStock() : 0));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal circulanteTotal = cajaActual.add(inventarioValor);

        ReporteBalanceGeneralDTO.Activos activos = new ReporteBalanceGeneralDTO.Activos();
        activos.setCirculanteCaja(cajaActual);
        activos.setCirculanteInventario(inventarioValor);
        activos.setCirculanteTotal(circulanteTotal);

        // Mocks para Activos Fijos
        BigDecimal mockMaquinaria = new BigDecimal("250000.00");
        BigDecimal mockMobiliario = new BigDecimal("50000.00");
        BigDecimal fijoTotal = mockMaquinaria.add(mockMobiliario);
        
        activos.setFijoMaquinaria(mockMaquinaria);
        activos.setFijoMobiliario(mockMobiliario);
        activos.setFijoTotal(fijoTotal);
        activos.setTotal(circulanteTotal.add(fijoTotal));
        dto.setActivos(activos);

        // Mocks para Pasivos
        ReporteBalanceGeneralDTO.Pasivos pasivos = new ReporteBalanceGeneralDTO.Pasivos();
        BigDecimal mockProveedores = new BigDecimal("15000.00");
        BigDecimal mockPrestamos = new BigDecimal("100000.00");
        pasivos.setCortoPlazoProveedores(mockProveedores);
        pasivos.setLargoPlazoPrestamos(mockPrestamos);
        pasivos.setTotal(mockProveedores.add(mockPrestamos));
        dto.setPasivos(pasivos);

        // Mocks para Capital Contable
        ReporteBalanceGeneralDTO.CapitalContable capital = new ReporteBalanceGeneralDTO.CapitalContable();
        // Capital = Activo - Pasivo
        BigDecimal capitalTotal = activos.getTotal().subtract(pasivos.getTotal());
        BigDecimal mockCapitalSocial = new BigDecimal("150000.00");
        BigDecimal utilidadesRetenidas = capitalTotal.subtract(mockCapitalSocial);
        
        capital.setCapitalSocial(mockCapitalSocial);
        capital.setUtilidadesRetenidas(utilidadesRetenidas);
        capital.setTotal(capitalTotal);
        dto.setCapitalContable(capital);

        return dto;
    }

    @Override
    public ReporteFlujoEfectivoDTO generarFlujoEfectivo(int mes, int anio) {
        LocalDateTime startDate = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);

        // Saldo inicial (histórico antes de este mes)
        List<Transaccion> historicoIngresos = transaccionRepository.findByTipoAndFechaBetween("INGRESO", LocalDateTime.MIN, startDate);
        List<Transaccion> historicoEgresos = transaccionRepository.findByTipoAndFechaBetween("EGRESO", LocalDateTime.MIN, startDate);
        
        BigDecimal saldoInicialIngresos = historicoIngresos.stream().map(t -> t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoInicialEgresos = historicoEgresos.stream().map(t -> t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoInicial = saldoInicialIngresos.subtract(saldoInicialEgresos);

        List<Transaccion> ingresosList = transaccionRepository.findByTipoAndFechaBetween("INGRESO", startDate, endDate);
        List<Transaccion> egresosList = transaccionRepository.findByTipoAndFechaBetween("EGRESO", startDate, endDate);

        BigDecimal totalEntradas = BigDecimal.ZERO;
        Map<String, BigDecimal> desgloseEntradas = new HashMap<>();
        for (Transaccion t : ingresosList) {
            BigDecimal monto = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
            totalEntradas = totalEntradas.add(monto);
            String cat = t.getCategoria() != null ? t.getCategoria() : "OTROS";
            desgloseEntradas.put(cat, desgloseEntradas.getOrDefault(cat, BigDecimal.ZERO).add(monto));
        }

        BigDecimal totalSalidas = BigDecimal.ZERO;
        Map<String, BigDecimal> desgloseSalidas = new HashMap<>();
        for (Transaccion t : egresosList) {
            BigDecimal monto = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
            totalSalidas = totalSalidas.add(monto);
            String cat = t.getCategoria() != null ? t.getCategoria() : "OTROS";
            desgloseSalidas.put(cat, desgloseSalidas.getOrDefault(cat, BigDecimal.ZERO).add(monto));
        }

        BigDecimal flujoNeto = totalEntradas.subtract(totalSalidas);
        BigDecimal saldoFinal = saldoInicial.add(flujoNeto);

        ReporteFlujoEfectivoDTO dto = new ReporteFlujoEfectivoDTO();
        dto.setPeriodo(mes + "/" + anio);
        dto.setFechaInicio(startDate.toString());
        dto.setFechaFin(endDate.minusDays(1).toString());
        dto.setSaldoInicial(saldoInicial);
        dto.setTotalEntradas(totalEntradas);
        dto.setDesgloseEntradas(desgloseEntradas);
        dto.setTotalSalidas(totalSalidas);
        dto.setDesgloseSalidas(desgloseSalidas);
        dto.setFlujoNeto(flujoNeto);
        dto.setSaldoFinal(saldoFinal);

        return dto;
    }

    @Override
    public org.example.fitgymbackend.modules.finance.dto.ReporteOperacionesDiariasDTO generarOperacionesDiarias() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        // Simular caja inicial de hoy (histórico hasta ayer)
        List<Transaccion> historicoIngresos = transaccionRepository.findByTipoAndFechaBetween("INGRESO", LocalDateTime.MIN, startOfDay);
        List<Transaccion> historicoEgresos = transaccionRepository.findByTipoAndFechaBetween("EGRESO", LocalDateTime.MIN, startOfDay);
        BigDecimal cajaInicial = historicoIngresos.stream().map(t -> t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(historicoEgresos.stream().map(t -> t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add));

        List<Transaccion> ingresosHoy = transaccionRepository.findByTipoAndFechaBetween("INGRESO", startOfDay, endOfDay);
        List<Transaccion> egresosHoy = transaccionRepository.findByTipoAndFechaBetween("EGRESO", startOfDay, endOfDay);

        BigDecimal totalIngresos = BigDecimal.ZERO;
        Map<String, BigDecimal> ingresosPorCategoria = new HashMap<>();
        for (Transaccion t : ingresosHoy) {
            BigDecimal m = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
            totalIngresos = totalIngresos.add(m);
            String cat = t.getCategoria() != null ? t.getCategoria() : "OTROS";
            ingresosPorCategoria.put(cat, ingresosPorCategoria.getOrDefault(cat, BigDecimal.ZERO).add(m));
        }

        BigDecimal totalEgresos = BigDecimal.ZERO;
        Map<String, BigDecimal> egresosPorCategoria = new HashMap<>();
        for (Transaccion t : egresosHoy) {
            BigDecimal m = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
            totalEgresos = totalEgresos.add(m);
            String cat = t.getCategoria() != null ? t.getCategoria() : "OTROS";
            egresosPorCategoria.put(cat, egresosPorCategoria.getOrDefault(cat, BigDecimal.ZERO).add(m));
        }

        org.example.fitgymbackend.modules.finance.dto.ReporteOperacionesDiariasDTO dto = new org.example.fitgymbackend.modules.finance.dto.ReporteOperacionesDiariasDTO();
        dto.setFecha(LocalDate.now().toString());
        dto.setCajaInicial(cajaInicial);
        dto.setTotalIngresos(totalIngresos);
        dto.setTotalEgresos(totalEgresos);
        dto.setCajaFinalCalculada(cajaInicial.add(totalIngresos).subtract(totalEgresos));
        dto.setIngresosPorCategoria(ingresosPorCategoria);
        dto.setEgresosPorCategoria(egresosPorCategoria);
        dto.setTransaccionesTotales(ingresosHoy.size() + egresosHoy.size());

        return dto;
    }

    @Override
    public org.example.fitgymbackend.modules.finance.dto.ReporteCuentasCobrarDTO generarCuentasPorCobrar() {
        List<Socio> todos = socioRepository.findAll();
        List<org.example.fitgymbackend.modules.finance.dto.ReporteCuentasCobrarDTO.DeudorDTO> deudores = new ArrayList<>();
        BigDecimal totalAdeudado = BigDecimal.ZERO;

        for (Socio s : todos) {
            if ("Inactivo".equalsIgnoreCase(s.getEstatus()) || "Cancelado".equalsIgnoreCase(s.getEstatus())) {
                continue;
            }
            if (s.getFechaFin() != null && s.getFechaFin().isBefore(LocalDate.now())) {
                BigDecimal adeudado = BigDecimal.ZERO;
                try {
                    adeudado = s.getCostoMensual() != null ? new BigDecimal(s.getCostoMensual().replace("$","").replace(",","").trim()) : BigDecimal.ZERO;
                } catch (Exception ignored) {}
                
                if (adeudado.compareTo(BigDecimal.ZERO) > 0) {
                    org.example.fitgymbackend.modules.finance.dto.ReporteCuentasCobrarDTO.DeudorDTO deudor = new org.example.fitgymbackend.modules.finance.dto.ReporteCuentasCobrarDTO.DeudorDTO(
                            s.getId(),
                            s.getNombreCompleto(),
                            s.getTelefono(),
                            s.getFechaFin().toString(),
                            adeudado
                    );
                    deudores.add(deudor);
                    totalAdeudado = totalAdeudado.add(adeudado);
                }
            }
        }

        return new org.example.fitgymbackend.modules.finance.dto.ReporteCuentasCobrarDTO(
                LocalDate.now().toString(),
                totalAdeudado,
                deudores.size(),
                deudores
        );
    }

    @Override
    public org.example.fitgymbackend.modules.finance.dto.ReporteMembresiasDTO generarAnalisisMembresias(int mes, int anio) {
        List<Socio> todos = socioRepository.findAll();
        int activas = 0;
        int inactivasVencidas = 0;
        int nuevasDelMes = 0;
        Map<String, Integer> distribucion = new HashMap<>();

        for (Socio s : todos) {
            boolean isActivo = "Activo".equalsIgnoreCase(s.getEstatus());
            if (isActivo && (s.getFechaFin() == null || !s.getFechaFin().isBefore(LocalDate.now()))) {
                activas++;
                String tipo = s.getTipoMembresia() != null ? s.getTipoMembresia() : "GENERAL";
                distribucion.put(tipo, distribucion.getOrDefault(tipo, 0) + 1);
            } else {
                inactivasVencidas++;
            }

            if (s.getFechaRegistro() != null && s.getFechaRegistro().getYear() == anio && s.getFechaRegistro().getMonthValue() == mes) {
                nuevasDelMes++;
            }
        }

        return new org.example.fitgymbackend.modules.finance.dto.ReporteMembresiasDTO(
                mes + "/" + anio,
                todos.size(),
                activas,
                inactivasVencidas,
                nuevasDelMes,
                distribucion
        );
    }

    @Override
    public org.example.fitgymbackend.modules.finance.dto.ReporteVentasCategoriaDTO generarVentasPorCategoria(int mes, int anio) {
        LocalDateTime startDate = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);

        List<Transaccion> ingresosList = transaccionRepository.findByTipoAndFechaBetween("INGRESO", startDate, endDate);
        BigDecimal totalVentas = BigDecimal.ZERO;
        Map<String, BigDecimal> ventasPorCat = new HashMap<>();
        
        for (Transaccion t : ingresosList) {
            BigDecimal monto = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
            totalVentas = totalVentas.add(monto);
            String cat = t.getCategoria() != null ? t.getCategoria() : "OTROS";
            ventasPorCat.put(cat, ventasPorCat.getOrDefault(cat, BigDecimal.ZERO).add(monto));
        }

        Map<String, Double> porcentajes = new HashMap<>();
        if (totalVentas.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : ventasPorCat.entrySet()) {
                double pct = entry.getValue().doubleValue() / totalVentas.doubleValue() * 100.0;
                porcentajes.put(entry.getKey(), Math.round(pct * 100.0) / 100.0);
            }
        }

        return new org.example.fitgymbackend.modules.finance.dto.ReporteVentasCategoriaDTO(
                mes + "/" + anio,
                totalVentas,
                ventasPorCat,
                porcentajes
        );
    }

    @Override
    public org.example.fitgymbackend.modules.finance.dto.ReportePredictivoDTO generarKPIsPredictivos(int mes, int anio) {
        // Churn Rate
        LocalDate startOfMonth = LocalDate.of(anio, mes, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        List<Socio> todos = socioRepository.findAll();
        int sociosInicioMes = 0;
        int sociosCanceladosEsteMes = 0;
        
        long totalMesesVida = 0;
        int sociosLtvCount = 0;
        BigDecimal sumaCostosMensuales = BigDecimal.ZERO;

        for (Socio s : todos) {
            // Activos al inicio del mes (registrados antes y no vencidos antes del inicio)
            if (s.getFechaRegistro() != null && s.getFechaRegistro().isBefore(startOfMonth)) {
                if (s.getFechaFin() == null || s.getFechaFin().isAfter(startOfMonth)) {
                    sociosInicioMes++;
                }
            }
            
            // Cancelados/Vencidos este mes
            if (s.getFechaFin() != null && 
                !s.getFechaFin().isBefore(startOfMonth) && 
                !s.getFechaFin().isAfter(endOfMonth) && 
                !"Activo".equalsIgnoreCase(s.getEstatus())) {
                sociosCanceladosEsteMes++;
            }
            
            // LTV Data
            if (s.getFechaRegistro() != null) {
                LocalDate fin = s.getFechaFin() != null ? s.getFechaFin() : LocalDate.now();
                if (fin.isAfter(s.getFechaRegistro())) {
                    long meses = ChronoUnit.MONTHS.between(s.getFechaRegistro(), fin);
                    totalMesesVida += (meses > 0 ? meses : 1);
                    sociosLtvCount++;
                    try {
                        BigDecimal costo = s.getCostoMensual() != null ? new BigDecimal(s.getCostoMensual().replace("$","").replace(",","").trim()) : BigDecimal.ZERO;
                        sumaCostosMensuales = sumaCostosMensuales.add(costo);
                    } catch (Exception ignored) {}
                }
            }
        }

        double churnRate = sociosInicioMes > 0 ? ((double) sociosCanceladosEsteMes / sociosInicioMes) * 100.0 : 0.0;

        // LTV = Costo Promedio Mensual * Vida Promedio en Meses
        BigDecimal ltv = BigDecimal.ZERO;
        if (sociosLtvCount > 0) {
            BigDecimal costoPromedioMensual = sumaCostosMensuales.divide(new BigDecimal(sociosLtvCount), 2, java.math.RoundingMode.HALF_UP);
            double vidaPromedioMeses = (double) totalMesesVida / sociosLtvCount;
            ltv = costoPromedioMensual.multiply(new BigDecimal(vidaPromedioMeses));
        }

        // Mock CAC (Marketing / Nuevos)
        BigDecimal cac = new BigDecimal("150.00"); // Mock constante de $150 MXN por usuario nuevo
        Double ratioLtvCac = cac.compareTo(BigDecimal.ZERO) > 0 ? ltv.doubleValue() / cac.doubleValue() : 0.0;

        // Break-even
        // Costos fijos (Gastos operativos + Nomina)
        LocalDateTime startD = startOfMonth.atStartOfDay();
        LocalDateTime endD = endOfMonth.atTime(23, 59, 59);
        List<Transaccion> egresos = transaccionRepository.findByTipoAndFechaBetween("EGRESO", startD, endD);
        BigDecimal costosFijos = BigDecimal.ZERO;
        for(Transaccion t : egresos) {
             if ("MANTENIMIENTO".equalsIgnoreCase(t.getCategoria()) || "SERVICIOS".equalsIgnoreCase(t.getCategoria()) || "RENTA".equalsIgnoreCase(t.getCategoria()) || "NOMINA".equalsIgnoreCase(t.getCategoria())) {
                 costosFijos = costosFijos.add(t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO);
             }
        }
        
        // Margen de contribución simulado de 60% para el gym
        BigDecimal margenPorcentaje = new BigDecimal("0.60");
        BigDecimal breakEven = margenPorcentaje.compareTo(BigDecimal.ZERO) > 0 ? costosFijos.divide(margenPorcentaje, 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;

        org.example.fitgymbackend.modules.finance.dto.ReportePredictivoDTO dto = new org.example.fitgymbackend.modules.finance.dto.ReportePredictivoDTO();
        dto.setPeriodo(mes + "/" + anio);
        dto.setSociosInicioMes(sociosInicioMes);
        dto.setSociosCancelados(sociosCanceladosEsteMes);
        dto.setChurnRatePorcentaje(Math.round(churnRate * 100.0) / 100.0);
        dto.setLifetimeValue(ltv);
        dto.setCustomerAcquisitionCost(cac);
        dto.setRatioLtvCac(Math.round(ratioLtvCac * 100.0) / 100.0);
        dto.setCostosFijosEstimados(costosFijos);
        dto.setMargenContribucionPorcentaje(margenPorcentaje.multiply(new BigDecimal("100")));
        dto.setPuntoEquilibrio(breakEven);

        return dto;
    }

    // --- Métodos de exportación simulados por ahora ---
    @Override
    public byte[] exportarEstadoResultadosPdf(int mes, int anio) {
        return "Contenido PDF Simulado - Estado de Resultados".getBytes();
    }

    @Override
    public byte[] exportarEstadoResultadosXls(int mes, int anio) {
        return "Contenido Excel Simulado - Estado de Resultados".getBytes();
    }

    @Override
    public byte[] exportarBalanceGeneralPdf() {
        return "Contenido PDF Simulado - Balance General".getBytes();
    }

    @Override
    public byte[] exportarBalanceGeneralXls() {
        return "Contenido Excel Simulado - Balance General".getBytes();
    }

    @Override
    public byte[] exportarFlujoEfectivoPdf(int mes, int anio) {
        return "Contenido PDF Simulado - Flujo de Efectivo".getBytes();
    }

    @Override
    public byte[] exportarFlujoEfectivoXls(int mes, int anio) {
        return "Contenido Excel Simulado - Flujo de Efectivo".getBytes();
    }

    // --- Exportaciones Bloque 2 y 3 ---
    @Override
    public byte[] exportarOperacionesDiariasPdf() { return "PDF Simulado - Operaciones Diarias".getBytes(); }
    @Override
    public byte[] exportarOperacionesDiariasXls() { return "XLS Simulado - Operaciones Diarias".getBytes(); }
    
    @Override
    public byte[] exportarCuentasPorCobrarPdf() { return "PDF Simulado - Cuentas por Cobrar".getBytes(); }
    @Override
    public byte[] exportarCuentasPorCobrarXls() { return "XLS Simulado - Cuentas por Cobrar".getBytes(); }
    
    @Override
    public byte[] exportarAnalisisMembresiasPdf(int mes, int anio) { return "PDF Simulado - Análisis de Membresías".getBytes(); }
    @Override
    public byte[] exportarAnalisisMembresiasXls(int mes, int anio) { return "XLS Simulado - Análisis de Membresías".getBytes(); }
    
    @Override
    public byte[] exportarVentasPorCategoriaPdf(int mes, int anio) { return "PDF Simulado - Ventas por Categoría".getBytes(); }
    @Override
    public byte[] exportarVentasPorCategoriaXls(int mes, int anio) { return "XLS Simulado - Ventas por Categoría".getBytes(); }
    
    @Override
    public byte[] exportarKPIsPredictivosPdf(int mes, int anio) { return "PDF Simulado - KPIs Predictivos".getBytes(); }
    @Override
    public byte[] exportarKPIsPredictivosXls(int mes, int anio) { return "XLS Simulado - KPIs Predictivos".getBytes(); }
}
