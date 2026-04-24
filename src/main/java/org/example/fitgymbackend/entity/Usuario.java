// entity/Usuario.java
package org.example.fitgymbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    // CAMPOS DE AUTH
    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Column(name = "huella_digital", length = 255)
    private String huellaDigital;

    @Column(name = "no_control", nullable = false, length = 12)
    private String noControl;

    @Column(name = "reset_token", length = 500)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Transient
    private String token;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "creation_user", length = 150)
    private String creationUser;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @Column(name = "modification_user", length = 150)
    private String modificationUser;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (enabled == null) {
            enabled = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        modificationDate = LocalDateTime.now();
    }
}