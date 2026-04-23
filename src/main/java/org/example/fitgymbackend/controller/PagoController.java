package org.example.fitgymbackend.controller;

import org.example.fitgymbackend.model.request.PagoRequest;
import org.example.fitgymbackend.model.response.TicketResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
// Opcional: Agregar @CrossOrigin si desde el frontend te da error de CORS
public class PagoController {

    // Nuestra "Base de datos" temporal en memoria
    private List<TicketResponse> ticketsGuardados = new ArrayList<>();

    // 1. MÉTODO POST (Pantalla de Información de Pago)
    @PostMapping("/procesar")
    public TicketResponse procesarPago(@RequestBody PagoRequest request) {
        // Generamos un ID simulado (el tamaño de la lista + 1)
        Integer nuevoId = ticketsGuardados.size() + 1;

        // Creamos la respuesta con los datos que llegaron
        TicketResponse nuevoTicket = new TicketResponse(
                nuevoId,
                request.getNombre(),
                request.getPlan(),
                request.getMonto(),
                request.getVigencia()
        );

        // Lo "guardamos" en nuestra lista
        ticketsGuardados.add(nuevoTicket);

        // Devolvemos el ticket generado con su ID
        return nuevoTicket;
    }

    // 2. MÉTODO GET (Pantalla de Suscripción Exitosa)
    @GetMapping("/ticket/{id}")
    public TicketResponse obtenerTicket(@PathVariable Integer id) {
        // Buscamos en la lista el ticket que coincida con el ID
        return ticketsGuardados.stream()
                .filter(ticket -> ticket.getId().equals(id))
                .findFirst()
                .orElse(null); // Retorna vacío si no existe
    }
}