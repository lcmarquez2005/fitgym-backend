package org.example.fitgymbackend.service.imp;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.request.UsuarioRequest;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.UsuarioResponse;
import org.example.fitgymbackend.repository.IUsuarioRepository;
import org.example.fitgymbackend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImp implements IUsuarioService {

    @Autowired
    private IUsuarioRepository iusuarioRepository;

    @Override
    public List<Usuario> listarTodos() {
        return iusuarioRepository.findAll();
    }


    @Override
    public ApiResponse guardar(Usuario request) {
        try {
            request.setCreationUser("system");
            request.setCreationDate(LocalDateTime.now());

            // Guardamos directamente
            Usuario userSaved = iusuarioRepository.save(request);
    //
            return new ApiResponse("Usuario guardado con éxito", true, userSaved);
        } catch (Exception e) {
            return new ApiResponse("Error: " + e.getMessage(), false, null);
        }
    }

    @Override
    public UsuarioResponse obtenerUsuario(Integer id) {
        return iusuarioRepository.findById(id)
                .map( usuario -> {
                    UsuarioResponse usuarioRes = new UsuarioResponse();
                    usuarioRes.setName(usuario.getName());
                    usuarioRes.setLastName(usuario.getLastName());
                    usuarioRes.setNoControl(usuario.getNoControl());
                    usuarioRes.setHuellaDigital(usuario.getHuellaDigital());
                    usuarioRes.setRol(usuario.getRol());
                    usuarioRes.setFotoPerfil(usuario.getFotoPerfil());

                    return usuarioRes;
                }) // O el mapeo que uses
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    @Override
    public List<UsuarioResponse> buscarUsuarios(String termino) {
        // 1. Buscamos en la DB (usamos el método que busca en los 3 campos)
        List<Usuario> usuariosEncontrados = iusuarioRepository
                .findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNoControlContainingIgnoreCase(termino, termino, termino);

        // 2. Convertimos la lista de Entidades a lista de DTOs usando Stream
        return usuariosEncontrados.stream()
                .map(usuario -> {
                    UsuarioResponse res = new UsuarioResponse();
                    res.setName(usuario.getName());
                    res.setLastName(usuario.getLastName());
                    res.setNoControl(usuario.getNoControl());
                    res.setHuellaDigital(usuario.getHuellaDigital());
                    res.setRol(usuario.getRol());
                    res.setFotoPerfil(usuario.getFotoPerfil());
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {

    }


}
