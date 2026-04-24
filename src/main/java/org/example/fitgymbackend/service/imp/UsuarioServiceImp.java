package org.example.fitgymbackend.service.imp;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.request.UsuarioRequest;
import org.example.fitgymbackend.model.response.ApiResponse;
import org.example.fitgymbackend.model.response.UsuarioResponse;
import org.example.fitgymbackend.repository.IUsuarioRepository;
import org.example.fitgymbackend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.example.fitgymbackend.model.response.LoginResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class UsuarioServiceImp implements IUsuarioService {

    @Autowired
    private IUsuarioRepository iusuarioRepository;

    @Override
    public List<Usuario> listarTodos() {
        return iusuarioRepository.findAll();
    }


    @Override
    public ApiResponse guardar(Usuario request) {
        try {
            // Validación básica
            if (request.getNoControl() == null || request.getNoControl().isEmpty()) {
                return new ApiResponse("El número de control es obligatorio", false, null);
            }

            // Si no viene creation_user, ponemos 'system'
            if (request.getCreationUser() == null) {
                request.setCreationUser("system");
            }

            // Fecha automática
            request.setCreationDate(LocalDateTime.now());

            Usuario userSaved = iusuarioRepository.save(request);
            return new ApiResponse("Usuario guardado con éxito", true, userSaved);

        } catch (Exception e) {
            return new ApiResponse("Error en el servidor: " + e.getMessage(), false, null);
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
                }) // O el mapeo que uses
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public List<UsuarioResponse> buscarUsuarios(String termino) {
        // 1. Buscamos en la DB (usamos el método que busca en los 3 campos)
        List<Usuario> usuariosEncontrados = iusuarioRepository
                .findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNoControlContainingIgnoreCase(termino, termino, termino);

        // 2. Convertimos la lista de Entidades a lista de DTOs usando Stream
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

//    @Override
//    public void eliminar(Long id) {
//
//    }


    @Override
    public LoginResponse login(String noControl, String huellaDigital) {
        // 1. Buscar usuario por noControl y huella
        Optional<Usuario> usuarioOpt = iusuarioRepository.findByNoControlAndHuellaDigital(noControl, huellaDigital);

        // 2. Si existe, login exitoso
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            UsuarioResponse userResponse = new UsuarioResponse();
            userResponse.setName(usuario.getName());
            userResponse.setLastName(usuario.getLastName());
            userResponse.setNoControl(usuario.getNoControl());
            userResponse.setHuellaDigital(usuario.getHuellaDigital());
            userResponse.setRol(usuario.getRol());
            userResponse.setFotoPerfil(usuario.getFotoPerfil());

            return new LoginResponse(true, "Login exitoso. ¡Bienvenido " + usuario.getName() + "!", userResponse);
        }

        // 3. Si no existe, verificar si el noControl existe (para dar mejor mensaje)
        // 👇 CAMBIO: Ahora usamos existsByNoControl
        boolean existeNoControl = iusuarioRepository.existsByNoControl(noControl);

        if (existeNoControl) {
            return new LoginResponse(false, "Huella digital incorrecta", null);
        } else {
            return new LoginResponse(false, "Número de control no encontrado", null);
        }
    }

    //mapeo de usuarioRquest en usuario
    private Usuario mapeoUsuarioRequest(UsuarioRequest usuario) {
        Usuario usuarioReq = new Usuario();
        usuarioReq.setName(usuario.getName());
        usuarioReq.setLastName(usuario.getLastName());
        usuarioReq.setNoControl(usuario.getNoControl());
        usuarioReq.setHuellaDigital(usuario.getHuellaDigital());
        usuarioReq.setRol(usuario.getRol());
        usuarioReq.setFotoPerfil(usuario.getFotoPerfil());
        return usuarioReq;
    }

    //mapeo de usuarioResponse en usuario
    private UsuarioResponse mapeoUsuarioResponse(Usuario usuario) {
        UsuarioResponse usuarioRes = new UsuarioResponse();
        usuarioRes.setName(usuario.getName());
        usuarioRes.setLastName(usuario.getLastName());
        usuarioRes.setNoControl(usuario.getNoControl());
        usuarioRes.setHuellaDigital(usuario.getHuellaDigital());
        usuarioRes.setRol(usuario.getRol());
        usuarioRes.setFotoPerfil(usuario.getFotoPerfil());
        return usuarioRes;
    }

    @Override
    public ApiResponse editarUsuario(Integer id, UsuarioRequest nuevo) {
        iusuarioRepository.findById(id)
                .map(usuarioOld -> {
                    Usuario nuevoUser = mapeoUsuarioRequest(nuevo);
                    // Agregamos el id para que JPA no cree un usuario nuevo sino que haga update
                    nuevoUser.setId(id);
                    Usuario actualizado = iusuarioRepository.save(nuevoUser);

                    return new ApiResponse("Usuario guardado con éxito", true, actualizado);
                })
                // Si el findById no encuentra nada, entra aquí:
                .orElse(new ApiResponse("Error: Usuario con ID " + id + " no existe", false, null));
    }

    @Override
    public ApiResponse eliminar(Integer id) {
        iusuarioRepository.findById(id)
                .map(usuarioOld -> {
                    Usuario nuevoUser = mapeoUsuarioRequest(nuevo);
                    // Agregamos el id para que JPA no cree un usuario nuevo sino que haga update
                    nuevoUser.setId(id);
                    Usuario actualizado = iusuarioRepository.save(nuevoUser);

                    return new ApiResponse("Usuario guardado con éxito", true, actualizado);
                })
                // Si el findById no encuentra nada, entra aquí:
                .orElse(new ApiResponse("Error: Usuario con ID " + id + " no existe", false, null));
    }
}
