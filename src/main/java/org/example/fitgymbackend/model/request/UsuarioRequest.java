package org.example.fitgymbackend.model.request;

import lombok.Data;

@Data
public class UsuarioRequest {

    private String name;
    private String fotoPerfil;
    private String huellaDigital;
    private String rol;
    private String token;

}
