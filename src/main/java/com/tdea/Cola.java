package co.edu.tdea;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Cola FIFO con nodos. Modo sin límite ({@code new Cola<>()}) o con capacidad máxima
 * ({@code new Cola<>(n)}). Con límite, {@link #insertar} devuelve {@code false} si está llena.
 *
 * @param <T> tipo de los elementos
 */
public class Cola<T> {

    private static final int SIN_LIMITE = -1;

    private final int capacidadMaxima;
    private Nodo<T> frente;
    private Nodo<T> fondo;
    private int tamano;

    /** Cola sin capacidad máxima (nunca “llena” por tamaño). */
    public Cola() {
        this.capacidadMaxima = SIN_LIMITE;
    }

    /**
     * Cola con capacidad máxima (p. ej. preoperatoria). {@code capacidadMaxima} debe ser &gt; 0.
     *
     * @throws IllegalArgumentException si {@code capacidadMaxima <= 0}
     */
    public Cola(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor que 0: " + capacidadMaxima);
        }
        this.capacidadMaxima = capacidadMaxima;
    }

    /**
     * Encola un elemento. Si la cola tiene límite y está llena, no modifica la estructura y
     * devuelve {@code false}.
     *
     * @return {@code false} solo en cola acotada llena; en otro caso {@code true}
     */
    public boolean insertar(T dato) {
        Objects.requireNonNull(dato, "dato");
        if (capacidadMaxima != SIN_LIMITE && tamano >= capacidadMaxima) {
            return false;
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fondo == null) {
            frente = fondo = nuevo;
        } else {
            fondo.setSiguiente(nuevo);
            fondo = nuevo;
        }
        tamano++;
        return true;
    }

    /**
     * Desencola el frente.
     *
     * @throws NoSuchElementException si la cola está vacía
     */
    public T eliminar() {
        if (frente == null) {
            throw new NoSuchElementException("Cola vacía");
        }
        T valor = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fondo = null;
        }
        tamano--;
        return valor;
    }

    /** Primer elemento sin desencolar. */
    public T verFrente() {
        if (frente == null) {
            throw new NoSuchElementException("Cola vacía");
        }
        return frente.getDato();
    }

    /** Busca el primer elemento igual a {@code clave} desde el frente; si no existe, {@code null}. */
    public T buscar(T clave) {
        for (Nodo<T> n = frente; n != null; n = n.getSiguiente()) {
            if (Objects.equals(n.getDato(), clave)) {
                return n.getDato();
            }
        }
        return null;
    }

    /** Recorrido del frente al fondo. */
    public void recorrer(Consumer<? super T> accion) {
        Objects.requireNonNull(accion, "accion");
        for (Nodo<T> n = frente; n != null; n = n.getSiguiente()) {
            accion.accept(n.getDato());
        }
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamano() {
        return tamano;
    }

    /** Capacidad máxima, o {@code -1} si la cola no tiene límite. */
    public int capacidadMaxima() {
        return capacidadMaxima;
    }

    public boolean estaLlena() {
        return capacidadMaxima != SIN_LIMITE && tamano >= capacidadMaxima;
    }
}
