package org.example.fitgymbackend.model.request;

public class LoginRequest {
    private String email;
    private String noControl;
    private String password;
    private String huellaDigital;  // 👈 AÑADE ESTO

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoControl() { return noControl; }
    public void setNoControl(String noControl) { this.noControl = noControl; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getHuellaDigital() { return huellaDigital; }        // 👈 AÑADE ESTO
    public void setHuellaDigital(String huellaDigital) { this.huellaDigital = huellaDigital; }  // 👈 Y ESTO
}