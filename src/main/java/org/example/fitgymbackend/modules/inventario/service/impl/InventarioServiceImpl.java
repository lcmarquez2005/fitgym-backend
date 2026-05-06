package org.example.fitgymbackend.modules.inventario.service.impl;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.entity.CorteCaja;
import org.example.fitgymbackend.modules.finance.entity.Transaccion;
import org.example.fitgymbackend.modules.finance.repository.CorteCajaRepository;
import org.example.fitgymbackend.modules.finance.repository.TransaccionRepository;
import org.example.fitgymbackend.modules.inventario.entity.Equipo;
import org.example.fitgymbackend.modules.inventario.entity.Mantenimiento;
import org.example.fitgymbackend.modules.inventario.entity.Suplemento;
import org.example.fitgymbackend.modules.inventario.repository.EquipoRepository;
import org.example.fitgymbackend.modules.inventario.repository.MantenimientoRepository;
import org.example.fitgymbackend.modules.inventario.repository.SuplementoRepository;
import org.example.fitgymbackend.modules.inventario.service.IInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class InventarioServiceImpl implements IInventarioService {

    @Autowired
    private SuplementoRepository suplementoRepo;

    @Autowired
    private MantenimientoRepository mantenimientoRepo;

    @Autowired
    private EquipoRepository equipoRepo;

    @Autowired
    private TransaccionRepository transaccionRepo;

    @Autowired
    private CorteCajaRepository corteCajaRepo;

    @Override
    @Transactional
    public ApiResponse venderSuplemento(Long suplementoId, Integer cantidad, Long empleadoId) {
        Optional<Suplemento> supOpt = suplementoRepo.findById(suplementoId);
        if (supOpt.isEmpty()) {
            return new ApiResponse("Suplemento no encontrado", false, null);
        }

        Suplemento suplemento = supOpt.get();
        if (suplemento.getStock() < cantidad) {
            return new ApiResponse("Stock insuficiente. Stock actual: " + suplemento.getStock(), false, null);
        }

        // Descontar stock
        suplemento.setStock(suplemento.getStock() - cantidad);
        suplementoRepo.save(suplemento);

        // Registrar transacción de ingreso
        BigDecimal totalVenta = suplemento.getPrecioVenta().multiply(new BigDecimal(cantidad));
        registrarTransaccion("INGRESO", "SUPLEMENTO", totalVenta, 
                "Venta de suplemento: " + suplemento.getNombre() + " (x" + cantidad + ")", empleadoId);

        return new ApiResponse("Venta registrada exitosamente", true, suplemento);
    }

    @Override
    @Transactional
    public ApiResponse reabastecerSuplemento(Long suplementoId, Integer cantidad, Long empleadoId) {
        Optional<Suplemento> supOpt = suplementoRepo.findById(suplementoId);
        if (supOpt.isEmpty()) {
            return new ApiResponse("Suplemento no encontrado", false, null);
        }

        Suplemento suplemento = supOpt.get();
        
        // Aumentar stock
        suplemento.setStock(suplemento.getStock() + cantidad);
        suplementoRepo.save(suplemento);

        // Registrar transacción de egreso
        BigDecimal totalCompra = suplemento.getPrecioCompra().multiply(new BigDecimal(cantidad));
        registrarTransaccion("EGRESO", "INVENTARIO", totalCompra, 
                "Reabastecimiento de suplemento: " + suplemento.getNombre() + " (x" + cantidad + ")", empleadoId);

        return new ApiResponse("Reabastecimiento registrado exitosamente", true, suplemento);
    }

    @Override
    @Transactional
    public ApiResponse registrarMantenimiento(Map<String, Object> body, Long empleadoId) {
        Long equipoId = Long.valueOf(body.get("equipoId").toString());
        Optional<Equipo> equipoOpt = equipoRepo.findById(equipoId);
        
        if (equipoOpt.isEmpty()) {
            return new ApiResponse("Equipo no encontrado", false, null);
        }

        Mantenimiento mant = new Mantenimiento();
        mant.setEquipo(equipoOpt.get());
        mant.setFecha(LocalDate.parse(body.getOrDefault("fecha", LocalDate.now().toString()).toString()));
        mant.setTipo((String) body.getOrDefault("tipo", "PREVENTIVO"));
        mant.setDescripcion((String) body.get("descripcion"));
        mant.setTecnico((String) body.get("tecnico"));
        
        BigDecimal costo = new BigDecimal(body.getOrDefault("costo", "0").toString());
        mant.setCosto(costo);

        // Registrar transacción de egreso si hay costo
        if (costo.compareTo(BigDecimal.ZERO) > 0) {
            Transaccion t = registrarTransaccion("EGRESO", "MANTENIMIENTO", costo, 
                    "Mantenimiento " + mant.getTipo() + " a equipo: " + equipoOpt.get().getNombre(), empleadoId);
            mant.setTransaccionId(t.getId());
        }

        mantenimientoRepo.save(mant);

        return new ApiResponse("Mantenimiento registrado exitosamente", true, mant);
    }

    private Transaccion registrarTransaccion(String tipo, String categoria, BigDecimal monto, String descripcion, Long empleadoId) {
        Optional<CorteCaja> cajaAbierta = corteCajaRepo.findFirstByEstadoOrderByIdDesc("ABIERTA");
        
        Transaccion t = new Transaccion();
        t.setTipo(tipo);
        t.setCategoria(categoria);
        t.setMonto(monto);
        t.setDescripcion(descripcion);
        t.setFechaHora(LocalDateTime.now());
        t.setEmpleadoId(empleadoId);
        t.setRequiereFactura(false);
        
        cajaAbierta.ifPresent(t::setCorteCaja);
        
        return transaccionRepo.save(t);
    }
}
