package org.example.fitgymbackend.modules.finance.service;

import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.dto.GenerarNominaRequest;
import org.example.fitgymbackend.modules.finance.entity.*;
import org.example.fitgymbackend.modules.finance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class NominaServiceImpl implements INominaService {

    @Autowired
    private EmpleadoFinanceRepository empleadoRepository;
    
    @Autowired
    private NominaRepository nominaRepository;
    
    @Autowired
    private TransaccionRepository transaccionRepository;

    @Override
    @Transactional
    public ApiResponse generarNominaMasiva(GenerarNominaRequest request) {
        List<EmpleadoFinance> empleadosActivos = empleadoRepository.findByActivoTrue();
        
        if (empleadosActivos.isEmpty()) {
            return new ApiResponse("No hay empleados activos para generar nomina", false, null);
        }

        Nomina nomina = new Nomina();
        nomina.setPeriodo(request.getPeriodo());
        nomina.setFechaInicio(request.getFechaInicio());
        nomina.setFechaFin(request.getFechaFin());
        nomina.setEstado("BORRADOR");

        for (EmpleadoFinance emp : empleadosActivos) {
            ReciboNomina recibo = new ReciboNomina();
            recibo.setEmpleado(emp);
            recibo.setNomina(nomina);
            
            BigDecimal totalPercepciones = BigDecimal.ZERO;
            BigDecimal totalDeducciones = BigDecimal.ZERO;
            
            // 1. Sueldo Base (15 dias)
            BigDecimal sueldoQuincenal = emp.getSueldoBaseDiario().multiply(new BigDecimal(15));
            totalPercepciones = totalPercepciones.add(sueldoQuincenal);
            
            DetalleRecibo dSueldo = new DetalleRecibo();
            dSueldo.setReciboNomina(recibo);
            dSueldo.setTipo("PERCEPCION");
            dSueldo.setConcepto("Sueldo Base 15 días");
            dSueldo.setMonto(sueldoQuincenal);
            recibo.getDetalles().add(dSueldo);

            // 2. Calculo automático de Comisiones (5% sobre ventas para vendedores y entrenadores)
            if ("VENDEDOR".equalsIgnoreCase(emp.getPuesto()) || "ENTRENADOR".equalsIgnoreCase(emp.getPuesto())) {
                LocalDateTime start = LocalDateTime.of(request.getFechaInicio(), LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(request.getFechaFin(), LocalTime.MAX);
                List<Transaccion> ventas = transaccionRepository.findByEmpleadoIdAndFechaHoraBetween(emp.getUsuario().getId().longValue(), start, end);
                
                BigDecimal sumaVentas = ventas.stream().map(Transaccion::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal comision = sumaVentas.multiply(emp.getPorcentajeComision());
                
                if (comision.compareTo(BigDecimal.ZERO) > 0) {
                    totalPercepciones = totalPercepciones.add(comision);
                    DetalleRecibo dComision = new DetalleRecibo();
                    dComision.setReciboNomina(recibo);
                    dComision.setTipo("PERCEPCION");
                    dComision.setConcepto("Comisión Ventas ($" + sumaVentas + ")");
                    dComision.setMonto(comision);
                    recibo.getDetalles().add(dComision);
                }
            }

            // 3. Impuestos (Fórmula sencilla: ISR 10%, IMSS 2% sobre el total percibido)
            BigDecimal isr = totalPercepciones.multiply(new BigDecimal("0.10"));
            BigDecimal imss = totalPercepciones.multiply(new BigDecimal("0.02"));
            
            totalDeducciones = totalDeducciones.add(isr).add(imss);
            
            DetalleRecibo dIsr = new DetalleRecibo();
            dIsr.setReciboNomina(recibo);
            dIsr.setTipo("DEDUCCION");
            dIsr.setConcepto("Retención ISR (10%)");
            dIsr.setMonto(isr);
            recibo.getDetalles().add(dIsr);
            
            DetalleRecibo dImss = new DetalleRecibo();
            dImss.setReciboNomina(recibo);
            dImss.setTipo("DEDUCCION");
            dImss.setConcepto("Cuota IMSS (2%)");
            dImss.setMonto(imss);
            recibo.getDetalles().add(dImss);

            // 4. Totales
            recibo.setTotalPercepciones(totalPercepciones);
            recibo.setTotalDeducciones(totalDeducciones);
            recibo.setNetoAPagar(totalPercepciones.subtract(totalDeducciones));

            nomina.getRecibos().add(recibo);
        }

        nominaRepository.save(nomina);
        return new ApiResponse("Nomina de " + nomina.getRecibos().size() + " empleados generada con éxito", true, nomina);
    }
}
