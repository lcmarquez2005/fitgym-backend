package org.example.fitgymbackend.modules.finance.controller;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.modules.finance.entity.EmpleadoFinance;
import org.example.fitgymbackend.modules.finance.repository.EmpleadoFinanceRepository;
import org.example.fitgymbackend.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EmpleadoController — RRHH básico.
 * Los empleados son los Usuario del ERP que tienen una ficha de nómina asignada.
 * Flujo: Admin crea Usuario (Auth) → Admin asigna ficha de nómina aquí.
 */
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoFinanceRepository empleadoRepo;

    @Autowired
    private IUsuarioRepository usuarioRepo;

    /** Listar todos los empleados con ficha activa */
    @GetMapping
    public ResponseEntity<ApiResponse> listarEmpleados() {
        List<EmpleadoFinance> empleados = empleadoRepo.findByActivoTrue();
        return ResponseEntity.ok(new ApiResponse("Empleados activos", true, empleados));
    }

    /** Todos los usuarios del ERP que AÚN no tienen ficha de nómina */
    @GetMapping("/sin-ficha")
    public ResponseEntity<ApiResponse> usuariosSinFicha() {
        List<Integer> idsConFicha = empleadoRepo.findAll()
                .stream()
                .filter(e -> e.getUsuario() != null)
                .map(e -> e.getUsuario().getId())
                .collect(Collectors.toList());

        List<Usuario> sinFicha = usuarioRepo.findAll()
                .stream()
                .filter(u -> !idsConFicha.contains(u.getId()) && u.getEnabled())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse("Usuarios sin ficha de nómina", true, sinFicha));
    }

    /**
     * Crear ficha de nómina para un usuario existente.
     * Body: { usuarioId, puesto, tipoContrato, sueldoBaseDiario, porcentajeComision }
     */
    @PostMapping
    public ResponseEntity<ApiResponse> crearFicha(@RequestBody Map<String, Object> body) {
        Integer usuarioId = (Integer) body.get("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("usuarioId es requerido", false, null));
        }

        Optional<Usuario> usuarioOpt = usuarioRepo.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Usuario no encontrado", false, null));
        }

        if (empleadoRepo.existsByUsuarioId(usuarioId)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Este usuario ya tiene ficha de nómina", false, null));
        }

        EmpleadoFinance emp = new EmpleadoFinance();
        emp.setUsuario(usuarioOpt.get());
        emp.setPuesto((String) body.getOrDefault("puesto", "OPERATIVO"));
        emp.setTipoContrato((String) body.getOrDefault("tipoContrato", "PLANTA"));
        emp.setSueldoBaseDiario(new BigDecimal(body.getOrDefault("sueldoBaseDiario", "0").toString()));
        emp.setPorcentajeComision(new BigDecimal(body.getOrDefault("porcentajeComision", "0.05").toString()));
        emp.setActivo(true);

        EmpleadoFinance saved = empleadoRepo.save(emp);
        return ResponseEntity.ok(new ApiResponse("Ficha de nómina creada", true, saved));
    }

    /**
     * Actualizar ficha de nómina existente.
     * Body: { puesto, tipoContrato, sueldoBaseDiario, porcentajeComision, activo }
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizarFicha(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        Optional<EmpleadoFinance> empOpt = empleadoRepo.findById(id);
        if (empOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EmpleadoFinance emp = empOpt.get();
        if (body.containsKey("puesto"))              emp.setPuesto((String) body.get("puesto"));
        if (body.containsKey("tipoContrato"))         emp.setTipoContrato((String) body.get("tipoContrato"));
        if (body.containsKey("sueldoBaseDiario"))
            emp.setSueldoBaseDiario(new BigDecimal(body.get("sueldoBaseDiario").toString()));
        if (body.containsKey("porcentajeComision"))
            emp.setPorcentajeComision(new BigDecimal(body.get("porcentajeComision").toString()));
        if (body.containsKey("activo"))               emp.setActivo((Boolean) body.get("activo"));

        return ResponseEntity.ok(new ApiResponse("Ficha actualizada", true, empleadoRepo.save(emp)));
    }

    /** Ver ficha de un empleado específico */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtenerFicha(@PathVariable Long id) {
        return empleadoRepo.findById(id)
                .map(e -> ResponseEntity.ok(new ApiResponse("Ficha encontrada", true, e)))
                .orElse(ResponseEntity.notFound().build());
    }
}
