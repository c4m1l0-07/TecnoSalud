import java.util.Scanner;
import co.edu.tdea.Cola;
import co.edu.tdea.ListaEnlazada;
import co.edu.tdea.Pila;

// ════════════════════════════════════════════════════════════════════════════
// Estudiante 4 – Menú principal e integración del sistema
//
// Punto de entrada del programa. Coordina los 4 módulos del grupo:
//   Est.1 → ListaEnlazada<T>, Cola<T>, Pila<T>, Nodo<T>  (estructuras)
//   Est.2 → Paciente, Consultorio, Ingreso                (entidades)
//   Est.3 → Servicio, ItemServicio, Pago, SistemaClinica  (lógica de negocio)
//   Est.4 → Reportes, Main                                (menú y estadísticas)
//
// Notas de API para las estructuras (Est.1):
//   · ListaEnlazada, Cola y Pila usan tamano() — NO getTamanio()
//   · Cola usa capacidadMaxima()              — NO getCapacidadMaxima()
//   · Iteración se hace con recorrer(Consumer) — NO getCabeza() + bucle while
//   · Cola usa insertar()/eliminar()           — NO encolar()/desencolar()
//
// Notas de API para entidades (Est.2):
//   · La sala es Consultorio                  — NO ConsultorioHabitacion
//   · Disponibilidad: getEstado()             — NO isDisponible()
//   · Sin tieneEps() en Paciente; verificar getEps() != null && !empty
// ════════════════════════════════════════════════════════════════════════════
public class Main {

    private static SistemaClinica sistema;
    private static Reportes       reportes;
    private static Scanner        sc;

    private static final String SEP = "=".repeat(60);

    /**
     * Punto de entrada. Crea SistemaClinica (fuente única de datos),
     * instancia Reportes con esa misma referencia y abre el menú principal.
     */
    public static void main(String[] args) {
        sistema  = new SistemaClinica();
        reportes = new Reportes(sistema);
        sc       = new Scanner(System.in);

        System.out.println(SEP);
        System.out.println("  BIENVENIDO AL SISTEMA – CLÍNICA TECNOSALUD");
        System.out.println(SEP);

        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Opción: ");
            switch (opcion) {
                case 1:  menuPacientes();    break;
                case 2:  menuColas();        break;
                case 3:  menuHabitaciones(); break;
                case 4:  menuServicios();    break;
                case 5:  menuReportes();     break;
                case 6:  menuConsultas();    break;
                case 0:
                    System.out.println("\n  Hasta luego. Sistema cerrado.\n");
                    continuar = false;
                    break;
                default:
                    System.out.println("  Opción inválida. Intente de nuevo.");
            }
        }
        sc.close();
    }

    // ════════════════════════════════════════════════════════════════════════
    // MENÚ PRINCIPAL
    // ════════════════════════════════════════════════════════════════════════
    private static void mostrarMenuPrincipal() {
        System.out.println("\n" + SEP);
        System.out.println("  MENÚ PRINCIPAL – CLÍNICA TECNOSALUD");
        System.out.println(SEP);
        System.out.println("  1. Gestión de Pacientes");
        System.out.println("  2. Gestión de Colas de Atención");
        System.out.println("  3. Gestión de Habitaciones / Salas");
        System.out.println("  4. Servicios y Pagos");
        System.out.println("  5. Reportes y Estadísticas");
        System.out.println("  6. Consultas Rápidas");
        System.out.println("  0. Salir");
        System.out.println(SEP);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SUBMENÚ 1 – PACIENTES
    // Delega en SistemaClinica (Est.3) todas las operaciones CRUD.
    // ════════════════════════════════════════════════════════════════════════
    private static void menuPacientes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- GESTIÓN DE PACIENTES ---");
            System.out.println("  1. Registrar nuevo paciente");
            System.out.println("  2. Buscar paciente por documento");
            System.out.println("  3. Ver todos los pacientes");
            System.out.println("  4. Check-in directo (sin cola)");
            System.out.println("  5. Cancelar ingreso activo");
            System.out.println("  0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1:  registrarPaciente(); break;
                case 2:  buscarPaciente();    break;
                case 3:  listarPacientes();   break;
                case 4:  checkInDirecto();    break;
                case 5:  cancelarIngreso();   break;
                case 0:  volver = true;       break;
                default: System.out.println("  Opción inválida.");
            }
        }
    }

    private static void registrarPaciente() {
        System.out.println("\n-- Registrar Paciente --");
        String doc   = leerTexto("Documento     : ");
        String nom   = leerTexto("Nombre        : ");
        int    edad  = leerEntero("Edad          : ");
        System.out.print("Género (M/F/Otro): ");
        String gen   = sc.nextLine().trim().toUpperCase();
        String tel   = leerTexto("Teléfono      : ");
        System.out.print("EPS/Seguro (Enter si no tiene): ");
        String eps   = sc.nextLine().trim();
        String sangre = leerTexto("Tipo de sangre: ");
        System.out.println("  " + sistema.registrarPaciente(doc, nom, edad, gen, tel, eps, sangre));
    }

    private static void buscarPaciente() {
        String doc = leerTexto("Documento a buscar: ");
        Paciente p = sistema.buscarPaciente(doc);
        if (p == null) System.out.println("  Paciente no encontrado.");
        else           System.out.println("  Encontrado:\n  " + p);
    }

    /**
     * Lista todos los pacientes usando recorrer(Consumer).
     * ListaEnlazada no tiene toString() útil; hay que iterar con recorrer().
     */
    private static void listarPacientes() {
        System.out.println("\n-- Lista de Pacientes Registrados --");
        if (sistema.getListaPacientes().estaVacia()) {
            System.out.println("  Sin pacientes registrados.");
        } else {
            int[] i = {1};
            sistema.getListaPacientes().recorrer(p ->
                System.out.println("  " + i[0]++ + ". " + p));
        }
    }

    private static void checkInDirecto() {
        String doc = leerTexto("Documento del paciente: ");
        Ingreso ing = sistema.checkInDirecto(doc);
        if (ing == null) System.out.println("  Paciente no encontrado. Regístrelo primero.");
        else System.out.println("  Check-in creado: Ingreso #" + ing.getId()
                                + " para " + ing.getPaciente().getNombre());
    }

    private static void cancelarIngreso() {
        String doc = leerTexto("Documento del paciente: ");
        System.out.println("  " + sistema.cancelarIngreso(doc));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SUBMENÚ 2 – COLAS
    // Cola<Paciente>: insertar()/eliminar(), tamano(), capacidadMaxima().
    // SistemaClinica envuelve esas operaciones con lógica de negocio.
    // ════════════════════════════════════════════════════════════════════════
    private static void menuColas() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- GESTIÓN DE COLAS ---");
            System.out.println("  1. Agregar a cola de atención (consulta externa)");
            System.out.println("  2. Agregar a cola preoperatoria");
            System.out.println("  3. Atender siguiente – cola atención");
            System.out.println("  4. Atender siguiente – cola preoperatoria");
            System.out.println("  5. Ver estado de colas");
            System.out.println("  0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1: {
                    String doc = leerTexto("Documento: ");
                    System.out.println("  " + sistema.agregarAColaAtencion(doc));
                    break;
                }
                case 2: {
                    String doc = leerTexto("Documento: ");
                    System.out.println("  " + sistema.agregarAColaPreoperatoria(doc));
                    break;
                }
                case 3: {
                    Ingreso ing = sistema.atenderSiguienteDeAtencion();
                    if (ing == null) System.out.println("  Cola de atención vacía.");
                    else System.out.println("  Atendiendo: " + ing.getPaciente().getNombre()
                                           + " (Ingreso #" + ing.getId() + ")");
                    break;
                }
                case 4: {
                    Ingreso ing = sistema.atenderSiguientePreoperatorio();
                    if (ing == null) System.out.println("  Cola preoperatoria vacía.");
                    else System.out.println("  Atendiendo: " + ing.getPaciente().getNombre()
                                           + " (Ingreso #" + ing.getId() + ")");
                    break;
                }
                case 5:
                    System.out.println(reportes.reporteEstadoColas());
                    break;
                case 0: volver = true; break;
                default: System.out.println("  Opción inválida.");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // SUBMENÚ 3 – HABITACIONES / SALAS
    // La clase de sala es Consultorio (no ConsultorioHabitacion).
    // Disponibilidad: getEstado() devuelve "disponible" u "ocupado".
    // Listado con recorrer(Consumer) sobre getHabitaciones().
    // ════════════════════════════════════════════════════════════════════════
    private static void menuHabitaciones() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- GESTIÓN DE HABITACIONES / SALAS ---");
            System.out.println("  1. Ver todas las salas y estado");
            System.out.println("  2. Asignar sala a paciente con ingreso activo");
            System.out.println("  3. Liberar sala manualmente");
            System.out.println("  0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1:
                    System.out.println("\n-- Salas de la Clínica --");
                    if (sistema.getHabitaciones().estaVacia()) {
                        System.out.println("  Sin salas registradas.");
                    } else {
                        // Consultorio.getEstado() devuelve "disponible" u "ocupado"
                        int[] i = {1};
                        sistema.getHabitaciones().recorrer(c ->
                            System.out.printf("  %d. Sala %d | %s | %s%n",
                                i[0]++, c.getNumero(), c.getTipo(), c.getEstado()));
                    }
                    break;
                case 2: {
                    String doc  = leerTexto("Documento del paciente: ");
                    int    sala = leerEntero("Número de sala       : ");
                    System.out.println("  " + sistema.asignarHabitacion(doc, sala));
                    break;
                }
                case 3: {
                    int sala = leerEntero("Número de sala a liberar: ");
                    System.out.println("  " + sistema.liberarHabitacion(sala));
                    break;
                }
                case 0: volver = true; break;
                default: System.out.println("  Opción inválida.");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // SUBMENÚ 4 – SERVICIOS Y PAGOS
    // Pago.MedioPago es un enum de Est.3 con valores EFECTIVO, TARJETA_DEBITO,
    // TARJETA_CREDITO, SEGURO_EPS. Cada uno tiene getIndice() (0-3) usado
    // por SistemaClinica para indexar contadorPorMedio[] y montoPorMedio[].
    // ════════════════════════════════════════════════════════════════════════
    private static void menuServicios() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- SERVICIOS Y PAGOS ---");
            System.out.println("  1. Ver catálogo de servicios");
            System.out.println("  2. Agregar servicio a paciente");
            System.out.println("  3. Registrar pago de paciente");
            System.out.println("  4. Ver pila de servicios prestados");
            System.out.println("  5. Ver historial de ingresos");
            System.out.println("  0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1:
                    System.out.println("\n-- Catálogo de Servicios --");
                    if (sistema.getCatalogoServicios().estaVacia()) {
                        System.out.println("  Sin servicios en catálogo.");
                    } else {
                        int[] i = {1};
                        // Servicio.getCodigo() devuelve Long; se imprime con concatenación
                        sistema.getCatalogoServicios().recorrer(s ->
                            System.out.printf("  %d. [%s] %s — $%,.0f%n",
                                i[0]++, s.getCodigo(), s.getNombre(), s.getPrecio()));
                    }
                    break;
                case 2:  agregarServicio();   break;
                case 3:  registrarPago();     break;
                case 4:
                    System.out.println("\n-- Pila de Servicios Prestados (más reciente arriba) --");
                    if (sistema.getPilaServicios().estaVacia()) {
                        System.out.println("  Sin servicios registrados aún.");
                    } else {
                        int[] i = {1};
                        // Pila<String>: cada elemento es un String descriptivo del servicio
                        sistema.getPilaServicios().recorrer(s ->
                            System.out.println("  " + i[0]++ + ". " + s));
                    }
                    break;
                case 5:
                    System.out.println("\n-- Historial de Ingresos --");
                    if (sistema.getHistorialIngresos().estaVacia()) {
                        System.out.println("  Sin ingresos registrados.");
                    } else {
                        int[] i = {1};
                        sistema.getHistorialIngresos().recorrer(ing ->
                            System.out.println("  " + i[0]++ + ". " + ing));
                    }
                    break;
                case 0: volver = true; break;
                default: System.out.println("  Opción inválida.");
            }
        }
    }

    private static void agregarServicio() {
        System.out.println("\n-- Agregar Servicio al Paciente --");
        String doc    = leerTexto("Documento del paciente: ");
        System.out.println("  (Consulte el catálogo con opción 1 para ver los códigos)");
        String codigo = leerTexto("Código de servicio    : ").toUpperCase();
        int    cant   = leerEntero("Cantidad              : ");
        double monto  = 0;
        // Monto especial solo aplica a ítem de farmacia (código configurable por est.3)
        if (codigo.equals("SRV012")) {
            System.out.print("  Monto del ítem de farmacia ($): ");
            monto = leerDouble();
        }
        System.out.println("  " + sistema.agregarServicio(doc, codigo, cant, monto));
    }

    /**
     * Solicita medio de pago y delega en SistemaClinica.registrarPago().
     * Primero muestra el subtotal del ingreso activo para que el usuario
     * conozca el valor antes de seleccionar el medio.
     */
    private static void registrarPago() {
        System.out.println("\n-- Registrar Pago --");
        String doc = leerTexto("Documento del paciente: ");

        Ingreso ing = sistema.buscarIngresoActivo(doc);
        if (ing == null) {
            System.out.println("  ERROR: Sin ingreso activo para este paciente.");
            return;
        }
        System.out.printf("  Subtotal a pagar: $%,.0f%n", ing.calcularSubtotal());

        System.out.println("  Medios de pago:");
        System.out.println("    1. Efectivo");
        System.out.println("    2. Tarjeta Débito  (+2% recargo)");
        System.out.println("    3. Tarjeta Crédito (+2% recargo)");
        System.out.println("    4. Seguro / EPS");
        int opMedio = leerEntero("  Seleccione medio (1-4): ");

        // Pago.MedioPago: enum de Est.3 con getIndice() → 0=Efectivo, 1=Débito, 2=Crédito, 3=EPS
        Pago.MedioPago medio;
        switch (opMedio) {
            case 1:  medio = Pago.MedioPago.EFECTIVO;        break;
            case 2:  medio = Pago.MedioPago.TARJETA_DEBITO;  break;
            case 3:  medio = Pago.MedioPago.TARJETA_CREDITO; break;
            case 4:  medio = Pago.MedioPago.SEGURO_EPS;      break;
            default:
                System.out.println("  Medio inválido. Operación cancelada.");
                return;
        }
        System.out.println("  " + sistema.registrarPago(doc, medio));
    }

    // ════════════════════════════════════════════════════════════════════════
    // SUBMENÚ 5 – REPORTES
    // Todos los métodos de Reportes devuelven String ya formateado.
    // mostrarReporteCompleto() imprime los 10 seguidos.
    // ════════════════════════════════════════════════════════════════════════
    private static void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- REPORTES Y ESTADÍSTICAS ---");
            System.out.println("  1.  Reporte completo (todos los reportes)");
            System.out.println("  2.  Total de pacientes atendidos");
            System.out.println("  3.  Pacientes y montos por género");
            System.out.println("  4.  Monto total recaudado por la clínica");
            System.out.println("  5.  Recaudo por medio de pago (+ porcentajes)");
            System.out.println("  6.  Recaudo por servicio (+ porcentajes)");
            System.out.println("  7.  Total de descuentos aplicados");
            System.out.println("  8.  Recaudo por rango de edad (<5 | 5-64 | >=65)");
            System.out.println("  9.  Paciente que más / menos pagó");
            System.out.println("  10. Estado actual de colas");
            System.out.println("  11. Promedios (hospitalización y espera)");
            System.out.println("  0.  Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1:  reportes.mostrarReporteCompleto();                    break;
                case 2:  System.out.println(reportes.reporteTotalPacientes()); break;
                case 3:  System.out.println(reportes.reportePorGenero());      break;
                case 4:  System.out.println(reportes.reporteMontoTotal());     break;
                case 5:  System.out.println(reportes.reportePorMedioPago());   break;
                case 6:  System.out.println(reportes.reportePorServicio());    break;
                case 7:  System.out.println(reportes.reporteDescuentos());     break;
                case 8:  System.out.println(reportes.reportePorRangoEdad());   break;
                case 9:  System.out.println(reportes.reporteExtremosPago());   break;
                case 10: System.out.println(reportes.reporteEstadoColas());    break;
                case 11: System.out.println(reportes.reportePromedios());      break;
                case 0:  volver = true;                                        break;
                default: System.out.println("  Opción inválida.");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // SUBMENÚ 6 – CONSULTAS RÁPIDAS
    //
    // Caso 1: usa recorrer(Consumer) sobre getHabitaciones() buscando
    //         Consultorio cuyo getEstado() sea "disponible".
    //         — Antes usaba getCabeza()+while sobre ConsultorioHabitacion,
    //           que no existe en este repositorio.
    // Casos 2-3: tamano() y capacidadMaxima() (no getTamanio/getCapacidadMaxima).
    // Caso 5: getServicios() devuelve ListaEnlazada<ItemServicio>; se itera con recorrer.
    // ════════════════════════════════════════════════════════════════════════
    private static void menuConsultas() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- CONSULTAS RÁPIDAS ---");
            System.out.println("  1. Ver salas disponibles");
            System.out.println("  2. Ver pacientes en cola de atención");
            System.out.println("  3. Ver pacientes en cola preoperatoria");
            System.out.println("  4. Ver historial de ingresos");
            System.out.println("  5. Consultar ingreso activo de paciente");
            System.out.println("  0. Volver");
            int op = leerEntero("Opción: ");
            switch (op) {
                case 1: {
                    System.out.println("\n-- Salas Disponibles --");
                    // Consultorio.getEstado() → "disponible" | "ocupado"
                    boolean[] hayDisp = {false};
                    sistema.getHabitaciones().recorrer(c -> {
                        if ("disponible".equalsIgnoreCase(c.getEstado())) {
                            System.out.printf("  Sala %d | %s%n", c.getNumero(), c.getTipo());
                            hayDisp[0] = true;
                        }
                    });
                    if (!hayDisp[0]) System.out.println("  No hay salas disponibles.");
                    break;
                }
                case 2: {
                    System.out.println("\n-- Cola de Atención (Consulta Externa) --");
                    // tamano() — no getTamanio()
                    System.out.println("  Pacientes en espera: " + sistema.getColaAtencion().tamano());
                    sistema.getColaAtencion().recorrer(p ->
                        System.out.println("    · " + p.getNombre()));
                    break;
                }
                case 3: {
                    System.out.println("\n-- Cola Preoperatoria --");
                    // tamano() y capacidadMaxima() — no getTamanio/getCapacidadMaxima
                    System.out.println("  Pacientes en espera: "
                        + sistema.getColaPreoperatoria().tamano()
                        + " / " + sistema.getColaPreoperatoria().capacidadMaxima());
                    sistema.getColaPreoperatoria().recorrer(p ->
                        System.out.println("    · " + p.getNombre()));
                    break;
                }
                case 4: {
                    System.out.println("\n-- Historial de Ingresos --");
                    if (sistema.getHistorialIngresos().estaVacia())
                        System.out.println("  Sin ingresos registrados.");
                    else {
                        int[] i = {1};
                        sistema.getHistorialIngresos().recorrer(ing ->
                            System.out.println("  " + i[0]++ + ". " + ing));
                    }
                    break;
                }
                case 5: {
                    String doc = leerTexto("Documento: ");
                    Ingreso ing = sistema.buscarIngresoActivo(doc);
                    if (ing == null) {
                        System.out.println("  Sin ingreso activo para ese paciente.");
                    } else {
                        System.out.println("\n  " + ing);
                        System.out.printf("  Subtotal acumulado: $%,.0f%n", ing.calcularSubtotal());
                        System.out.println("  Servicios:");
                        if (ing.getServicios().estaVacia())
                            System.out.println("    (sin servicios aún)");
                        else
                            ing.getServicios().recorrer(item ->
                                System.out.printf("    · %s x%d = $%,.0f%n",
                                    item.getServicio().getNombre(),
                                    item.getCantidad(),
                                    item.getMontoFinal()));
                    }
                    break;
                }
                case 0: volver = true; break;
                default: System.out.println("  Opción inválida.");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // UTILIDADES DE LECTURA SEGURA POR CONSOLA
    // ════════════════════════════════════════════════════════════════════════

    /** Lee un entero re-intentando hasta obtener input válido. */
    private static int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("  Por favor ingrese un número entero válido.");
            }
        }
    }

    /** Lee un double aceptando coma o punto decimal. */
    private static double leerDouble() {
        while (true) {
            String linea = sc.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(linea);
            } catch (NumberFormatException e) {
                System.out.print("  Valor inválido. Ingrese el monto: $");
            }
        }
    }

    /** Lee una línea de texto no vacía. */
    private static String leerTexto(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}
