package org.example.fitgymbackend.service;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.request.UsuarioRequest;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.UsuarioResponse;

import java.util.List;

public interface IUsuarioService {
    List<Usuario> listarTodos();
    ApiResponse guardar(Usuario usuario);
    UsuarioResponse obtenerUsuario(Integer id);
    List<UsuarioResponse> buscarUsuarios(String texto);
    void eliminar(Long id);
}
