package org.example.fitgymbackend.controller;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.request.UsuarioRequest;
import org.example.fitgymbackend.model.response.UsuarioResponse;
import org.example.fitgymbackend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private IUsuarioService iusuarioservice;

    @PostMapping
    public ResponseEntity<UsuarioResponse> saveTeacher(@RequestBody UsuarioRequest request) {
        UsuarioResponse usuario = iusuarioservice.guardar(request);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllSubjects() {
        List<Usuario> subjects = iusuarioservice.listarTodos();
        return ResponseEntity.ok(subjects);
    }

}
