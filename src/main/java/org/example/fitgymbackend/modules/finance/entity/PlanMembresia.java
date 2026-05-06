package org.example.fitgymbackend.modules.finance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "finance_plan_membresia")
public class PlanMembresia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer duracionMeses;
    private BigDecimal precio;
    private String beneficios;
    
    private Boolean activo = true;
}
