package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.entity.CorteCaja;
import org.example.fitgymbackend.modules.finance.entity.Transaccion;
import org.example.fitgymbackend.modules.finance.repository.CorteCajaRepository;
import org.example.fitgymbackend.modules.finance.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FinanceServiceImpl implements IFinanceService {

    @Autowired
    private CorteCajaRepository corteCajaRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Override
    @Transactional
    public ApiResponse abrirCaja(Integer usuarioId, BigDecimal saldoInicial) {
        Optional<CorteCaja> cajaAbierta = corteCajaRepository.findFirstByEstadoOrderByIdDesc("ABIERTA");
        if (cajaAbierta.isPresent()) {
            return new ApiResponse("Ya existe una caja abierta.", false, null);
        }

        CorteCaja corte = new CorteCaja();
        corte.setFechaHoraApertura(LocalDateTime.now());
        corte.setSaldoInicial(saldoInicial != null ? saldoInicial : BigDecimal.ZERO);
        corte.setUsuarioIdApertura(usuarioId);
        corte.setEstado("ABIERTA");

        corteCajaRepository.save(corte);
        return new ApiResponse("Caja abierta exitosamente", true, corte);
    }

    @Override
    @Transactional
    public ApiResponse cerrarCaja(Integer usuarioId) {
        Optional<CorteCaja> cajaOpt = corteCajaRepository.findFirstByEstadoOrderByIdDesc("ABIERTA");
        if (cajaOpt.isEmpty()) {
            return new ApiResponse("No hay caja abierta para cerrar.", false, null);
        }

        CorteCaja corte = cajaOpt.get();
        List<Transaccion> transacciones = transaccionRepository.findByCorteCajaId(corte.getId());
        
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;

        for (Transaccion t : transacciones) {
            if ("INGRESO".equalsIgnoreCase(t.getTipo())) {
                totalIngresos = totalIngresos.add(t.getMonto());
            } else if ("EGRESO".equalsIgnoreCase(t.getTipo())) {
                totalEgresos = totalEgresos.add(t.getMonto());
            }
        }

        BigDecimal saldoFinalCalculado = corte.getSaldoInicial().add(totalIngresos).subtract(totalEgresos);
        corte.setSaldoFinal(saldoFinalCalculado);
        corte.setFechaHoraCierre(LocalDateTime.now());
        corte.setEstado("CERRADA");

        corteCajaRepository.save(corte);
        return new ApiResponse("Caja cerrada exitosamente. Saldo Final: $" + saldoFinalCalculado, true, corte);
    }

    @Override
    @Transactional
    public ApiResponse registrarTransaccion(String tipo, String categoria, BigDecimal monto, String descripcion, Boolean requiereFactura) {
        Optional<CorteCaja> cajaOpt = corteCajaRepository.findFirstByEstadoOrderByIdDesc("ABIERTA");
        if (cajaOpt.isEmpty()) {
            return new ApiResponse("No se puede registrar transaccion: No hay caja abierta.", false, null);
        }

        Transaccion t = new Transaccion();
        t.setTipo(tipo);
        t.setCategoria(categoria);
        t.setMonto(monto);
        t.setDescripcion(descripcion);
        t.setFechaHora(LocalDateTime.now());
        t.setCorteCaja(cajaOpt.get());
        t.setRequiereFactura(requiereFactura != null ? requiereFactura : false);

        transaccionRepository.save(t);
        return new ApiResponse("Transaccion registrada correctamente", true, t);
    }

    @Override
    public ApiResponse obtenerCajaAbierta() {
        Optional<CorteCaja> cajaOpt = corteCajaRepository.findFirstByEstadoOrderByIdDesc("ABIERTA");
        if (cajaOpt.isPresent()) {
            return new ApiResponse("Caja actual recuperada", true, cajaOpt.get());
        }
        return new ApiResponse("No hay caja abierta actualmente", false, null);
    }
}
