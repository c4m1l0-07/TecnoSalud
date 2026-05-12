package com.tdea;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Lista simple enlazada basada solo en nodos (sin colecciones del JDK como respaldo).
 *
 * @param <T> tipo de los elementos
 */
public class ListaEnlazada<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamano;

    /** Añade un elemento al final de la lista. */
    public void insertar(T dato) {
        Nodo<T> nuevo = new Nodo<>(Objects.requireNonNull(dato, "dato"));
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamano++;
    }

    /**
     * Elimina la primera aparición de {@code dato} según {@link Objects#equals(Object, Object)}.
     *
     * @return {@code true} si se eliminó algún nodo
     */
    public boolean eliminar(T dato) {
        if (cabeza == null) {
            return false;
        }
        if (Objects.equals(cabeza.getDato(), dato)) {
            cabeza = cabeza.getSiguiente();
            if (cabeza == null) {
                cola = null;
            }
            tamano--;
            return true;
        }
        Nodo<T> prev = cabeza;
        Nodo<T> cur = cabeza.getSiguiente();
        while (cur != null) {
            if (Objects.equals(cur.getDato(), dato)) {
                prev.setSiguiente(cur.getSiguiente());
                if (cur == cola) {
                    cola = prev;
                }
                tamano--;
                return true;
            }
            prev = cur;
            cur = cur.getSiguiente();
        }
        return false;
    }

    /** Busca el primer dato igual a {@code clave}; si no hay coincidencia, {@code null}. */
    public T buscar(T clave) {
        for (Nodo<T> n = cabeza; n != null; n = n.getSiguiente()) {
            if (Objects.equals(n.getDato(), clave)) {
                return n.getDato();
            }
        }
        return null;
    }

    /** Recorrido desde el primero hasta el último. */
    public void recorrer(Consumer<? super T> accion) {
        Objects.requireNonNull(accion, "accion");
        for (Nodo<T> n = cabeza; n != null; n = n.getSiguiente()) {
            accion.accept(n.getDato());
        }
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int tamano() {
        return tamano;
    }
}
