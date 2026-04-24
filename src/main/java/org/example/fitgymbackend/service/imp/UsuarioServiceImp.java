// service/imp/UsuarioServiceImp.java
package org.example.fitgymbackend.service.imp;

import org.example.fitgymbackend.dto.*;
import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.UsuarioResponse;
import org.example.fitgymbackend.repository.IUsuarioRepository;
import org.example.fitgymbackend.security.JwtUtil;
import org.example.fitgymbackend.service.EmailService;
import org.example.fitgymbackend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImp implements IUsuarioService {

    @Autowired
    private IUsuarioRepository iusuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    // ============================================
    // MÉTODOS ANTIGUOS (MANTENLOS IGUAL)
    // ============================================

    @Override
    public List<Usuario> listarTodos() {
        return iusuarioRepository.findAll();
    }

    @Override
    public ApiResponse guardar(Usuario request) {
        try {
            request.setCreationUser("system");
            request.setCreationDate(LocalDateTime.now());
            Usuario userSaved = iusuarioRepository.save(request);
            return new ApiResponse("Usuario guardado con exito", true, userSaved);
        } catch (Exception e) {
            return new ApiResponse("Error: " + e.getMessage(), false, null);
        }
    }

    @Override
    public UsuarioResponse obtenerUsuario(Integer id) {
        return iusuarioRepository.findById(id)
                .map(usuario -> {
                    UsuarioResponse usuarioRes = new UsuarioResponse();
                    usuarioRes.setName(usuario.getName());
                    usuarioRes.setLastName(usuario.getLastName());
                    usuarioRes.setNoControl(usuario.getNoControl());
                    usuarioRes.setHuellaDigital(usuario.getHuellaDigital());
                    usuarioRes.setRol(usuario.getRol());
                    usuarioRes.setFotoPerfil(usuario.getFotoPerfil());
                    return usuarioRes;
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public List<UsuarioResponse> buscarUsuarios(String termino) {
        List<Usuario> usuariosEncontrados = iusuarioRepository
                .findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNoControlContainingIgnoreCase(termino, termino, termino);

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
        // TODO: Implementar
    }

    // ============================================
    // NUEVOS MÉTODOS DE AUTENTICACIÓN
    // ============================================

    @Override
    @Transactional
    public ApiResponse register(RegisterRequest request) {
        // 1. Verificar email duplicado
        if (iusuarioRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse("El email ya esta registrado", false, null);
        }

        // 2. Crear usuario
        Usuario usuario = new Usuario();
        usuario.setName(request.getFullName());
        usuario.setLastName("");
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol("USER");
        usuario.setEnabled(false);
        usuario.setNoControl("PENDIENTE");

        // 👇 NUEVAS LÍNEAS (arreglan el error)
        usuario.setFotoPerfil("");           // String vacío
        usuario.setHuellaDigital("PENDIENTE"); // Valor temporal

        usuario.setCreationUser("system");
        usuario.setCreationDate(LocalDateTime.now());

        // 3. Generar token de verificacion
        String verificationToken = UUID.randomUUID().toString();
        usuario.setResetToken(verificationToken);
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        // 4. Guardar
        Usuario savedUser = iusuarioRepository.save(usuario);

        // 5. Enviar email
        try {
            emailService.sendVerificationEmail(usuario.getEmail(), verificationToken);
        } catch (Exception e) {
            iusuarioRepository.delete(savedUser);
            return new ApiResponse("Error al enviar email de verificacion", false, null);
        }

        return new ApiResponse("Registro exitoso. Revisa tu email para verificar la cuenta.", true, null);
    }

    @Override
    @Transactional
    public ApiResponse verifyEmail(String token) {
        Optional<Usuario> userOpt = iusuarioRepository.findByResetToken(token);

        if (userOpt.isEmpty()) {
            return new ApiResponse("Token invalido", false, null);
        }

        Usuario usuario = userOpt.get();

        if (usuario.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return new ApiResponse("Token expirado. Solicita uno nuevo.", false, null);
        }

        usuario.setEnabled(true);
        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        iusuarioRepository.save(usuario);

        return new ApiResponse("Email verificado exitosamente. Ya puedes iniciar sesion.", true, null);
    }

    @Override
    public ApiResponse login(LoginRequest request) {
        Optional<Usuario> userOpt = iusuarioRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return new ApiResponse("Credenciales invalidas", false, null);
        }

        Usuario usuario = userOpt.get();

        if (!usuario.getEnabled()) {
            return new ApiResponse("Cuenta no verificada. Revisa tu email.", false, null);
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return new ApiResponse("Credenciales invalidas", false, null);
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", mapUserToDTO(usuario));

        return new ApiResponse("Login exitoso", true, data);
    }

    @Override
    @Transactional
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        Optional<Usuario> userOpt = iusuarioRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return new ApiResponse("Si el email existe, recibiras instrucciones.", true, null);
        }

        Usuario usuario = userOpt.get();
        String resetToken = jwtUtil.generateResetToken(usuario.getEmail());
        usuario.setResetToken(resetToken);
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        iusuarioRepository.save(usuario);

        try {
            emailService.sendPasswordResetEmail(usuario.getEmail(), resetToken);
        } catch (Exception e) {
            return new ApiResponse("Error al enviar email de recuperacion", false, null);
        }

        return new ApiResponse("Si el email existe, recibiras instrucciones.", true, null);
    }

    @Override
    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        if (!jwtUtil.validateToken(request.getToken())) {
            return new ApiResponse("Token invalido o expirado", false, null);
        }

        String email = jwtUtil.extractEmail(request.getToken());
        Optional<Usuario> userOpt = iusuarioRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new ApiResponse("Usuario no encontrado", false, null);
        }

        Usuario usuario = userOpt.get();

        if (!request.getToken().equals(usuario.getResetToken())) {
            return new ApiResponse("Token invalido", false, null);
        }

        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        iusuarioRepository.save(usuario);

        return new ApiResponse("Contrasena actualizada exitosamente", true, null);
    }

    @Override
    @Transactional
    public ApiResponse changePassword(String email, ChangePasswordRequest request) {
        // 1. Buscar usuario por email
        Optional<Usuario> userOpt = iusuarioRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new ApiResponse("Usuario no encontrado", false, null);
        }

        Usuario usuario = userOpt.get();

        // 2. Verificar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getPassword())) {
            return new ApiResponse("La contrasena actual es incorrecta", false, null);
        }

        // 3. Verificar que la nueva sea diferente
        if (passwordEncoder.matches(request.getNewPassword(), usuario.getPassword())) {
            return new ApiResponse("La nueva contrasena no puede ser igual a la actual", false, null);
        }

        // 4. Cambiar contraseña
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        iusuarioRepository.save(usuario);

        return new ApiResponse("Contrasena actualizada exitosamente", true, null);
    }

    @Override
    @Transactional
    public ApiResponse deleteAccount(String email, String password) {
        // 1. Buscar usuario
        Optional<Usuario> userOpt = iusuarioRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new ApiResponse("Usuario no encontrado", false, null);
        }

        Usuario usuario = userOpt.get();

        // 2. Verificar contraseña antes de eliminar (seguridad)
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return new ApiResponse("Contrasena incorrecta. No se puede eliminar la cuenta.", false, null);
        }

        // 3. Eliminar usuario
        iusuarioRepository.delete(usuario);

        // 4. Enviar email de despedida (opcional)
        try {
            emailService.sendGoodbyeEmail(usuario.getEmail(), usuario.getName());
        } catch (Exception e) {
            // No pasa nada si falla el email
        }

        return new ApiResponse("Cuenta eliminada exitosamente. Te extranaremos!", true, null);
    }

    @Override
    public ApiResponse getProfile(String email) {
        Optional<Usuario> userOpt = iusuarioRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new ApiResponse("Usuario no encontrado", false, null);
        }

        return new ApiResponse("Perfil obtenido", true, mapUserToDTO(userOpt.get()));
    }

    private Map<String, Object> mapUserToDTO(Usuario usuario) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", usuario.getId());
        dto.put("name", usuario.getName());
        dto.put("email", usuario.getEmail());
        dto.put("rol", usuario.getRol());
        dto.put("enabled", usuario.getEnabled());
        return dto;
    }
}