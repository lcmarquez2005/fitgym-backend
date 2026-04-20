package org.example.fitgymbackend.repository;

import org.example.fitgymbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    // Esto te servirá para el Login más adelante
//    Optional<Usuario> findByEmail(String email);
}
