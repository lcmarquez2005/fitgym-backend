package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.dto.*;
import org.example.fitgymbackend.modules.finance.entity.*;
import org.example.fitgymbackend.modules.finance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ImpuestoServiceImpl implements IImpuestoService {

    @Autowired
    private PeriodoFiscalRepository periodoFiscalRepository;

    @Autowired
    private RegistroIVARepository registroIVARepository;

    @Autowired
    private RetencionISRRepository retencionISRRepository;

    @Autowired
    private RegistroDIOTRepository registroDIOTRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    // ═══════════════════════════════════════════════════
    // PERÍODOS FISCALES
    // ═══════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse crearPeriodoFiscal(PeriodoFiscalRequest request) {
        PeriodoFiscal periodo = new PeriodoFiscal();
        periodo.setNombre(request.getNombre());
        periodo.setTipoPeriodo(request.getTipoPeriodo());
        periodo.setFechaInicio(request.getFechaInicio());
        periodo.setFechaFin(request.getFechaFin());
        periodo.setFechaLimite(request.getFechaLimite());
        periodo.setEstado("PENDIENTE");
        periodoFiscalRepository.save(periodo);
        return new ApiResponse("Periodo fiscal creado", true, periodo);
    }

    @Override
    public ApiResponse listarPeriodos(String estado) {
        List<PeriodoFiscal> lista = (estado != null && !estado.isBlank())
                ? periodoFiscalRepository.findByEstadoOrderByFechaLimiteAsc(estado)
                : periodoFiscalRepository.findAll();
        return new ApiResponse("Periodos recuperados", true, lista);
    }

    @Override
    @Transactional
    public ApiResponse marcarDeclaracionPresentada(Long periodoId) {
        Optional<PeriodoFiscal> opt = periodoFiscalRepository.findById(periodoId);
        if (opt.isEmpty()) return new ApiResponse("Periodo no encontrado", false, null);
        PeriodoFiscal p = opt.get();
        p.setEstado("PRESENTADA");
        periodoFiscalRepository.save(p);
        return new ApiResponse("Declaración marcada como PRESENTADA", true, p);
    }

    // ═══════════════════════════════════════════════════
    // IVA — Se calcula automáticamente desde la caja
    // ═══════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse calcularIVADelPeriodo(RegistroIVARequest request) {
        Optional<PeriodoFiscal> periodoOpt = periodoFiscalRepository.findById(request.getPeriodoFiscalId());
        if (periodoOpt.isEmpty()) return new ApiResponse("Periodo fiscal no encontrado", false, null);

        PeriodoFiscal periodo = periodoOpt.get();

        // Traer los ingresos de la caja del periodo para calcular el IVA trasladado automáticamente
        LocalDateTime inicio = LocalDateTime.of(periodo.getFechaInicio(), LocalTime.MIN);
        LocalDateTime fin = LocalDateTime.of(periodo.getFechaFin(), LocalTime.MAX);
        List<Transaccion> ingresos = transaccionRepository.findByTipoAndFechaBetween("INGRESO", inicio, fin);

        BigDecimal totalIngresos = ingresos.stream()
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Los ingresos se consideran con IVA incluido: base = total / 1.16
        BigDecimal baseGravable = totalIngresos.divide(new BigDecimal("1.16"), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal ivaTrasladado = totalIngresos.subtract(baseGravable);
        BigDecimal ivaAcreditable = request.getIvaAcreditable() != null ? request.getIvaAcreditable() : BigDecimal.ZERO;
        BigDecimal ivaNeto = ivaTrasladado.subtract(ivaAcreditable); // positivo = a pagar; negativo = saldo a favor

        RegistroIVA registro = new RegistroIVA();
        registro.setPeriodiFiscal(periodo);
        registro.setBaseGravable(baseGravable);
        registro.setIvaTrasladado(ivaTrasladado);
        registro.setIvaAcreditable(ivaAcreditable);
        registro.setIvaNeto(ivaNeto);
        registro.setObservaciones(request.getObservaciones());
        registroIVARepository.save(registro);

        return new ApiResponse(
                ivaNeto.compareTo(BigDecimal.ZERO) > 0
                        ? "IVA calculado — A PAGAR: $" + ivaNeto
                        : "IVA calculado — SALDO A FAVOR: $" + ivaNeto.abs(),
                true, registro);
    }

    // ═══════════════════════════════════════════════════
    // RETENCIONES ISR
    // ═══════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse registrarRetencionISR(RetencionISRRequest request) {
        Optional<PeriodoFiscal> periodoOpt = periodoFiscalRepository.findById(request.getPeriodoFiscalId());
        if (periodoOpt.isEmpty()) return new ApiResponse("Periodo fiscal no encontrado", false, null);

        BigDecimal tasa = request.getTasaAplicada() != null ? request.getTasaAplicada() : new BigDecimal("0.10");
        BigDecimal montoISR = request.getMontoBase().multiply(tasa);

        RetencionISR retencion = new RetencionISR();
        retencion.setPeriodiFiscal(periodoOpt.get());
        retencion.setTipoRetencion(request.getTipoRetencion());
        retencion.setNombreBeneficiario(request.getNombreBeneficiario());
        retencion.setRfcBeneficiario(request.getRfcBeneficiario());
        retencion.setMontoBase(request.getMontoBase());
        retencion.setTasaAplicada(tasa);
        retencion.setMontoISR(montoISR);
        retencionISRRepository.save(retencion);

        return new ApiResponse("Retención ISR registrada — Monto: $" + montoISR, true, retencion);
    }

    // ═══════════════════════════════════════════════════
    // DIOT
    // ═══════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse registrarProveedorDIOT(RegistroDIOTRequest request) {
        RegistroDIOT diot = new RegistroDIOT();
        diot.setMesDeclaracion(request.getMesDeclaracion());
        diot.setRfcProveedor(request.getRfcProveedor());
        diot.setNombreProveedor(request.getNombreProveedor());
        diot.setTipoProveedor(request.getTipoProveedor());
        diot.setMontoOperacion(request.getMontoOperacion());
        diot.setIvaAcreditable(request.getIvaAcreditable());
        diot.setConcepto(request.getConcepto());
        registroDIOTRepository.save(diot);
        return new ApiResponse("Proveedor DIOT registrado", true, diot);
    }

    @Override
    public ApiResponse listarDIOTPorMes(String mes) {
        List<RegistroDIOT> lista = registroDIOTRepository.findByMesDeclaracion(mes);
        return new ApiResponse("DIOT del mes " + mes, true, lista);
    }
}
