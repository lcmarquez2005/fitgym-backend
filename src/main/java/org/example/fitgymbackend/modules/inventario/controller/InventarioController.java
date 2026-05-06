package org.example.fitgymbackend.modules.inventario.controller;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.inventario.entity.*;
import org.example.fitgymbackend.modules.inventario.repository.*;
import org.example.fitgymbackend.modules.inventario.service.IInventarioService;
import org.example.fitgymbackend.repository.IUsuarioRepository;
import org.example.fitgymbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired private CategoriaEquipoRepository categoriaRepo;
    @Autowired private EquipoRepository equipoRepo;
    @Autowired private ProveedorRepository proveedorRepo;
    @Autowired private SuplementoRepository suplementoRepo;
    @Autowired private MantenimientoRepository mantenimientoRepo;

    @Autowired private IInventarioService inventarioService;
    @Autowired private IUsuarioRepository usuarioRepo;
    @Autowired private JwtUtil jwtUtil;

    // Helper para obtener el ID del usuario logueado
    private Long getUsuarioId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            Optional<Usuario> userOpt = usuarioRepo.findByEmail(email);
            if (userOpt.isPresent()) {
                return userOpt.get().getId().longValue();
            }
        }
        return null;
    }

    // ==========================================
    // CATEGORÍAS
    // ==========================================
    @GetMapping("/categorias")
    public ResponseEntity<ApiResponse> getCategorias() {
        return ResponseEntity.ok(new ApiResponse("Categorías", true, categoriaRepo.findAll()));
    }

    @PostMapping("/categorias")
    public ResponseEntity<ApiResponse> createCategoria(@RequestBody CategoriaEquipo categoria) {
        return ResponseEntity.ok(new ApiResponse("Categoría creada", true, categoriaRepo.save(categoria)));
    }

    // ==========================================
    // EQUIPOS
    // ==========================================
    @GetMapping("/equipos")
    public ResponseEntity<ApiResponse> getEquipos() {
        return ResponseEntity.ok(new ApiResponse("Equipos", true, equipoRepo.findAll()));
    }

    @PostMapping("/equipos")
    public ResponseEntity<ApiResponse> createEquipo(@RequestBody Equipo equipo) {
        return ResponseEntity.ok(new ApiResponse("Equipo creado", true, equipoRepo.save(equipo)));
    }

    @GetMapping("/equipos/estado/{estado}")
    public ResponseEntity<ApiResponse> getEquiposByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(new ApiResponse("Equipos por estado", true, equipoRepo.findByEstado(estado)));
    }

    // ==========================================
    // MANTENIMIENTOS
    // ==========================================
    @GetMapping("/mantenimiento/equipo/{equipoId}")
    public ResponseEntity<ApiResponse> getMantenimientosPorEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.ok(new ApiResponse("Mantenimientos", true, mantenimientoRepo.findByEquipoIdOrderByFechaDesc(equipoId)));
    }

    @PostMapping("/mantenimiento")
    public ResponseEntity<ApiResponse> registrarMantenimiento(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        Long empleadoId = getUsuarioId(authHeader);
        return ResponseEntity.ok(inventarioService.registrarMantenimiento(body, empleadoId));
    }

    // ==========================================
    // PROVEEDORES
    // ==========================================
    @GetMapping("/proveedores")
    public ResponseEntity<ApiResponse> getProveedores() {
        return ResponseEntity.ok(new ApiResponse("Proveedores", true, proveedorRepo.findByActivoTrue()));
    }

    @PostMapping("/proveedores")
    public ResponseEntity<ApiResponse> createProveedor(@RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(new ApiResponse("Proveedor creado", true, proveedorRepo.save(proveedor)));
    }

    // ==========================================
    // SUPLEMENTOS
    // ==========================================
    @GetMapping("/suplementos")
    public ResponseEntity<ApiResponse> getSuplementos() {
        return ResponseEntity.ok(new ApiResponse("Suplementos", true, suplementoRepo.findByActivoTrue()));
    }

    @PostMapping("/suplementos")
    public ResponseEntity<ApiResponse> createSuplemento(@RequestBody Suplemento suplemento) {
        return ResponseEntity.ok(new ApiResponse("Suplemento creado", true, suplementoRepo.save(suplemento)));
    }

    @GetMapping("/suplementos/stock-bajo")
    public ResponseEntity<ApiResponse> getStockBajo() {
        // Obtenemos todos los activos y filtramos los que tengan stock <= stockMinimo
        List<Suplemento> todos = suplementoRepo.findByActivoTrue();
        List<Suplemento> alertas = todos.stream()
                .filter(s -> s.getStock() <= s.getStockMinimo())
                .toList();
        return ResponseEntity.ok(new ApiResponse("Alertas de stock bajo", true, alertas));
    }

    @PostMapping("/suplementos/{id}/vender")
    public ResponseEntity<ApiResponse> venderSuplemento(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Integer> body) {
        Long empleadoId = getUsuarioId(authHeader);
        Integer cantidad = body.getOrDefault("cantidad", 1);
        ApiResponse response = inventarioService.venderSuplemento(id, cantidad, empleadoId);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/suplementos/{id}/reabastecer")
    public ResponseEntity<ApiResponse> reabastecerSuplemento(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Integer> body) {
        Long empleadoId = getUsuarioId(authHeader);
        Integer cantidad = body.getOrDefault("cantidad", 1);
        ApiResponse response = inventarioService.reabastecerSuplemento(id, cantidad, empleadoId);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}
