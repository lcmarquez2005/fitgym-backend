package org.example.fitgymbackend.repository;

import org.example.fitgymbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.List;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNoControl(String noControl);
    boolean existsByEmail(String email);
    boolean existsByNoControl(String noControl);

    Optional<Usuario> findByNoControlAndHuellaDigital(String noControl, String huellaDigital);


    //  Para verificación y recuperación
    Optional<Usuario> findByTokenVerificacion(String token);
    Optional<Usuario> findByTokenResetPassword(String token);

    List<Usuario> findByNameContainingIgnoreCase(String name);
    List<Usuario> findByLastNameContainingIgnoreCase(String lastName);
    List<Usuario> findByNoControlContainingIgnoreCase(String noControl);
    List<Usuario> findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNoControlContainingIgnoreCase(
            String name, String lastName, String noControl
    );
}