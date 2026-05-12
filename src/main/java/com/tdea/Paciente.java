package com.tdea;
public class Paciente {

    private String documento;
    private String nombre;
    private int edad;
    private String genero;
    private String telefono;
    private String eps;
    private String sangre;

    public Paciente() {
    }

    public Paciente(String documento, String nombre, int edad,
                     String genero, String telefono,
                     String eps, String sangre) {

        this.documento = documento;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.telefono = telefono;
        this.eps = eps;
        this.sangre = sangre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getSangre() {
        return sangre;
    }

    public void setSangre(String sangre) {
        this.sangre = sangre;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "documento='" + documento + '\'' +
                ", nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", genero='" + genero + '\'' +
                ", telefono='" + telefono + '\'' +
                ", eps='" + eps + '\'' +
                ", sangre='" + sangre + '\'' +
                '}';
    }
}
