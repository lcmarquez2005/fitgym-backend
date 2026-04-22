package org.example.fitgymbackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private boolean success;
    private Object data; // Aquí puedes meter el usuario guardado, una lista, etc.
}
