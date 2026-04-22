package org.example.fitgymbackend.model.request;

public class PagoRequest {
    private String nombre;
    private String plan;
    private Double monto;
    private String vigencia;

    // Genera los Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getVigencia() { return vigencia; }
    public void setVigencia(String vigencia) { this.vigencia = vigencia; }
}

