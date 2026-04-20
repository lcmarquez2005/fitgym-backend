package org.example.fitgymbackend.model.response;

import lombok.Data;

@Data
public class UsuarioResponse {
    private String name;
    private String fotoPerfil;
    private String huellaDigital;
    private String rol;
    private String token;
}
