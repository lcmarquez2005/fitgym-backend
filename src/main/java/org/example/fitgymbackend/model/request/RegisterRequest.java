package org.example.fitgymbackend.model.request;

public class RegisterRequest {
    private String name;
    private String lastName;
    private String email;
    private String noControl;
    private String fotoPerfil;
    private String huellaDigital;
    private String rol;
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoControl() { return noControl; }
    public void setNoControl(String noControl) { this.noControl = noControl; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getHuellaDigital() { return huellaDigital; }
    public void setHuellaDigital(String huellaDigital) { this.huellaDigital = huellaDigital; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}