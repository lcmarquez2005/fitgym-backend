package org.example.fitgymbackend.modules.finance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMembresiasDTO {
    private String mesAnio;
    
    private Integer totalSocios;
    private Integer activas;
    private Integer inactivasOVencidas;
    private Integer nuevasDelMes;
    
    private Map<String, Integer> distribucionPorTipo;
}
