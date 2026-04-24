package org.example.fitgymbackend.service;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.request.*;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.AuthResponse;
import org.example.fitgymbackend.repository.IUsuarioRepository;
import org.example.fitgymbackend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    // 📝 REGISTRO
    public ApiResponse register(RegisterRequest request) {
        try {
            // 1. Validar email
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return new ApiResponse("El email es obligatorio", false, null);
            }

            // 2. Verificar email duplicado
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                return new ApiResponse("El email ya está registrado", false, null);
            }

            // 3. Verificar noControl duplicado
            if (usuarioRepository.existsByNoControl(request.getNoControl())) {
                return new ApiResponse("El número de control ya está registrado", false, null);
            }

            // 4. Crear usuario
            Usuario usuario = new Usuario();
            usuario.setName(request.getName());
            usuario.setLastName(request.getLastName());
            usuario.setEmail(request.getEmail().toLowerCase().trim());
            usuario.setNoControl(request.getNoControl());
            usuario.setRol(request.getRol() != null ? request.getRol() : "CLIENTE");
            usuario.setFotoPerfil(request.getFotoPerfil());
            usuario.setHuellaDigital(request.getHuellaDigital());
            usuario.setEmailVerificado(false);

            // 5. Encriptar contraseña
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            // 6. Generar token de verificación
            String verificationToken = UUID.randomUUID().toString();
            usuario.setTokenVerificacion(verificationToken);

            // 7. Auditoría
            usuario.setCreationUser("system");
            usuario.setCreationDate(LocalDateTime.now());

            // 8. Guardar en BD
            Usuario savedUser = usuarioRepository.save(usuario);

            // 9. Enviar email de verificación
            emailService.enviarEmailVerificacion(
                    savedUser.getEmail(),
                    savedUser.getName(),
                    verificationToken
            );

            return new ApiResponse(
                    "Registro exitoso. Revisa tu email para verificar tu cuenta.",
                    true,
                    savedUser
            );

        } catch (Exception e) {
            return new ApiResponse("Error: " + e.getMessage(), false, null);
        }
    }

    // ✅ VERIFICAR EMAIL
    public ApiResponse verificarEmail(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenVerificacion(token);

        if (usuarioOpt.isEmpty()) {
            return new ApiResponse("Token de verificación inválido", false, null);
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setEmailVerificado(true);
        usuario.setTokenVerificacion(null); // Limpiar token
        usuarioRepository.save(usuario);

        return new ApiResponse("Email verificado exitosamente. Ya puedes iniciar sesión.", true, null);
    }

    // 🔐 LOGIN
    public AuthResponse login(LoginRequest request) {
        // 1. Buscar por email (o noControl)
        Usuario usuario;
        if (request.getEmail() != null) {
            usuario = usuarioRepository.findByEmail(request.getEmail().toLowerCase().trim())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        } else if (request.getNoControl() != null) {
            usuario = usuarioRepository.findByNoControl(request.getNoControl())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        } else {
            throw new RuntimeException("Debes proporcionar email o número de control");
        }

        // 2. Verificar email
        if (!usuario.isEmailVerificado()) {
            throw new RuntimeException("Debes verificar tu email antes de iniciar sesión");
        }

        // 3. Verificar contraseña
        if (request.getPassword() == null ||
                !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 4. Generar JWT
        String token = jwtService.generateToken(usuario.getNoControl(), usuario.getRol());

        return new AuthResponse(
                token,                              // String token
                usuario.getId().longValue(),        // Long id
                usuario.getName(),                  // String name
                usuario.getLastName(),             // String lastName
                usuario.getNoControl(),            // String noControl
                usuario.getRol(),                  // String rol
                usuario.getFotoPerfil(),           // String fotoPerfil
                usuario.getEmail()                 // String email
        );
    }

    // 🤔 OLVIDÉ CONTRASEÑA
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail().toLowerCase().trim());

        if (usuarioOpt.isEmpty()) {
            // Por seguridad, no revelamos si el email existe o no
            return new ApiResponse("Si el email está registrado, recibirás un enlace de recuperación", true, null);
        }

        Usuario usuario = usuarioOpt.get();

        // Generar token de recuperación
        String resetToken = UUID.randomUUID().toString();
        usuario.setTokenResetPassword(resetToken);
        usuario.setFechaExpiracionReset(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // Enviar email
        emailService.enviarEmailRecuperacion(usuario.getEmail(), usuario.getName(), resetToken);

        return new ApiResponse("Si el email está registrado, recibirás un enlace de recuperación", true, null);
    }

    //  RESETEAR CONTRASEÑA
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenResetPassword(request.getToken());

        if (usuarioOpt.isEmpty()) {
            return new ApiResponse("Token de recuperación inválido", false, null);
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar expiración
        if (usuario.getFechaExpiracionReset().isBefore(LocalDateTime.now())) {
            return new ApiResponse("El token de recuperación ha expirado", false, null);
        }

        // Cambiar contraseña
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuario.setTokenResetPassword(null);
        usuario.setFechaExpiracionReset(null);
        usuario.setModificationDate(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return new ApiResponse("Contraseña actualizada exitosamente", true, null);
    }

    // 🔍 OBTENER USUARIO DESDE TOKEN
    public AuthResponse getUserFromToken(String token) {
        String noControl = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepository.findByNoControl(noControl)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new AuthResponse(
                token,                              // String token
                usuario.getId().longValue(),        // Long id
                usuario.getName(),                  // String name
                usuario.getLastName(),             // String lastName
                usuario.getNoControl(),            // String noControl
                usuario.getRol(),                  // String rol
                usuario.getFotoPerfil(),           // String fotoPerfil
                usuario.getEmail()                 // String email
        );
    }
}