package org.example.fitgymbackend.model.response;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String lastName;
    private String noControl;
    private String rol;
    private String fotoPerfil;
    private String email;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long id, String name, String lastName,
                        String noControl, String rol, String fotoPerfil, String email) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.noControl = noControl;
        this.rol = rol;
        this.fotoPerfil = fotoPerfil;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNoControl() { return noControl; }
    public void setNoControl(String noControl) { this.noControl = noControl; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}