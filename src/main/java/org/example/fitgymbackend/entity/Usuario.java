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
    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "foto_perfil", nullable = false, length = 100)
    private String fotoPerfil;

    @Column(name = "huella_digital", nullable = false, length = 100)
    private String huellaDigital;

    @Transient // Esto evita que se cree una columna en la tabla
    private String token;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "creation_user", length = 150)
    private String creationUser;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @Column(name = "modification_user", length = 150)
    private String modificationUser;

}
