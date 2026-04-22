package org.example.fitgymbackend.model.response;

public class TicketResponse {
    private Integer id;
    private String nombre;
    private String plan;
    private Double monto;
    private String vigencia;

    // Constructor vacío
    public TicketResponse() {}

    // Constructor con parámetros
    public TicketResponse(Integer id, String nombre, String plan, Double monto, String vigencia) {
        this.id = id;
        this.nombre = nombre;
        this.plan = plan;
        this.monto = monto;
        this.vigencia = vigencia;
    }

    // Genera los Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getVigencia() { return vigencia; }
    public void setVigencia(String vigencia) { this.vigencia = vigencia; }
}