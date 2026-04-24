// repository/IUsuarioRepository.java
package org.example.fitgymbackend.repository;

import org.example.fitgymbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Métodos antiguos (mantenlos)
    List<Usuario> findByNameContainingIgnoreCase(String name);
    List<Usuario> findByLastNameContainingIgnoreCase(String lastName);
    List<Usuario> findByNoControlContainingIgnoreCase(String noControl);
    List<Usuario> findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNoControlContainingIgnoreCase(
            String name, String lastName, String noControl
    );

    // 👇 NUEVOS MÉTODOS PARA AUTH
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByResetToken(String resetToken);
    boolean existsByEmail(String email);
    Optional<Usuario> findByHuellaDigital(String huellaDigital);
}