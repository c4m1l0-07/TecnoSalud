package com.tdea;
public class ItemServicio {

    private Servicio servicio;
    private int cantidad;
    private double valorUnitario;
    private double montoFinal;

    public ItemServicio() {
    }

    public ItemServicio(Servicio servicio, int cantidad) {
        this(servicio, cantidad, servicio.getPrecio());
    }

    public ItemServicio(Servicio servicio, int cantidad, double valorUnitario) {
        this.servicio = servicio;
        this.cantidad = cantidad;
        this.valorUnitario = valorUnitario;
        this.montoFinal = valorUnitario * cantidad;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.montoFinal = this.valorUnitario * cantidad;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
        this.montoFinal = this.valorUnitario * cantidad;
    }

    public double getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(double montoFinal) {
        this.montoFinal = montoFinal;
    }

    @Override
    public String toString() {
        return servicio.getNombre() + " x" + cantidad + " = $" + montoFinal;
    }
}