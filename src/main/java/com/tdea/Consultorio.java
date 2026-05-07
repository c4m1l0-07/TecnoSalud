public class Consultorio {
    private int numero;
    private String tipo;
    private String estado;

    public Consultorio(){
    }
    public Consultorio(int numero,String tipo,String estado){
        this.numero=numero;
        this.tipo=tipo;
        this.estado=estado;
    }
    public int getNumero(){
        return numero;
    }
    public void setNumero(int numero){
        this.numero=numero;
    }
    public String getTipo(){
        return tipo;
    }
    public void setTipo(String tipo){
        this.tipo=tipo;
    }
    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado=estado;
    }
}
