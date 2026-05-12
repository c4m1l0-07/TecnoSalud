package com.tdea;


public class SistemaClinica {

    private ListaEnlazada<Paciente> listaPacientes;

    private Cola<Paciente> colaAtencion;

    private Cola<Paciente> colaPreoperatoria;

    private ListaEnlazada<Ingreso> historialIngresos;

    private ListaEnlazada<Consultorio> habitaciones;

    private Pila<String> pilaServicios;

    private ListaEnlazada<Servicio> catalogoServicios;

    private int[] contadorPorMedio;

    private double[] montoPorMedio;

    public SistemaClinica() {

        listaPacientes = new ListaEnlazada<>();

        colaAtencion = new Cola<>();

        colaPreoperatoria = new Cola<>(3);

        historialIngresos = new ListaEnlazada<>();

        habitaciones = new ListaEnlazada<>();

        pilaServicios = new Pila<>();

        catalogoServicios = new ListaEnlazada<>();

        contadorPorMedio = new int[4];

        montoPorMedio = new double[4];

        cargarConsultorios();
        cargarServicios();
    }

    private void cargarConsultorios() {

        habitaciones.insertar(
                new Consultorio(1,
                        "consulta externa",
                        "disponible"));

        habitaciones.insertar(
                new Consultorio(2,
                        "observacion",
                        "disponible"));

        habitaciones.insertar(
                new Consultorio(3,
                        "hospitalizacion",
                        "disponible"));

        habitaciones.insertar(
                new Consultorio(4,
                        "quirofano",
                        "disponible"));
    }

    private void cargarServicios() {

        catalogoServicios.insertar(new Servicio(1L,
                "Consulta general",
                70000));

        catalogoServicios.insertar(new Servicio(2L,
                "Consulta especializada",
                150000));

        catalogoServicios.insertar(new Servicio(3L,
                "Exámenes de laboratorio - paquete básico",
                40000));

        catalogoServicios.insertar(new Servicio(4L,
                "Imagenología - ecografía",
                120000));

        catalogoServicios.insertar(new Servicio(5L,
                "Rayos X",
                60000));

        catalogoServicios.insertar(new Servicio(6L,
                "Hospitalización por día - habitación estándar",
                200000));

        catalogoServicios.insertar(new Servicio(7L,
                "Cirugía menor - tarifa base",
                1200000));

        catalogoServicios.insertar(new Servicio(8L,
                "Cirugía mayor - tarifa base",
                6000000));

        catalogoServicios.insertar(new Servicio(9L,
                "Vacunación por dosis",
                30000));

        catalogoServicios.insertar(new Servicio(10L,
                "Ambulancia / traslado",
                180000));

        catalogoServicios.insertar(new Servicio(11L,
                "Observación por día",
                120000));

        catalogoServicios.insertar(new Servicio(12L,
                "Farmacia / medicamentos (variable - usar SRV012)",
                0));
    }

    public String registrarPaciente(
            String documento,
            String nombre,
            int edad,
            String genero,
            String telefono,
            String eps,
            String sangre) {

        if (buscarPaciente(documento) != null) {
            return "Paciente ya registrado.";
        }

        Paciente p = new Paciente(
                documento,
                nombre,
                edad,
                genero,
                telefono,
                eps,
                sangre);

        listaPacientes.insertar(p);

        return "Paciente registrado correctamente.";
    }

    public Paciente buscarPaciente(String documento) {

        final Paciente[] encontrado = {null};

        listaPacientes.recorrer(p -> {

            if (p.getDocumento()
                    .equalsIgnoreCase(documento)) {

                encontrado[0] = p;
            }
        });

        return encontrado[0];
    }

    public String agregarAColaAtencion(String documento) {

        Paciente p = buscarPaciente(documento);

        if (p == null) {
            return "Paciente no encontrado.";
        }

        colaAtencion.insertar(p);

        return "Paciente agregado a cola.";
    }

    public String agregarAColaPreoperatoria(String documento) {

        Paciente p = buscarPaciente(documento);

        if (p == null) {
            return "Paciente no encontrado.";
        }

        if (!colaPreoperatoria.insertar(p)) {
            return "Cola preoperatoria llena.";
        }

        return "Paciente agregado.";
    }

    public Ingreso atenderSiguienteDeAtencion() {

        if (colaAtencion.estaVacia()) {
            return null;
        }

        Paciente p = colaAtencion.eliminar();

        Ingreso ingreso = new Ingreso(p);

        ingreso.marcarAtendido();

        historialIngresos.insertar(ingreso);

        return ingreso;
    }

    public Ingreso atenderSiguientePreoperatorio() {

        if (colaPreoperatoria.estaVacia()) {
            return null;
        }

        Paciente p = colaPreoperatoria.eliminar();

        Ingreso ingreso = new Ingreso(p);

        ingreso.marcarAtendido();

        historialIngresos.insertar(ingreso);

        return ingreso;
    }

    public String asignarHabitacion(
            String documento,
            int numeroSala) {

        Ingreso ingreso = buscarIngresoActivo(documento);

        if (ingreso == null) {
            return "El paciente no tiene ingreso activo.";
        }

        final Consultorio[] consultorio = {null};

        habitaciones.recorrer(c -> {

            if (c.getNumero() == numeroSala) {
                consultorio[0] = c;
            }
        });

        if (consultorio[0] == null) {
            return "Sala no encontrada.";
        }

        if (!consultorio[0].estaDisponible()) {
            return "Sala ocupada.";
        }

        consultorio[0].setEstado("ocupado");

        ingreso.setConsultorio(consultorio[0]);

        return "Sala asignada correctamente.";
    }

    public String liberarHabitacion(int numeroSala) {

        final boolean[] encontrada = {false};

        habitaciones.recorrer(c -> {

            if (c.getNumero() == numeroSala) {

                c.setEstado("disponible");

                encontrada[0] = true;
            }
        });

        if (encontrada[0]) {
            return "Sala liberada.";
        }

        return "Sala no encontrada.";
    }

    public String cancelarIngreso(String documento) {

        Ingreso ingreso = buscarIngresoActivo(documento);

        if (ingreso == null) {
            return "No existe ingreso activo.";
        }

        if (ingreso.getConsultorio() != null) {
            ingreso.getConsultorio().setEstado("disponible");
        }

        ingreso.cancelar();

        return "Ingreso cancelado.";
    }

    public String agregarServicio(
            String documento,
            String codigoServicio,
            int cantidad,
            double montoEspecial) {

        Ingreso ingreso = buscarIngresoActivo(documento);

        if (ingreso == null) {
            return "El paciente no tiene ingreso activo.";
        }

        if (cantidad <= 0) {
            return "La cantidad debe ser mayor que cero.";
        }

        Servicio servicio = buscarServicioPorCodigo(codigoServicio);

        if (servicio == null) {
            return "Servicio no encontrado en el catálogo.";
        }

        double valorUnitario = servicio.getPrecio();

        if (servicio.getCodigo().equals(12L)) {

            if (montoEspecial <= 0) {
                return "Para farmacia debe ingresar el código SRV012 y un monto mayor que cero.";
            }

            valorUnitario = montoEspecial;
        }

        ItemServicio item = new ItemServicio(
                servicio,
                cantidad,
                valorUnitario);

        ingreso.agregarServicio(item);

        pilaServicios.insertar(
                servicio.getNombre()
                        + " x" + cantidad
                        + " | Paciente: "
                        + ingreso.getPaciente().getNombre());

        return "Servicio agregado correctamente. Subtotal actual: $"
                + String.format("%,.0f", ingreso.calcularSubtotal());
    }

    public String registrarPago(
            String documento,
            Pago.MedioPago medio) {

        Ingreso ingreso = buscarIngresoActivo(documento);

        if (ingreso == null) {
            return "No existe ingreso activo para este paciente.";
        }

        double subtotal = ingreso.calcularSubtotal();

        if (subtotal <= 0) {
            return "No se puede pagar: el ingreso no tiene servicios registrados.";
        }

        boolean tieneEps = tieneEps(ingreso.getPaciente());

        if (medio == Pago.MedioPago.SEGURO_EPS && !tieneEps) {
            return "El paciente no tiene EPS/Seguro registrado.";
        }

        if (tieneEps) {
            medio = Pago.MedioPago.SEGURO_EPS;
        }

        double descuento = calcularDescuento(ingreso);
        double baseConDescuento = subtotal - descuento;

        if (baseConDescuento < 0) {
            baseConDescuento = 0;
        }

        double recargo = calcularRecargo(medio, baseConDescuento);

        Pago pago = new Pago(
                medio,
                subtotal,
                descuento,
                recargo);

        ingreso.setPago(pago);

        contadorPorMedio[medio.getIndice()]++;
        montoPorMedio[medio.getIndice()] += pago.getMontoTotal();

        if (ingreso.getConsultorio() != null) {
            ingreso.getConsultorio().setEstado("disponible");
        }

        return "\nPago registrado correctamente."
        + "\nMedio de pago : " + medio.getNombre()
        + "\nSubtotal      : $" + String.format("%,.0f", subtotal)
        + "\nDescuento     : $" + String.format("%,.0f", descuento)
        + "\nRecargo       : $" + String.format("%,.0f", recargo)
        + "\nTotal pagado  : $" + String.format("%,.0f", pago.getMontoTotal());
    }

    private Servicio buscarServicioPorCodigo(String codigoServicio) {

        Long codigo = convertirCodigoServicio(codigoServicio);

        if (codigo == null) {
            return null;
        }

        final Servicio[] encontrado = {null};

        catalogoServicios.recorrer(s -> {

            if (s.getCodigo().equals(codigo)) {
                encontrado[0] = s;
            }
        });

        return encontrado[0];
    }

    private Long convertirCodigoServicio(String codigoServicio) {

        if (codigoServicio == null) {
            return null;
        }

        String codigoLimpio = codigoServicio
                .trim()
                .toUpperCase()
                .replace("SRV", "");

        try {
            return Long.parseLong(codigoLimpio);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double calcularDescuento(Ingreso ingreso) {

        Paciente paciente = ingreso.getPaciente();
        double subtotal = ingreso.calcularSubtotal();

        if (tieneEps(paciente)) {
            return subtotal;
        }

        if (paciente.getEdad() < 5) {
            return subtotal * 0.20;
        }

        if (paciente.getEdad() >= 65) {
            return subtotal * 0.30;
        }

        return 0;
    }

    private double calcularRecargo(
            Pago.MedioPago medio,
            double baseConDescuento) {

        if (medio == Pago.MedioPago.TARJETA_DEBITO
                || medio == Pago.MedioPago.TARJETA_CREDITO) {

            return baseConDescuento * 0.02;
        }

        return 0;
    }

    private boolean tieneEps(Paciente paciente) {

        return paciente.getEps() != null
                && !paciente.getEps().trim().isEmpty();
    }

    public Ingreso buscarIngresoActivo(String documento) {

        final Ingreso[] encontrado = {null};

        historialIngresos.recorrer(i -> {

            if (!i.isCancelado()
                    && !i.isPagado()
                    && i.getPaciente()
                    .getDocumento()
                    .equalsIgnoreCase(documento)) {

                encontrado[0] = i;
            }
        });

        return encontrado[0];
    }

    public Ingreso checkInDirecto(String documento) {

        Paciente paciente = buscarPaciente(documento);

        if (paciente == null) {
            return null;
        }

        Ingreso ingreso = new Ingreso(paciente);

        historialIngresos.insertar(ingreso);

        return ingreso;
    }

    public ListaEnlazada<Paciente> getListaPacientes() {
        return listaPacientes;
    }

    public Cola<Paciente> getColaAtencion() {
        return colaAtencion;
    }

    public Cola<Paciente> getColaPreoperatoria() {
        return colaPreoperatoria;
    }

    public ListaEnlazada<Ingreso> getHistorialIngresos() {
        return historialIngresos;
    }

    public ListaEnlazada<Consultorio> getHabitaciones() {
        return habitaciones;
    }

    public Pila<String> getPilaServicios() {
        return pilaServicios;
    }

    public ListaEnlazada<Servicio> getCatalogoServicios() {
        return catalogoServicios;
    }

    public int[] getContadorPorMedio() {
        return contadorPorMedio;
    }

    public double[] getMontoPorMedio() {
        return montoPorMedio;
    }
}
