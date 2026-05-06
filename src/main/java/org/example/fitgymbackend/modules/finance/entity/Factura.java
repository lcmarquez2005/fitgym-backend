package org.example.fitgymbackend.modules.finance.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "finance_factura")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rfcCliente;
    private String razonSocial;
    private String usoCfdi;
    
    private String status; // PENDIENTE, TIMBRADA, CANCELADA
    private String folioFiscal; // UUID o ID del PAC
    
    private LocalDateTime fechaEmision;
    private BigDecimal total;
    
    @JsonManagedReference("factura-detalles")
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalles = new ArrayList<>();
}
