package com.tdea;
public class Pago {

    public enum MedioPago {
        EFECTIVO(0, "Efectivo"),
        TARJETA_DEBITO(1, "Tarjeta débito"),
        TARJETA_CREDITO(2, "Tarjeta crédito"),
        SEGURO_EPS(3, "Seguro / EPS");

        private int indice;
        private String nombre;

        MedioPago(int indice, String nombre) {
            this.indice = indice;
            this.nombre = nombre;
        }

        public int getIndice() {
            return indice;
        }

        public String getNombre() {
            return nombre;
        }
    }

    private MedioPago medioPago;
    private double subtotal;
    private double descuento;
    private double recargo;
    private double montoTotal;

    public Pago() {
    }

    public Pago(MedioPago medioPago, double subtotal, double descuento, double recargo) {
        this.medioPago = medioPago;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.recargo = recargo;
        this.montoTotal = subtotal - descuento + recargo;

        if (this.montoTotal < 0) {
            this.montoTotal = 0;
        }
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getRecargo() {
        return recargo;
    }

    public void setRecargo(double recargo) {
        this.recargo = recargo;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "medioPago=" + medioPago.getNombre() +
                ", subtotal=" + subtotal +
                ", descuento=" + descuento +
                ", recargo=" + recargo +
                ", montoTotal=" + montoTotal +
                '}';
    }
}