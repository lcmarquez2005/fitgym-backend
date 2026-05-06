package org.example.fitgymbackend.modules.finance.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "finance_nomina")
public class Nomina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String periodo; // Ej. "Quincena 1 - Mayo 2026"
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado; // BORRADOR, PAGADA

    @JsonManagedReference("nomina-recibos")
    @OneToMany(mappedBy = "nomina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReciboNomina> recibos = new ArrayList<>();
}
