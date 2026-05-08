package org.example.fitgymbackend.modules.reservas.service.impl;

import org.example.fitgymbackend.entity.Socio;
import org.example.fitgymbackend.modules.reservas.entity.CatalogoClase;
import org.example.fitgymbackend.modules.reservas.entity.ClaseProgramada;
import org.example.fitgymbackend.modules.reservas.entity.Reserva;
import org.example.fitgymbackend.modules.reservas.entity.Salon;
import org.example.fitgymbackend.modules.reservas.repository.CatalogoClaseRepository;
import org.example.fitgymbackend.modules.reservas.repository.ClaseProgramadaRepository;
import org.example.fitgymbackend.modules.reservas.repository.ReservaRepository;
import org.example.fitgymbackend.modules.reservas.repository.SalonRepository;
import org.example.fitgymbackend.modules.reservas.service.IReservasService;
import org.example.fitgymbackend.repository.ISocioRepository;
import org.example.fitgymbackend.modules.finance.entity.CorteCaja;
import org.example.fitgymbackend.modules.finance.entity.Transaccion;
import org.example.fitgymbackend.modules.finance.repository.CorteCajaRepository;
import org.example.fitgymbackend.modules.finance.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ReservasServiceImpl implements IReservasService {

    @Autowired
    private SalonRepository salonRepository;

    @Autowired
    private CatalogoClaseRepository catalogoClaseRepository;

    @Autowired
    private ClaseProgramadaRepository claseProgramadaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ISocioRepository socioRepository;

    @Autowired
    private TransaccionRepository transaccionRepo;

    @Autowired
    private CorteCajaRepository corteCajaRepo;

    @Override
    public List<Salon> obtenerSalonesActivos() {
        return salonRepository.findByActivoTrue();
    }

    @Override
    public Salon guardarSalon(Salon salon) {
        return salonRepository.save(salon);
    }

    @Override
    public List<CatalogoClase> obtenerCatalogoActivo() {
        return catalogoClaseRepository.findByActivoTrue();
    }

    @Override
    public CatalogoClase guardarCatalogoClase(CatalogoClase catalogo) {
        return catalogoClaseRepository.save(catalogo);
    }

    @Override
    public void eliminarSalon(Long id) {
        Salon salon = salonRepository.findById(id).orElseThrow(() -> new RuntimeException("Salón no encontrado"));
        salon.setActivo(false);
        salonRepository.save(salon);
    }

    @Override
    public void eliminarCatalogoClase(Long id) {
        CatalogoClase catalogo = catalogoClaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Catálogo no encontrado"));
        catalogo.setActivo(false);
        catalogoClaseRepository.save(catalogo);
    }

    @Override
    public List<ClaseProgramada> obtenerHorario(LocalDate inicio, LocalDate fin) {
        return claseProgramadaRepository.findByFechaBetweenOrderByFechaAscHoraInicioAsc(inicio, fin);
    }

    @Override
    public ClaseProgramada programarClase(ClaseProgramada claseProgramada) {
        // Asignar cupo máximo del salón si no viene especificado
        if (claseProgramada.getCupoMaximo() == null) {
            Salon salon = salonRepository.findById(claseProgramada.getSalon().getId())
                    .orElseThrow(() -> new RuntimeException("Salón no encontrado"));
            claseProgramada.setCupoMaximo(salon.getCapacidad());
        }
        claseProgramada.setEstado("PROGRAMADA");
        claseProgramada.setReservasActuales(0);
        return claseProgramadaRepository.save(claseProgramada);
    }

    @Override
    @Transactional
    public void cancelarClaseProgramada(Long claseId) {
        ClaseProgramada clase = claseProgramadaRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        clase.setEstado("CANCELADA");
        claseProgramadaRepository.save(clase);

        // Cancelar todas las reservas asociadas
        List<Reserva> reservas = reservaRepository.findByClaseProgramadaIdOrderByFechaReservaAsc(claseId);
        for (Reserva res : reservas) {
            if (!res.getEstado().equals("CANCELADA")) {
                res.setEstado("CANCELADA_POR_GYM");
                reservaRepository.save(res);
            }
        }
    }

    @Override
    @Transactional
    public Reserva reservarClase(Long claseId, Long socioId) {
        ClaseProgramada clase = claseProgramadaRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (clase.getEstado().equals("CANCELADA") || clase.getEstado().equals("FINALIZADA")) {
            throw new RuntimeException("La clase no está disponible para reserva.");
        }

        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado"));

        if (!"ACTIVO".equalsIgnoreCase(socio.getEstatus())) {
            throw new RuntimeException("El socio no tiene una membresía activa.");
        }

        // Verificar si ya tiene una reserva para esta clase
        boolean yaReservado = reservaRepository.existsByClaseProgramadaIdAndSocioIdAndEstadoIn(
                claseId, socioId, Arrays.asList("CONFIRMADA", "EN_WAITLIST"));
        if (yaReservado) {
            throw new RuntimeException("El socio ya tiene una reserva activa para esta clase.");
        }

        Reserva reserva = new Reserva();
        reserva.setClaseProgramada(clase);
        reserva.setSocio(socio);
        reserva.setFechaReserva(LocalDateTime.now());

        // Lógica de Cupo y Waitlist
        if (clase.getReservasActuales() < clase.getCupoMaximo()) {
            reserva.setEstado("CONFIRMADA");
            clase.setReservasActuales(clase.getReservasActuales() + 1);
            claseProgramadaRepository.save(clase);

            // Registrar transacción si tiene costo
            BigDecimal costo = clase.getCatalogoClase().getCostoExtra();
            if (costo != null && costo.compareTo(BigDecimal.ZERO) > 0) {
                Transaccion t = registrarTransaccion("INGRESO", "CLASE_PREMIUM", costo,
                        "Reserva de clase premium: " + clase.getCatalogoClase().getNombre() + " (Socio: " + socio.getNombreCompleto() + ")", null);
                reserva.setTransaccionId(t.getId());
            }

        } else {
            reserva.setEstado("EN_WAITLIST");
        }

        return reservaRepository.save(reserva);
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

    @Override
    @Transactional
    public Reserva cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstado().equals("CANCELADA")) {
            return reserva;
        }

        String estadoAnterior = reserva.getEstado();
        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);

        ClaseProgramada clase = reserva.getClaseProgramada();

        // Si la reserva estaba confirmada, se libera un espacio y vemos si hay alguien en waitlist
        if (estadoAnterior.equals("CONFIRMADA")) {
            clase.setReservasActuales(clase.getReservasActuales() - 1);
            
            // Promover al primero de la Waitlist (Auto-upgrade)
            List<Reserva> waitlist = reservaRepository.findByClaseProgramadaIdAndEstadoOrderByFechaReservaAsc(clase.getId(), "EN_WAITLIST");
            if (!waitlist.isEmpty()) {
                Reserva aPromover = waitlist.get(0);
                aPromover.setEstado("CONFIRMADA");
                reservaRepository.save(aPromover);
                clase.setReservasActuales(clase.getReservasActuales() + 1);
            }
            claseProgramadaRepository.save(clase);
        }

        return reserva;
    }

    @Override
    public Reserva registrarAsistencia(Long reservaId, boolean asistio) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getEstado().equals("CONFIRMADA")) {
            throw new RuntimeException("Solo se puede registrar asistencia de reservas CONFIRMADAS.");
        }

        reserva.setEstado(asistio ? "ASISTIO" : "NO_ASISTIO");
        return reservaRepository.save(reserva);
    }

    @Override
    public List<Reserva> obtenerReservasPorClase(Long claseId) {
        return reservaRepository.findByClaseProgramadaIdOrderByFechaReservaAsc(claseId);
    }

    @Override
    public List<Reserva> obtenerReservasPorSocio(Long socioId) {
        return reservaRepository.findBySocioId(socioId);
    }
}
