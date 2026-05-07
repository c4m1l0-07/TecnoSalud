# TecnoSalud — Sistema de Gestión Clínica

Proyecto universitario desarrollado en Java para la asignatura **Lógica de Programación II**.  
Simula el sistema de gestión de una clínica: registro de pacientes, colas de atención, asignación de salas, servicios, pagos y reportes estadísticos.

---

## Integrantes y responsabilidades

| Estudiante | Módulo | Archivos |
|---|---|---|
| Estudiante 1 | Estructuras de datos | `Nodo.java`, `ListaEnlazada.java`, `Cola.java`, `Pila.java`, `Criterio.java` |
| Estudiante 2 | Entidades | `Paciente.java`, `Consultorio.java`, `Ingreso.java` |
| Estudiante 3 | Lógica de negocio | `Servicio.java`, `ItemServicio.java`, `Pago.java`, `SistemaClinica.java` |
| Estudiante 4 | Menú y reportes | `Main.java`, `Reportes.java` |

---

## Estructura del proyecto

```
TecnoSalud/
├── src/main/java/com/tdea/
│   ├── Nodo.java               ← Est.1
│   ├── ListaEnlazada.java      ← Est.1
│   ├── Cola.java               ← Est.1
│   ├── Pila.java               ← Est.1
│   ├── Criterio.java           ← Est.1
│   ├── Paciente.java           ← Est.2
│   ├── Consultorio.java        ← Est.2
│   ├── Ingreso.java            ← Est.2
│   ├── Servicio.java           ← Est.3
│   ├── ItemServicio.java       ← Est.3
│   ├── Pago.java               ← Est.3
│   ├── SistemaClinica.java     ← Est.3
│   ├── Reportes.java           ← Est.4
│   └── Main.java               ← Est.4
├── pom.xml
└── README.md
```

---

## Requisitos

- Java 17 o superior
- Maven 3.6+ (opcional; también se puede compilar con `javac`)

---

## Cómo compilar y ejecutar

### Opción A — con Maven

```bash
# Desde la carpeta TecnoSalud/
mvn compile
mvn exec:java -Dexec.mainClass="Main"
```

### Opción B — con javac directamente

```bash
# Desde la carpeta que contiene todos los .java
cd src/main/java/com/tdea
javac *.java
java Main
```

> **Nota sobre paquetes:** Algunos archivos existentes declaran `package co.edu.tdea;`
> mientras que otros no tienen declaración de paquete. Antes de compilar, el grupo
> debe acordar un único paquete para todos los archivos (o eliminar todas las
> declaraciones de paquete y compilar desde el mismo directorio).

---

## Qué hace la aplicación

El sistema presenta un menú de 6 submenús interactivos por consola:

### 1. Gestión de Pacientes
- Registrar nuevo paciente (documento, nombre, edad, género, teléfono, EPS, tipo de sangre)
- Buscar paciente por número de documento
- Listar todos los pacientes registrados
- Hacer check-in directo (crea un ingreso sin pasar por la cola)
- Cancelar un ingreso activo

### 2. Gestión de Colas de Atención
- Agregar paciente a la **cola de atención** (consulta externa, sin límite)
- Agregar paciente a la **cola preoperatoria** (máximo 3 pacientes — FIFO con capacidad)
- Atender al siguiente paciente de cada cola (lo desencola y crea su ingreso)
- Ver el estado actual de ambas colas

### 3. Gestión de Habitaciones / Salas
- Ver todas las salas con su número, tipo y estado (`disponible` / `ocupado`)
- Asignar una sala a un paciente con ingreso activo
- Liberar una sala manualmente

### 4. Servicios y Pagos
- Ver catálogo de servicios disponibles (código, nombre, precio)
- Agregar un servicio al ingreso activo de un paciente
- Registrar el pago de un ingreso (efectivo, débito +2%, crédito +2%, EPS)
- Ver la pila de servicios prestados (más reciente primero)
- Ver el historial completo de ingresos

### 5. Reportes y Estadísticas
Diez reportes independientes más un reporte completo que los agrupa:

| # | Reporte |
|---|---|
| 1 | Total de pacientes e ingresos (pagados, activos, cancelados) |
| 2 | Pacientes y monto recaudado por género (M / F / Otro) |
| 3 | Monto total recaudado, descuentos y recargos |
| 4 | Recaudo por medio de pago con porcentajes |
| 5 | Recaudo por tipo de servicio con porcentajes |
| 6 | Descuentos por categoría (<5 años, ≥65 años, EPS) |
| 7 | Recaudo por rango de edad (<5 / 5-64 / ≥65) |
| 8 | Paciente que más pagó y paciente que menos pagó |
| 9 | Estado actual de ambas colas en tiempo real |
| 10 | Promedios: % hospitalizaciones y tiempo medio de espera |

### 6. Consultas Rápidas
- Ver solo las salas disponibles en este momento
- Ver quién está en cada cola (sin desencolar)
- Consultar el ingreso activo de un paciente y sus servicios acumulados

---

## Reglas de negocio implementadas

| Regla | Detalle |
|---|---|
| Descuento por edad | Menores de 5 años: 20 % en consulta/hospitalización |
| Descuento por edad | Mayores de 65 años: 30 % en consulta/hospitalización |
| Cobertura EPS | 100 % cubierto si el paciente tiene EPS registrada |
| Recargo tarjeta | +2 % sobre el subtotal en débito y crédito |
| Cancelación tardía | Si el ingreso se cancela con menos de 24 h, la clínica retiene el 50 % |
| Cola preoperatoria | Capacidad máxima de 3 pacientes simultáneos |

---

## Cómo probar el sistema

Sigue este flujo mínimo para verificar las funciones principales:

1. **Menú 1 → opción 1**: Registrar al menos 3 pacientes (uno con EPS, uno menor de 5 años, uno mayor de 65).
2. **Menú 2 → opción 1**: Agregar dos de ellos a la cola de atención.
3. **Menú 2 → opción 3**: Atender al primero (se crea su ingreso automáticamente).
4. **Menú 4 → opción 1**: Ver el catálogo y anotar un código de servicio.
5. **Menú 4 → opción 2**: Agregar ese servicio al paciente atendido.
6. **Menú 4 → opción 3**: Registrar el pago (seleccionar cualquier medio).
7. **Menú 5 → opción 1**: Ejecutar el reporte completo y verificar que aparezcan los datos.
8. **Menú 6 → opción 5**: Consultar el ingreso activo del segundo paciente (aún sin pagar).

---

## Notas para la integración del grupo

- Todos los `.java` deben estar en el **mismo directorio** y con el **mismo paquete** para compilar sin errores de classpath.
- `Main.java` y `Reportes.java` llaman métodos definidos en `INTEGRACION.md`. Si un compañero usó un nombre diferente, solo hay que cambiar ese nombre en estas dos clases.
- Los errores "cannot be resolved" en el IDE son normales hasta que `SistemaClinica.java`, `Pago.java` e `ItemServicio.java` sean entregados por el Estudiante 3, y hasta que `Ingreso.java` sea completado por el Estudiante 2.
- Consultar `INTEGRACION.md` para el contrato exacto (nombres de métodos) que cada clase debe cumplir.
