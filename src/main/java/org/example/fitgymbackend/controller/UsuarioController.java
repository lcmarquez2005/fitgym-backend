package org.example.fitgymbackend.controller;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.request.UsuarioRequest;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.UsuarioResponse;
import org.example.fitgymbackend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    @Autowired
    private IUsuarioService iUsuarioService;

    @PostMapping
    public ResponseEntity<ApiResponse> saveUser(@RequestBody Usuario request) {
        ApiResponse resultado = iUsuarioService.guardar(request);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> subjects = iUsuarioService.listarTodos();
        return ResponseEntity.ok(subjects);
    }

    //Get One User
    @GetMapping("/id")
    public ResponseEntity<UsuarioResponse> getOneUser(@RequestParam("id") Integer id) {
        UsuarioResponse subjects = iUsuarioService.obtenerUsuario(id);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsuarioResponse>> filtrarUsuarios(@RequestParam String q) {
        List<UsuarioResponse> resultados = iUsuarioService.buscarUsuarios(q);
        return ResponseEntity.ok(resultados);
    }
}
