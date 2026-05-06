package org.example.fitgymbackend.modules.finance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DIOT - Declaración Informativa de Operaciones con Terceros.
 * Un registro por cada proveedor con el que tuvimos operaciones en el mes.
 */
@Data
@Entity
@Table(name = "finance_registro_diot")
public class RegistroDIOT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mesDeclaracion;   // "Mayo 2026"
    private String rfcProveedor;
    private String nombreProveedor;
    private String tipoProveedor;    // NACIONAL, EXTRANJERO
    private BigDecimal montoOperacion;
    private BigDecimal ivaAcreditable;
    private String concepto;         // "Suplementos", "Mantenimiento", "Renta"
}
