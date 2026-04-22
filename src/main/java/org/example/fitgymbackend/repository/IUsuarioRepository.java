package org.example.fitgymbackend.repository;

import org.example.fitgymbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {


    // 👇 CAMBIA: De Optional a List (porque puede haber varios)
    List<Usuario> findByNoControl(String noControl);

    // 👇 NUEVO: Para verificar si existe al menos uno
    boolean existsByNoControl(String noControl);

    // 👇 Este sí puede ser Optional porque noControl + huella DEBE ser único
    Optional<Usuario> findByNoControlAndHuellaDigital(String noControl, String huellaDigital);


    // Esto te servirá para el Login más adelante
//    Optional<Usuario> findByEmail(String email);
    // Busca coincidencias parciales (ignora mayúsculas/minúsculas si usas IgnoreCase)
    List<Usuario> findByNameContainingIgnoreCase(String name);

    List<Usuario> findByLastNameContainingIgnoreCase(String lastName);

    List<Usuario> findByNoControlContainingIgnoreCase(String noControl);

    List<Usuario> findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNoControlContainingIgnoreCase(
            String name, String lastName, String noControl
    );
}
