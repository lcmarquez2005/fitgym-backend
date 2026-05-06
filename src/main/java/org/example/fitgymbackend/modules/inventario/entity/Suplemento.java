package org.example.fitgymbackend.modules.inventario.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "inv_suplemento")
public class Suplemento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String marca;
    private String descripcion;

    @Column(nullable = false)
    private Integer stock = 0;

    private Integer stockMinimo = 5;  // Alerta cuando stock <= stockMinimo

    @Column(precision = 10, scale = 2)
    private BigDecimal precioCompra;   // Costo al proveedor

    @Column(precision = 10, scale = 2)
    private BigDecimal precioVenta;    // Precio al cliente del gym

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    private Boolean activo = true;
}
