package org.example.fitgymbackend.modules.inventario.service;

import org.example.fitgymbackend.model.response.ApiResponse;
import java.util.Map;

public interface IInventarioService {
    ApiResponse venderSuplemento(Long suplementoId, Integer cantidad, Long empleadoId);
    ApiResponse reabastecerSuplemento(Long suplementoId, Integer cantidad, Long empleadoId);
    ApiResponse registrarMantenimiento(Map<String, Object> body, Long empleadoId);
}
