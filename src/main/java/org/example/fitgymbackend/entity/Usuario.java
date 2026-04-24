package org.example.fitgymbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Column(name = "huella_digital", length = 255)
    private String huellaDigital;

    @Column(name = "no_control", nullable = false, length = 12, unique = true)
    private String noControl;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "email_verificado")
    private boolean emailVerificado = false;

    @Column(name = "token_verificacion", length = 255)
    private String tokenVerificacion;

    @Column(name = "token_reset_password", length = 255)
    private String tokenResetPassword;

    @Column(name = "fecha_expiracion_reset")
    private LocalDateTime fechaExpiracionReset;

    @Transient
    private String token;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "creation_user", length = 150)
    private String creationUser;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @Column(name = "modification_user", length = 150)
    private String modificationUser;

    // Constructor vacío
    public Usuario() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getHuellaDigital() { return huellaDigital; }
    public void setHuellaDigital(String huellaDigital) { this.huellaDigital = huellaDigital; }

    public String getNoControl() { return noControl; }
    public void setNoControl(String noControl) { this.noControl = noControl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEmailVerificado() { return emailVerificado; }
    public void setEmailVerificado(boolean emailVerificado) { this.emailVerificado = emailVerificado; }

    public String getTokenVerificacion() { return tokenVerificacion; }
    public void setTokenVerificacion(String tokenVerificacion) { this.tokenVerificacion = tokenVerificacion; }

    public String getTokenResetPassword() { return tokenResetPassword; }
    public void setTokenResetPassword(String tokenResetPassword) { this.tokenResetPassword = tokenResetPassword; }

    public LocalDateTime getFechaExpiracionReset() { return fechaExpiracionReset; }
    public void setFechaExpiracionReset(LocalDateTime fechaExpiracionReset) { this.fechaExpiracionReset = fechaExpiracionReset; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public String getCreationUser() { return creationUser; }
    public void setCreationUser(String creationUser) { this.creationUser = creationUser; }

    public LocalDateTime getModificationDate() { return modificationDate; }
    public void setModificationDate(LocalDateTime modificationDate) { this.modificationDate = modificationDate; }

    public String getModificationUser() { return modificationUser; }
    public void setModificationUser(String modificationUser) { this.modificationUser = modificationUser; }
}