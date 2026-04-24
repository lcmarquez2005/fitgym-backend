package org.example.fitgymbackend.controller;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.UsuarioResponse;
import org.example.fitgymbackend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    @Autowired
    private IUsuarioService iUsuarioService;

    @PostMapping
    public ResponseEntity<ApiResponse> saveUser(@RequestBody Usuario request) {
        ApiResponse resultado = iUsuarioService.guardar(request);

        if (resultado.isSuccess()) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.badRequest().body(resultado);
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> users = iUsuarioService.listarTodos();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/id")
    public ResponseEntity<UsuarioResponse> getOneUser(@RequestParam("id") Integer id) {
        UsuarioResponse user = iUsuarioService.obtenerUsuario(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsuarioResponse>> filtrarUsuarios(@RequestParam String q) {
        List<UsuarioResponse> resultados = iUsuarioService.buscarUsuarios(q);
        return ResponseEntity.ok(resultados);
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<Map<String, String>> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El archivo esta vacio");
                return ResponseEntity.badRequest().body(error);
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Solo se permiten imagenes");
                return ResponseEntity.badRequest().body(error);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;

            String uploadDir = System.getProperty("user.dir") + "/uploads/fotos/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/fotos/" + fileName;

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("message", "Foto subida exitosamente");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al guardar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}