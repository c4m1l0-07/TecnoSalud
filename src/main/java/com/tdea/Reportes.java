// ════════════════════════════════════════════════════════════════════════════
// Estudiante 4 - Módulo de Reportes y Estadísticas
// Responsabilidad: generar todos los reportes requeridos por el enunciado
// ════════════════════════════════════════════════════════════════════════════
public class Reportes {

    private SistemaClinica sistema;

    private static final String L1 = "=".repeat(72);
    private static final String L2 = "-".repeat(72);

    public Reportes(SistemaClinica sistema) {
        this.sistema = sistema;
    }

    // ── Helpers de formato ───────────────────────────────────────────────────

    private String moneda(double v) {
        return String.format("$%,14.0f", v);
    }

    private String pct(double parte, double total) {
        if (total == 0) return "  0.0 %";
        return String.format("%5.1f %%", (parte / total) * 100.0);
    }

    private String titulo(String texto) {
        return "\n" + L1 + "\n  " + texto + "\n" + L1 + "\n";
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 1 – Total de pacientes atendidos
    // ════════════════════════════════════════════════════════════════════════
    public String reporteTotalPacientes() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 1 – TOTAL DE PACIENTES"));

        int totalIngresos = 0, pagados = 0, activos = 0, cancelados = 0;
        Nodo<Ingreso> n = sistema.getHistorialIngresos().getCabeza();
        while (n != null) {
            Ingreso ing = n.getDato();
            totalIngresos++;
            if (ing.isCancelado())     cancelados++;
            else if (ing.isPagado())   pagados++;
            else                       activos++;
            n = n.getSiguiente();
        }

        sb.append(String.format("  Pacientes registrados en el sistema : %d%n",
            sistema.getListaPacientes().getTamanio()));
        sb.append(String.format("  Total ingresos generados            : %d%n", totalIngresos));
        sb.append(String.format("  Atendidos y pagados                 : %d%n", pagados));
        sb.append(String.format("  En atención (sin pago aún)          : %d%n", activos));
        sb.append(String.format("  Cancelados                          : %d%n", cancelados));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 2 – Pacientes y monto por género
    // ════════════════════════════════════════════════════════════════════════
    public String reportePorGenero() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 2 – PACIENTES Y MONTO POR GÉNERO"));

        int cM = 0, cF = 0, cO = 0;
        double mM = 0, mF = 0, mO = 0;

        Nodo<Ingreso> n = sistema.getHistorialIngresos().getCabeza();
        while (n != null) {
            Ingreso ing = n.getDato();
            if (!ing.isCancelado() && ing.isPagado()) {
                String gen = ing.getPaciente().getGenero().trim().toUpperCase();
                double monto = ing.getPago().getMontoTotal();
                if      (gen.equals("M")) { cM++; mM += monto; }
                else if (gen.equals("F")) { cF++; mF += monto; }
                else                      { cO++; mO += monto; }
            }
            n = n.getSiguiente();
        }

        int total = cM + cF + cO;
        double montoTotal = mM + mF + mO;

        sb.append(String.format("  %-14s | %7s | %8s | %15s | %8s%n",
            "Género", "Pac.", "% Pac.", "Monto", "% Monto"));
        sb.append(L2).append("\n");
        sb.append(fila("Masculino",  cM, total, mM, montoTotal));
        sb.append(fila("Femenino",   cF, total, mF, montoTotal));
        sb.append(fila("Otro",       cO, total, mO, montoTotal));
        sb.append(L2).append("\n");
        sb.append(String.format("  %-14s | %7d | %8s | %15s | %8s%n",
            "TOTAL", total, "100.0 %", moneda(montoTotal), "100.0 %"));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    private String fila(String etiq, int cant, int totalCant, double monto, double totalMonto) {
        return String.format("  %-14s | %7d | %8s | %15s | %8s%n",
            etiq, cant, pct(cant, totalCant), moneda(monto), pct(monto, totalMonto));
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 3 – Monto total recaudado por la clínica
    // ════════════════════════════════════════════════════════════════════════
    public String reporteMontoTotal() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 3 – MONTO TOTAL RECAUDADO"));

        double totalRecaudado = 0, totalDescuentos = 0, totalRecargos = 0;
        int    pacientesPagados = 0;

        Nodo<Ingreso> n = sistema.getHistorialIngresos().getCabeza();
        while (n != null) {
            Ingreso ing = n.getDato();
            if (!ing.isCancelado() && ing.isPagado()) {
                totalRecaudado   += ing.getPago().getMontoTotal();
                totalDescuentos  += ing.getTotalDescuentos();
                totalRecargos    += ing.getPago().getRecargo();
                pacientesPagados++;
            }
            n = n.getSiguiente();
        }

        double promedio = pacientesPagados > 0 ? totalRecaudado / pacientesPagados : 0;

        sb.append(String.format("  Total recaudado (pagos recibidos) : %s%n", moneda(totalRecaudado)));
        sb.append(String.format("  Total descuentos otorgados        : %s%n", moneda(totalDescuentos)));
        sb.append(String.format("  Total recargos por tarjeta (2%%)  : %s%n", moneda(totalRecargos)));
        sb.append(String.format("  Pacientes que pagaron             : %d%n", pacientesPagados));
        sb.append(String.format("  Promedio pagado por paciente      : %s%n", moneda(promedio)));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 4 – Monto y porcentaje por medio de pago
    // ════════════════════════════════════════════════════════════════════════
    public String reportePorMedioPago() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 4 – RECAUDO POR MEDIO DE PAGO"));

        int[]    cont  = sistema.getContadorPorMedio();
        double[] montos = sistema.getMontoPorMedio();

        double totalMonto = 0;
        int    totalCont  = 0;
        for (int i = 0; i < 4; i++) { totalMonto += montos[i]; totalCont += cont[i]; }

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
    // REPORTE 5 – Monto recaudado por tipo de servicio
    // ════════════════════════════════════════════════════════════════════════
    public String reportePorServicio() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 5 – RECAUDO POR SERVICIO"));

        // Recorrer catálogo y, para cada servicio, sumar lo facturado en el historial
        ListaEnlazada<Servicio> catalogo = sistema.getCatalogoServicios();
        int catalogoSize = catalogo.getTamanio();

        // Arrays paralelos al catálogo
        String[] codigos = new String[catalogoSize];
        String[] nombres = new String[catalogoSize];
        int[]    conts   = new int[catalogoSize];
        double[] montos  = new double[catalogoSize];

        Nodo<Servicio> ns = catalogo.getCabeza();
        int idx = 0;
        while (ns != null) {
            codigos[idx] = ns.getDato().getCodigo();
            nombres[idx] = ns.getDato().getNombre();
            ns = ns.getSiguiente();
            idx++;
        }

        // Sumar por cada ingreso pagado
        Nodo<Ingreso> ni = sistema.getHistorialIngresos().getCabeza();
        while (ni != null) {
            Ingreso ing = ni.getDato();
            if (!ing.isCancelado() && ing.isPagado()) {
                Nodo<ItemServicio> nItem = ing.getServicios().getCabeza();
                while (nItem != null) {
                    ItemServicio item = nItem.getDato();
                    for (int i = 0; i < catalogoSize; i++) {
                        if (codigos[i].equals(item.getServicio().getCodigo())) {
                            conts[i]++;
                            montos[i] += item.getMontoFinal();
                            break;
                        }
                    }
                    nItem = nItem.getSiguiente();
                }
            }
            ni = ni.getSiguiente();
        }

        double totalMonto = 0;
        int    totalCont  = 0;
        for (int i = 0; i < catalogoSize; i++) { totalMonto += montos[i]; totalCont += conts[i]; }

        sb.append(String.format("  %-42s | %5s | %15s | %8s%n",
            "Servicio", "Veces", "Monto", "% Monto"));
        sb.append(L2).append("\n");
        for (int i = 0; i < catalogoSize; i++) {
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
    // REPORTE 6 – Total de descuentos aplicados
    // ════════════════════════════════════════════════════════════════════════
    public String reporteDescuentos() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 6 – DESCUENTOS APLICADOS"));

        double descMenores  = 0, descMayores = 0, descEps = 0, totalDesc = 0;
        int    cntMenores   = 0, cntMayores  = 0, cntEps  = 0;

        Nodo<Ingreso> n = sistema.getHistorialIngresos().getCabeza();
        while (n != null) {
            Ingreso ing = n.getDato();
            if (!ing.isCancelado() && ing.isPagado()) {
                int edad = ing.getPaciente().getEdad();
                double d = ing.getTotalDescuentos();
                totalDesc += d;
                if (ing.getPaciente().tieneEps()) { descEps += d;      cntEps++; }
                else if (edad < 5)                { descMenores += d;  cntMenores++; }
                else if (edad >= 65)              { descMayores += d;  cntMayores++; }
            }
            n = n.getSiguiente();
        }

        sb.append(String.format("  Descuentos menores de 5 años  (20%%) : %s  (%d pac.)%n", moneda(descMenores), cntMenores));
        sb.append(String.format("  Descuentos mayores de 65 años (30%%) : %s  (%d pac.)%n", moneda(descMayores), cntMayores));
        sb.append(String.format("  Descuentos cubiertos por EPS        : %s  (%d pac.)%n", moneda(descEps),     cntEps));
        sb.append(L2).append("\n");
        sb.append(String.format("  TOTAL descuentos otorgados          : %s%n", moneda(totalDesc)));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 7 – Monto recaudado por rango de edad  (<5 | 5-64 | >=65)
    // ════════════════════════════════════════════════════════════════════════
    public String reportePorRangoEdad() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 7 – RECAUDO POR RANGO DE EDAD"));

        int    cNinios = 0, cAdultos = 0, cMayores = 0;
        double mNinios = 0, mAdultos = 0, mMayores = 0;

        Nodo<Ingreso> n = sistema.getHistorialIngresos().getCabeza();
        while (n != null) {
            Ingreso ing = n.getDato();
            if (!ing.isCancelado() && ing.isPagado()) {
                int    edad  = ing.getPaciente().getEdad();
                double monto = ing.getPago().getMontoTotal();
                if      (edad <  5)  { cNinios++;  mNinios  += monto; }
                else if (edad < 65)  { cAdultos++; mAdultos += monto; }
                else                 { cMayores++; mMayores += monto; }
            }
            n = n.getSiguiente();
        }

        int total = cNinios + cAdultos + cMayores;
        double montoTotal = mNinios + mAdultos + mMayores;

        sb.append(String.format("  %-12s | %7s | %8s | %15s | %8s%n",
            "Rango edad", "Pac.", "% Pac.", "Monto", "% Monto"));
        sb.append(L2).append("\n");
        sb.append(String.format("  %-12s | %7d | %8s | %15s | %8s%n",
            "< 5 años",   cNinios,  pct(cNinios, total),  moneda(mNinios),  pct(mNinios, montoTotal)));
        sb.append(String.format("  %-12s | %7d | %8s | %15s | %8s%n",
            "5 – 64 años", cAdultos, pct(cAdultos, total), moneda(mAdultos), pct(mAdultos, montoTotal)));
        sb.append(String.format("  %-12s | %7d | %8s | %15s | %8s%n",
            ">= 65 años",  cMayores, pct(cMayores, total), moneda(mMayores), pct(mMayores, montoTotal)));
        sb.append(L2).append("\n");
        sb.append(String.format("  %-12s | %7d | %8s | %15s | %8s%n",
            "TOTAL", total, "100.0 %", moneda(montoTotal), "100.0 %"));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 8 – Paciente que más pagó y que menos pagó
    // ════════════════════════════════════════════════════════════════════════
    public String reporteExtremosPago() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 8 – PACIENTE QUE MÁS / MENOS PAGÓ"));

        Ingreso maxIng = null, minIng = null;
        double maxMonto = -1, minMonto = Double.MAX_VALUE;

        Nodo<Ingreso> n = sistema.getHistorialIngresos().getCabeza();
        while (n != null) {
            Ingreso ing = n.getDato();
            if (!ing.isCancelado() && ing.isPagado()) {
                double m = ing.getPago().getMontoTotal();
                if (m > maxMonto) { maxMonto = m; maxIng = ing; }
                if (m < minMonto) { minMonto = m; minIng = ing; }
            }
            n = n.getSiguiente();
        }

        if (maxIng == null) {
            sb.append("  Sin datos de pagos registrados.\n");
        } else {
            sb.append(String.format("  MAYOR PAGO:%n"));
            sb.append(String.format("    Doc     : %s%n", maxIng.getPaciente().getDocumento()));
            sb.append(String.format("    Nombre  : %s%n", maxIng.getPaciente().getNombre()));
            sb.append(String.format("    Monto   : %s%n", moneda(maxMonto)));
            sb.append(String.format("%n  MENOR PAGO:%n"));
            sb.append(String.format("    Doc     : %s%n", minIng.getPaciente().getDocumento()));
            sb.append(String.format("    Nombre  : %s%n", minIng.getPaciente().getNombre()));
            sb.append(String.format("    Monto   : %s%n", moneda(minMonto)));
        }
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 9 – Estado actual de las colas
    // ════════════════════════════════════════════════════════════════════════
    public String reporteEstadoColas() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 9 – ESTADO ACTUAL DE COLAS"));

        Cola<Paciente> cA  = sistema.getColaAtencion();
        Cola<Paciente> cPO = sistema.getColaPreoperatoria();

        sb.append(String.format("  Cola de atención (consulta externa) : %d paciente(s) en espera%n",
            cA.getTamanio()));
        sb.append("  " + cA.toString()).append("\n\n");

        sb.append(String.format("  Cola preoperatoria (quirófano)      : %d / %d paciente(s) en espera%n",
            cPO.getTamanio(), cPO.getCapacidadMaxima()));
        sb.append("  " + cPO.toString()).append("\n");

        sb.append(L2).append("\n");
        sb.append(String.format("  Servicios prestados en pila         : %d registros%n",
            sistema.getPilaServicios().getTamanio()));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE 10 – Promedio de hospitalizaciones y tiempo de espera
    // ════════════════════════════════════════════════════════════════════════
    public String reportePromedios() {
        StringBuilder sb = new StringBuilder(titulo("REPORTE 10 – PROMEDIOS ESTADÍSTICOS"));

        int totalPagados = 0, hospitalizados = 0;
        long sumaEspera = 0; int conEspera = 0;

        Nodo<Ingreso> n = sistema.getHistorialIngresos().getCabeza();
        while (n != null) {
            Ingreso ing = n.getDato();
            if (!ing.isCancelado() && ing.isPagado()) {
                totalPagados++;
                if (ing.requirioHospitalizacion()) hospitalizados++;
                long espera = ing.calcularMinutosEspera();
                if (espera > 0) { sumaEspera += espera; conEspera++; }
            }
            n = n.getSiguiente();
        }

        double pctHosp = totalPagados > 0 ? (hospitalizados * 100.0 / totalPagados) : 0;
        double promEspera = conEspera > 0 ? (sumaEspera / (double) conEspera) : 0;

        sb.append(String.format("  Pacientes atendidos                 : %d%n", totalPagados));
        sb.append(String.format("  Pacientes que requirieron hosp.     : %d (%.1f %%)%n",
            hospitalizados, pctHosp));
        sb.append(String.format("  Promedio tiempo espera en cola      : %.1f min%s%n",
            promEspera, conEspera == 0 ? " (sin datos de tiempo)" : ""));
        sb.append(L1).append("\n");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // REPORTE COMPLETO – Llama todos los reportes anteriores
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
