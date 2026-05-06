package org.example.fitgymbackend.modules.finance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.example.fitgymbackend.entity.Usuario;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "finance_empleado")
public class EmpleadoFinance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación real con la tabla de usuarios del ERP
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    @JsonIgnoreProperties({"password", "resetToken", "resetTokenExpiry", "huellaDigital"})
    private Usuario usuario;

    // Datos laborales (no van en Usuario porque son datos de RR.HH.)
    private String puesto;        // ADMINISTRATIVO, ENTRENADOR, VENDEDOR, OPERATIVO
    private String tipoContrato;  // PLANTA, FREELANCE, TEMPORAL

    private BigDecimal sueldoBaseDiario;

    // Porcentaje de comisión sobre ventas en caja (ej. 0.05 = 5%)
    @Column(precision = 5, scale = 4)
    private BigDecimal porcentajeComision;

    private Boolean activo = true;
}
