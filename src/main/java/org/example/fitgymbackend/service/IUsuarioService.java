package org.example.fitgymbackend.service;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.request.UsuarioRequest;
import org.example.fitgymbackend.model.response.UsuarioResponse;

import java.util.List;

public interface IUsuarioService {
    List<Usuario> listarTodos();
    UsuarioResponse guardar(UsuarioRequest usuario);
    Usuario buscarPorId(Long id);
    void eliminar(Long id);
}
