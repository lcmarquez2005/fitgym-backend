package org.example.fitgymbackend.modules.reservas.service;

import org.example.fitgymbackend.modules.reservas.entity.CatalogoClase;
import org.example.fitgymbackend.modules.reservas.entity.ClaseProgramada;
import org.example.fitgymbackend.modules.reservas.entity.Reserva;
import org.example.fitgymbackend.modules.reservas.entity.Salon;

import java.time.LocalDate;
import java.util.List;

public interface IReservasService {

    // Catalogos y Salones
    List<Salon> obtenerSalonesActivos();
    Salon guardarSalon(Salon salon);

    List<CatalogoClase> obtenerCatalogoActivo();
    CatalogoClase guardarCatalogoClase(CatalogoClase catalogo);

    void eliminarSalon(Long id);
    void eliminarCatalogoClase(Long id);

    // Programacion
    List<ClaseProgramada> obtenerHorario(LocalDate inicio, LocalDate fin);
    ClaseProgramada programarClase(ClaseProgramada claseProgramada);
    void cancelarClaseProgramada(Long claseId);

    // Reservas (Socio)
    Reserva reservarClase(Long claseId, Long socioId);
    Reserva cancelarReserva(Long reservaId);
    Reserva registrarAsistencia(Long reservaId, boolean asistio);

    // Consultas
    List<Reserva> obtenerReservasPorClase(Long claseId);
    List<Reserva> obtenerReservasPorSocio(Long socioId);

}
