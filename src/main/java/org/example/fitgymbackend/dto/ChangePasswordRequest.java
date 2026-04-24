// dto/ChangePasswordRequest.java
package org.example.fitgymbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "La contrasena actual es obligatoria")
    private String currentPassword;

    @NotBlank(message = "La nueva contrasena es obligatoria")
    @Size(min = 6, message = "Minimo 6 caracteres")
    private String newPassword;
}