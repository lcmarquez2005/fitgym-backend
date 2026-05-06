package org.example.fitgymbackend.modules.finance.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "finance_recibo_nomina")
public class ReciboNomina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("nomina-recibos")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomina_id")
    private Nomina nomina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private EmpleadoFinance empleado;

    private BigDecimal totalPercepciones = BigDecimal.ZERO;
    private BigDecimal totalDeducciones = BigDecimal.ZERO;
    private BigDecimal netoAPagar = BigDecimal.ZERO;

    @JsonManagedReference("recibo-detalles")
    @OneToMany(mappedBy = "reciboNomina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRecibo> detalles = new ArrayList<>();
}
