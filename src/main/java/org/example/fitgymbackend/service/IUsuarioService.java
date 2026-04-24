// service/IUsuarioService.java
package org.example.fitgymbackend.service;

import org.example.fitgymbackend.dto.LoginRequest;
import org.example.fitgymbackend.dto.RegisterRequest;
import org.example.fitgymbackend.dto.ResetPasswordRequest;
import org.example.fitgymbackend.dto.ForgotPasswordRequest;
import org.example.fitgymbackend.dto.ChangePasswordRequest;
import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.UsuarioResponse;

import java.util.List;

public interface IUsuarioService {
    // Métodos antiguos
    List<Usuario> listarTodos();
    ApiResponse guardar(Usuario usuario);
    UsuarioResponse obtenerUsuario(Integer id);
    List<UsuarioResponse> buscarUsuarios(String texto);
    void eliminar(Long id);

    // 👇 NUEVOS MÉTODOS DE AUTH
    ApiResponse register(RegisterRequest request);
    ApiResponse verifyEmail(String token);
    ApiResponse login(LoginRequest request);
    ApiResponse forgotPassword(ForgotPasswordRequest request);
    ApiResponse resetPassword(ResetPasswordRequest request);
    ApiResponse getProfile(String email);
    ApiResponse changePassword(String email, ChangePasswordRequest request);
    ApiResponse deleteAccount(String email, String password);
}