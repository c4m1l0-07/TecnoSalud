import co.edu.tdea.Cola;
import co.edu.tdea.ListaEnlazada;

// ════════════════════════════════════════════════════════════════════════════
// Estudiante 4 – Módulo de Reportes y Estadísticas
//
// Genera los 10 reportes requeridos por el enunciado.
// Depende de SistemaClinica (Est.3) como fuente de datos.
// Itera todas las listas usando recorrer(Consumer) con arrays-holder para
// acumular resultados dentro de lambdas (Java no permite capturar variables
// locales mutables en expresiones lambda).
//
// Clases del proyecto que se usan aquí:
//   SistemaClinica  → fuente de datos (getHistorialIngresos, getListaPacientes, …)
//   Ingreso         → isCancelado(), isPagado(), getPago(), getPaciente(), …
//   Paciente        → getGenero(), getEdad(), getEps(), getNombre(), getDocumento()
//   Pago            → getMontoTotal(), getRecargo()
//   ItemServicio    → getServicio(), getMontoFinal()
//   Servicio        → getCodigo() [Long], getNombre()
//   Cola<Paciente>  → tamano(), capacidadMaxima(), recorrer(Consumer)
//   Pila<String>    → tamano()
//   ListaEnlazada<T>→ tamano(), estaVacia(), recorrer(Consumer)
// ════════════════════════════════════════════════════════════════════════════
public class Reportes {

    /** Fuente de datos compartida con el resto del sistema. */
    private SistemaClinica sistema;

    private static final String L1 = "=".repeat(72);
    private static final String L2 = "-".repeat(72);

    /**
     * Constructor: recibe la instancia única de SistemaClinica creada en Main.
     * No crea ni copia datos; trabaja siempre sobre la referencia en vivo.
     */
    public Reportes(SistemaClinica sistema) {
        this.sistema = sistema;
    }

    // ── Helpers de formato ───────────────────────────────────────────────────

    /** Formatea un valor como moneda con separador de miles. */
    private String moneda(double v) {
        return String.format("$%,14.0f", v);
    }

    /** Calcula porcentaje y lo devuelve formateado (ej. " 42.3 %"). */
    private String pct(double parte, double total) {
        if (total == 0) return "  0.0 %";
        return String.format("%5.1f %%", (parte / total) * 100.0);
    }

    /** Encabezado de reporte con línea decorativa. */
    private String titulo(String texto) {
        return "\n" + L1 + "\n  " + texto + "\n" + L1 + "\n";
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 1 – Total de pacientes / ingresos
    // Recorre getHistorialIngresos() con recorrer(Consumer) para contar
    // según estado: cancelado, pagado o en curso.
    // ════════════════════════════════════════════════════════════════════════
    public String reporteTotalPacientes() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 1 – TOTAL DE PACIENTES"));

        // [0]=total ingresos  [1]=pagados  [2]=activos  [3]=cancelados
        int[] counts = {0, 0, 0, 0};
        sistema.getHistorialIngresos().recorrer(ing -> {
            counts[0]++;
            if      (ing.isCancelado()) counts[3]++;
            else if (ing.isPagado())    counts[1]++;
            else                        counts[2]++;
        });

        // tamano() es el método correcto en ListaEnlazada (no getTamanio())
        sb.append(String.format("  Pacientes registrados en el sistema : %d%n",
            sistema.getListaPacientes().tamano()));
        sb.append(String.format("  Total ingresos generados            : %d%n", counts[0]));
        sb.append(String.format("  Atendidos y pagados                 : %d%n", counts[1]));
        sb.append(String.format("  En atención (sin pago aún)          : %d%n", counts[2]));
        sb.append(String.format("  Cancelados                          : %d%n", counts[3]));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 2 – Pacientes y monto pagado por género
    // Paciente.getGenero() devuelve "M", "F" u "Otro".
    // Solo se cuentan ingresos pagados (no cancelados).
    // ════════════════════════════════════════════════════════════════════════
    public String reportePorGenero() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 2 – PACIENTES Y MONTO POR GÉNERO"));

        // cs[0]=cntM  cs[1]=cntF  cs[2]=cntOtro
        int[]    cs = {0, 0, 0};
        double[] ms = {0, 0, 0};

        sistema.getHistorialIngresos().recorrer(ing -> {
            if (!ing.isCancelado() && ing.isPagado()) {
                String gen   = ing.getPaciente().getGenero().trim().toUpperCase();
                double monto = ing.getPago().getMontoTotal();
                if      (gen.equals("M")) { cs[0]++; ms[0] += monto; }
                else if (gen.equals("F")) { cs[1]++; ms[1] += monto; }
                else                      { cs[2]++; ms[2] += monto; }
            }
        });

        int    total      = cs[0] + cs[1] + cs[2];
        double montoTotal = ms[0] + ms[1] + ms[2];

        sb.append(String.format("  %-14s | %7s | %8s | %15s | %8s%n",
            "Género", "Pac.", "% Pac.", "Monto", "% Monto"));
        sb.append(L2).append("\n");
        sb.append(fila("Masculino", cs[0], total, ms[0], montoTotal));
        sb.append(fila("Femenino",  cs[1], total, ms[1], montoTotal));
        sb.append(fila("Otro",      cs[2], total, ms[2], montoTotal));
        sb.append(L2).append("\n");
        sb.append(String.format("  %-14s | %7d | %8s | %15s | %8s%n",
            "TOTAL", total, "100.0 %", moneda(montoTotal), "100.0 %"));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    /** Fila de tabla con conteo, porcentaje, monto y porcentaje de monto. */
    private String fila(String etiq, int cant, int totalCant, double monto, double totalMonto) {
        return String.format("  %-14s | %7d | %8s | %15s | %8s%n",
            etiq, cant, pct(cant, totalCant), moneda(monto), pct(monto, totalMonto));
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 3 – Monto total recaudado por la clínica
    // Suma montoTotal, descuentos y recargos de todos los ingresos pagados.
    // ════════════════════════════════════════════════════════════════════════
    public String reporteMontoTotal() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 3 – MONTO TOTAL RECAUDADO"));

        // [0]=recaudado  [1]=descuentos  [2]=recargos
        double[] totales = {0, 0, 0};
        int[]    cnt     = {0};

        sistema.getHistorialIngresos().recorrer(ing -> {
            if (!ing.isCancelado() && ing.isPagado()) {
                totales[0] += ing.getPago().getMontoTotal();
                totales[1] += ing.getTotalDescuentos();
                totales[2] += ing.getPago().getRecargo();
                cnt[0]++;
            }
        });

        double promedio = cnt[0] > 0 ? totales[0] / cnt[0] : 0;

        sb.append(String.format("  Total recaudado (pagos recibidos) : %s%n", moneda(totales[0])));
        sb.append(String.format("  Total descuentos otorgados        : %s%n", moneda(totales[1])));
        sb.append(String.format("  Total recargos por tarjeta (2%%)  : %s%n", moneda(totales[2])));
        sb.append(String.format("  Pacientes que pagaron             : %d%n", cnt[0]));
        sb.append(String.format("  Promedio pagado por paciente      : %s%n", moneda(promedio)));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 4 – Recaudo por medio de pago
    // Usa los vectores getContadorPorMedio() y getMontoPorMedio() de
    // SistemaClinica, indexados por Pago.MedioPago.getIndice() (0-3).
    // ════════════════════════════════════════════════════════════════════════
    public String reportePorMedioPago() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 4 – RECAUDO POR MEDIO DE PAGO"));

        int[]    cont   = sistema.getContadorPorMedio();   // tamaño 4
        double[] montos = sistema.getMontoPorMedio();       // tamaño 4

        double totalMonto = 0;
        int    totalCont  = 0;
        for (int i = 0; i < 4; i++) { totalMonto += montos[i]; totalCont += cont[i]; }

        // Orden debe coincidir con los índices de Pago.MedioPago (0=Efectivo … 3=EPS)
        String[] nombres = {"Efectivo", "Tarjeta Débito", "Tarjeta Crédito", "Seguro / EPS"};
        sb.append(String.format("  %-16s | %7s | %8s | %15s | %8s%n",
            "Medio de pago", "Pac.", "% Pac.", "Monto", "% Monto"));
        sb.append(L2).append("\n");
        for (int i = 0; i < 4; i++) {
            sb.append(String.format("  %-16s | %7d | %8s | %15s | %8s%n",
                nombres[i], cont[i], pct(cont[i], totalCont),
                moneda(montos[i]), pct(montos[i], totalMonto)));
        }
        sb.append(L2).append("\n");
        sb.append(String.format("  %-16s | %7d | %8s | %15s | %8s%n",
            "TOTAL", totalCont, "100.0 %", moneda(totalMonto), "100.0 %"));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 5 – Recaudo por tipo de servicio
    //
    // Estrategia:
    //   1. Se recorre el catálogo para construir arrays paralelos de Long (código)
    //      y String (nombre).  getCodigo() devuelve Long en Servicio.
    //   2. Se recorre el historial; por cada ingreso pagado se recorren sus
    //      ItemServicio buscando el índice del servicio en los arrays.
    //   3. Se acumulan cantidad y monto en arrays int/double por índice.
    // ════════════════════════════════════════════════════════════════════════
    public String reportePorServicio() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 5 – RECAUDO POR SERVICIO"));

        ListaEnlazada<Servicio> catalogo = sistema.getCatalogoServicios();
        int n = catalogo.tamano();   // tamano() — no getTamanio()

        Long[]   codigos = new Long[n];
        String[] nombres = new String[n];
        int[]    conts   = new int[n];
        double[] montos  = new double[n];

        // Poblar arrays de catálogo usando recorrer + índice holder
        int[] idx = {0};
        catalogo.recorrer(sv -> {
            codigos[idx[0]] = sv.getCodigo();   // getCodigo() devuelve Long
            nombres[idx[0]] = sv.getNombre();
            idx[0]++;
        });

        // Acumular por cada ingreso pagado y cada ítem de servicio
        sistema.getHistorialIngresos().recorrer(ing -> {
            if (!ing.isCancelado() && ing.isPagado()) {
                ing.getServicios().recorrer(item -> {
                    Long cod = item.getServicio().getCodigo();
                    for (int i = 0; i < n; i++) {
                        if (codigos[i] != null && codigos[i].equals(cod)) {
                            conts[i]++;
                            montos[i] += item.getMontoFinal();
                            break;
                        }
                    }
                });
            }
        });

        double totalMonto = 0;
        int    totalCont  = 0;
        for (int i = 0; i < n; i++) { totalMonto += montos[i]; totalCont += conts[i]; }

        sb.append(String.format("  %-42s | %5s | %15s | %8s%n",
            "Servicio", "Veces", "Monto", "% Monto"));
        sb.append(L2).append("\n");
        for (int i = 0; i < n; i++) {
            if (conts[i] > 0) {
                sb.append(String.format("  %-42s | %5d | %15s | %8s%n",
                    nombres[i], conts[i], moneda(montos[i]), pct(montos[i], totalMonto)));
            }
        }
        sb.append(L2).append("\n");
        sb.append(String.format("  %-42s | %5d | %15s | %8s%n",
            "TOTAL", totalCont, moneda(totalMonto), "100.0 %"));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 6 – Descuentos aplicados por categoría
    //
    // Paciente NO tiene tieneEps(): se verifica inline con getEps().
    // La categoría EPS tiene prioridad sobre edad (igual que en SistemaClinica).
    // ════════════════════════════════════════════════════════════════════════
    public String reporteDescuentos() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 6 – DESCUENTOS APLICADOS"));

        // descs[0]=menores<5  descs[1]=mayores>=65  descs[2]=EPS
        double[] descs     = {0, 0, 0};
        int[]    cnts      = {0, 0, 0};
        double[] totalDesc = {0};

        sistema.getHistorialIngresos().recorrer(ing -> {
            if (!ing.isCancelado() && ing.isPagado()) {
                int    edad  = ing.getPaciente().getEdad();
                double d     = ing.getTotalDescuentos();
                totalDesc[0] += d;

                // tieneEps() no existe en Paciente → verificar el String directamente
                String epsVal   = ing.getPaciente().getEps();
                boolean tieneEps = epsVal != null && !epsVal.trim().isEmpty();

                if      (tieneEps)   { descs[2] += d; cnts[2]++; }
                else if (edad < 5)   { descs[0] += d; cnts[0]++; }
                else if (edad >= 65) { descs[1] += d; cnts[1]++; }
            }
        });

        sb.append(String.format("  Descuentos menores de 5 años  (20%%) : %s  (%d pac.)%n",
            moneda(descs[0]), cnts[0]));
        sb.append(String.format("  Descuentos mayores de 65 años (30%%) : %s  (%d pac.)%n",
            moneda(descs[1]), cnts[1]));
        sb.append(String.format("  Descuentos cubiertos por EPS        : %s  (%d pac.)%n",
            moneda(descs[2]), cnts[2]));
        sb.append(L2).append("\n");
        sb.append(String.format("  TOTAL descuentos otorgados          : %s%n", moneda(totalDesc[0])));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 7 – Monto recaudado por rango de edad
    // Rangos: < 5 años | 5–64 años | >= 65 años
    // ════════════════════════════════════════════════════════════════════════
    public String reportePorRangoEdad() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 7 – RECAUDO POR RANGO DE EDAD"));

        // cs/ms: [0]=niños  [1]=adultos  [2]=mayores
        int[]    cs = {0, 0, 0};
        double[] ms = {0, 0, 0};

        sistema.getHistorialIngresos().recorrer(ing -> {
            if (!ing.isCancelado() && ing.isPagado()) {
                int    edad  = ing.getPaciente().getEdad();
                double monto = ing.getPago().getMontoTotal();
                if      (edad <  5)  { cs[0]++; ms[0] += monto; }
                else if (edad < 65)  { cs[1]++; ms[1] += monto; }
                else                 { cs[2]++; ms[2] += monto; }
            }
        });

        int    total      = cs[0] + cs[1] + cs[2];
        double montoTotal = ms[0] + ms[1] + ms[2];

        sb.append(String.format("  %-12s | %7s | %8s | %15s | %8s%n",
            "Rango edad", "Pac.", "% Pac.", "Monto", "% Monto"));
        sb.append(L2).append("\n");
        sb.append(String.format("  %-12s | %7d | %8s | %15s | %8s%n",
            "< 5 años",    cs[0], pct(cs[0], total), moneda(ms[0]), pct(ms[0], montoTotal)));
        sb.append(String.format("  %-12s | %7d | %8s | %15s | %8s%n",
            "5 – 64 años", cs[1], pct(cs[1], total), moneda(ms[1]), pct(ms[1], montoTotal)));
        sb.append(String.format("  %-12s | %7d | %8s | %15s | %8s%n",
            ">= 65 años",  cs[2], pct(cs[2], total), moneda(ms[2]), pct(ms[2], montoTotal)));
        sb.append(L2).append("\n");
        sb.append(String.format("  %-12s | %7d | %8s | %15s | %8s%n",
            "TOTAL", total, "100.0 %", moneda(montoTotal), "100.0 %"));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 8 – Paciente que más pagó y que menos pagó
    // Usa arrays de tamaño 1 como holder mutable para las referencias a
    // Ingreso (los lambdas no pueden capturar variables locales reasignables).
    // ════════════════════════════════════════════════════════════════════════
    public String reporteExtremosPago() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 8 – PACIENTE QUE MÁS / MENOS PAGÓ"));

        double[]  maxM   = {-1};
        double[]  minM   = {Double.MAX_VALUE};
        Ingreso[] maxIng = {null};
        Ingreso[] minIng = {null};

        sistema.getHistorialIngresos().recorrer(ing -> {
            if (!ing.isCancelado() && ing.isPagado()) {
                double m = ing.getPago().getMontoTotal();
                if (m > maxM[0]) { maxM[0] = m; maxIng[0] = ing; }
                if (m < minM[0]) { minM[0] = m; minIng[0] = ing; }
            }
        });

        if (maxIng[0] == null) {
            sb.append("  Sin datos de pagos registrados.\n");
        } else {
            sb.append(String.format("  MAYOR PAGO:%n"));
            sb.append(String.format("    Doc     : %s%n", maxIng[0].getPaciente().getDocumento()));
            sb.append(String.format("    Nombre  : %s%n", maxIng[0].getPaciente().getNombre()));
            sb.append(String.format("    Monto   : %s%n", moneda(maxM[0])));
            sb.append(String.format("%n  MENOR PAGO:%n"));
            sb.append(String.format("    Doc     : %s%n", minIng[0].getPaciente().getDocumento()));
            sb.append(String.format("    Nombre  : %s%n", minIng[0].getPaciente().getNombre()));
            sb.append(String.format("    Monto   : %s%n", moneda(minM[0])));
        }
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 9 – Estado actual de las colas
    // Cola usa tamano() y capacidadMaxima() (no getTamanio/getCapacidadMaxima).
    // Los pacientes se listan con recorrer(Consumer).
    // ════════════════════════════════════════════════════════════════════════
    public String reporteEstadoColas() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 9 – ESTADO ACTUAL DE COLAS"));

        Cola<Paciente> cA  = sistema.getColaAtencion();
        Cola<Paciente> cPO = sistema.getColaPreoperatoria();

        // tamano() — método correcto (no getTamanio())
        sb.append(String.format("  Cola de atención (consulta externa) : %d paciente(s) en espera%n",
            cA.tamano()));
        cA.recorrer(p -> sb.append("    - ").append(p.getNombre()).append("\n"));

        sb.append("\n");
        // capacidadMaxima() — método correcto (no getCapacidadMaxima())
        sb.append(String.format("  Cola preoperatoria (quirófano)      : %d / %d paciente(s) en espera%n",
            cPO.tamano(), cPO.capacidadMaxima()));
        cPO.recorrer(p -> sb.append("    - ").append(p.getNombre()).append("\n"));

        sb.append(L2).append("\n");
        // Pila también usa tamano() (no getTamanio())
        sb.append(String.format("  Servicios prestados en pila         : %d registros%n",
            sistema.getPilaServicios().tamano()));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 10 – Promedios estadísticos
    // Ingreso.requirioHospitalizacion() y calcularMinutosEspera() deben ser
    // implementados por el Estudiante 2/3.
    // ════════════════════════════════════════════════════════════════════════
    public String reportePromedios() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 10 – PROMEDIOS ESTADÍSTICOS"));

        // counts[0]=totalPagados  [1]=hospitalizados  [2]=conEspera
        int[]  counts     = {0, 0, 0};
        long[] sumaEspera = {0};

        sistema.getHistorialIngresos().recorrer(ing -> {
            if (!ing.isCancelado() && ing.isPagado()) {
                counts[0]++;
                if (ing.requirioHospitalizacion()) counts[1]++;
                long espera = ing.calcularMinutosEspera();
                if (espera > 0) { sumaEspera[0] += espera; counts[2]++; }
            }
        });

        double pctHosp    = counts[0] > 0 ? (counts[1] * 100.0 / counts[0]) : 0;
        double promEspera = counts[2] > 0 ? (sumaEspera[0] / (double) counts[2]) : 0;

        sb.append(String.format("  Pacientes atendidos                 : %d%n", counts[0]));
        sb.append(String.format("  Pacientes que requirieron hosp.     : %d (%.1f %%)%n",
            counts[1], pctHosp));
        sb.append(String.format("  Promedio tiempo espera en cola      : %.1f min%s%n",
            promEspera, counts[2] == 0 ? " (sin datos de tiempo)" : ""));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE COMPLETO – Imprime todos los reportes en secuencia
    // ════════════════════════════════════════════════════════════════════════
    public void mostrarReporteCompleto() {
        System.out.println(reporteTotalPacientes());
        System.out.println(reportePorGenero());
        System.out.println(reporteMontoTotal());
        System.out.println(reportePorMedioPago());
        System.out.println(reportePorServicio());
        System.out.println(reporteDescuentos());
        System.out.println(reportePorRangoEdad());
        System.out.println(reporteExtremosPago());
        System.out.println(reporteEstadoColas());
        System.out.println(reportePromedios());
    }
}
