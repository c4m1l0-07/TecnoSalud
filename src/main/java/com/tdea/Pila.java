package co.edu.tdea;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Pila LIFO con nodos (sin colecciones del JDK como respaldo).
 *
 * @param <T> tipo de los elementos
 */
public class Pila<T> {

    private Nodo<T> cima;
    private int tamano;

    public void insertar(T dato) {
        Nodo<T> nuevo = new Nodo<>(Objects.requireNonNull(dato, "dato"));
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamano++;
    }

    /**
     * Desapila la cima.
     *
     * @throws NoSuchElementException si la pila está vacía
     */
    public T eliminar() {
        if (cima == null) {
            throw new NoSuchElementException("Pila vacía");
        }
        T valor = cima.getDato();
        cima = cima.getSiguiente();
        tamano--;
        return valor;
    }

    /** Cima sin desapilar. */
    public T verCima() {
        if (cima == null) {
            throw new NoSuchElementException("Pila vacía");
        }
        return cima.getDato();
    }

    /** Busca el primer elemento igual a {@code clave} desde la cima hacia el fondo; si no hay, {@code null}. */
    public T buscar(T clave) {
        for (Nodo<T> n = cima; n != null; n = n.getSiguiente()) {
            if (Objects.equals(n.getDato(), clave)) {
                return n.getDato();
            }
        }
        return null;
    }

    /** Recorrido desde la cima hacia el fondo. */
    public void recorrer(Consumer<? super T> accion) {
        Objects.requireNonNull(accion, "accion");
        for (Nodo<T> n = cima; n != null; n = n.getSiguiente()) {
            accion.accept(n.getDato());
        }
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int tamano() {
        return tamano;
    }
}
