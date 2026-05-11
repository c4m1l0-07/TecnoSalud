import co.edu.tdea.Cola;
import co.edu.tdea.ListaEnlazada;
import co.edu.tdea.Pila;

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
    }

    private void cargarConsultorios() {

        habitaciones.insertar(
                new Consultorio(1,
                        "consulta externa",
                        "disponible"));

        habitaciones.insertar(
                new Consultorio(2,
                        "hospitalizacion",
                        "disponible"));

        habitaciones.insertar(
                new Consultorio(3,
                        "quirofano",
                        "disponible"));
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

        ingreso.cancelar();

        return "Ingreso cancelado.";
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

    public Ingreso checkInDirecto(String documento) {

        Paciente paciente = buscarPaciente(documento);

        if (paciente == null) {
            return null;
        }

        Ingreso ingreso = new Ingreso(paciente);

        historialIngresos.insertar(ingreso);

        return ingreso;
    }
}
