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

    @Column( name = "last_name", nullable = false, length = 150)
    private String lastName;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Column(name = "huella_digital", nullable = false, length = 255)
    private String huellaDigital;

    @Column( name="no_control", nullable = false, length = 12)
    private String noControl;


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
