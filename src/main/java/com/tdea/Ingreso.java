package com.tdea;
import java.time.LocalDateTime;

public class Ingreso {

    private static int contador = 1;

    private int id;

    private Paciente paciente;
    private Consultorio consultorio;

    private ListaEnlazada<ItemServicio> servicios;

    private Pago pago;

    private boolean pagado;
    private boolean cancelado;

    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaAtencion;

    public Ingreso(Paciente paciente) {

        this.id = contador++;
        this.paciente = paciente;

        this.servicios = new ListaEnlazada<>();

        this.fechaIngreso = LocalDateTime.now();

        this.pagado = false;
        this.cancelado = false;
    }

    public int getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Consultorio getConsultorio() {
        return consultorio;
    }

    public void setConsultorio(Consultorio consultorio) {
        this.consultorio = consultorio;
    }

    public ListaEnlazada<ItemServicio> getServicios() {
        return servicios;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
        this.pagado = true;
    }

    public boolean isPagado() {
        return pagado;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void cancelar() {
        this.cancelado = true;
    }

    public void agregarServicio(ItemServicio item) {
        servicios.insertar(item);
    }

    public double calcularSubtotal() {

        final double[] total = {0};

        servicios.recorrer(item -> {
            total[0] += item.getMontoFinal();
        });

        return total[0];
    }

    public double getTotalDescuentos() {

        double subtotal = calcularSubtotal();

        int edad = paciente.getEdad();

        if (paciente.getEps() != null &&
                !paciente.getEps().trim().isEmpty()) {

            return subtotal;
        }

        if (edad < 5) {
            return subtotal * 0.20;
        }

        if (edad >= 65) {
            return subtotal * 0.30;
        }

        return 0;
    }

    public boolean requirioHospitalizacion() {

        if (consultorio == null) {
            return false;
        }

        return consultorio.getTipo()
                .equalsIgnoreCase("hospitalizacion");
    }

    public long calcularMinutosEspera() {

        if (fechaAtencion == null) {
            return 0;
        }

        return java.time.Duration
                .between(fechaIngreso, fechaAtencion)
                .toMinutes();
    }

    public void marcarAtendido() {
        fechaAtencion = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Ingreso{" +
                "id=" + id +
                ", paciente=" + paciente.getNombre() +
                ", consultorio=" +
                (consultorio != null ?
                        consultorio.getNumero() : "Sin asignar") +
                ", pagado=" + pagado +
                ", cancelado=" + cancelado +
                '}';
    }
}
