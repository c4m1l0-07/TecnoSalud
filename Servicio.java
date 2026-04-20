public class Servicio {
    private Long codigo;
    private String nombre;
    private double precio;
    
    public Servicio(){
    }

    public Servicio(Long codigo,String nombre,double precio){
        this.codigo=codigo;
        this.nombre=nombre;
        this.precio=precio;
    }
    public Long getCodigo(){
        return codigo;
    }
    public void setCodigo(Long codigo){
        this.codigo=codigo;
    }
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public double getPrecio(){
        return precio;
    }
    public void setPrecio(double precio){
        this.precio=precio;
    }
}
