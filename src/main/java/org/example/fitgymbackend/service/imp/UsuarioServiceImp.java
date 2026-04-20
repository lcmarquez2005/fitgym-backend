package org.example.fitgymbackend.service.imp;

import org.example.fitgymbackend.entity.Usuario;
import org.example.fitgymbackend.model.request.UsuarioRequest;
import org.example.fitgymbackend.model.response.UsuarioResponse;
import org.example.fitgymbackend.repository.IUsuarioRepository;
import org.example.fitgymbackend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioServiceImp implements IUsuarioService {

    @Autowired
    private IUsuarioRepository iusuarioRepository;

    @Override
    public List<Usuario> listarTodos() {
        return iusuarioRepository.findAll();
    }


    @Override
    public UsuarioResponse guardar(UsuarioRequest request) {

        Usuario user = new Usuario();
        user.setName(request.getName());
        user.setFotoPerfil(request.getFotoPerfil());
        user.setHuellaDigital(request.getHuellaDigital());
        user.setRol(request.getRol());
        user.setCreationUser("system");
        user.setCreationDate(LocalDateTime.now());
//
        Usuario subjectSaved = iusuarioRepository.save(user);
//
        UsuarioResponse response = new UsuarioResponse();
        response.setHuellaDigital(subjectSaved.getHuellaDigital());
        response.setName(subjectSaved.getName());
        response.setRol(subjectSaved.getRol());

        return response;
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return null;
    }

    @Override
    public void eliminar(Long id) {

    }


}
