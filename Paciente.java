public class Paciente {
    private Long documento;
    private String nombre;
    private int edad;
    private String genero;
    private Long numero;
    private String eps;
    private String sangre;

    public Paciente(){
    }
    public Paciente(Long documento, String nombre, int edad, String genero, Long numero,String eps, String sangre){
        this.documento=documento;
        this.nombre=nombre;
        this.edad=edad;
        this.genero=genero;
        this.numero=numero;
        this.eps=eps;
        this.sangre=sangre;
    }
    public Long getDocumento(){
        return documento;
    }
    public void setDocumento(Long documento){
        this.documento=documento;
    }
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public int getEdad(){
        return edad;
    }
    public void setEdad(int edad){
        this.edad=edad;
    }
    public String getGenero(){
        return genero;
    }
    public void setGenero(String genero){
        this.genero=genero;
    }
    public Long getNumero(){
        return numero;
    }
    public void setNumero(Long numero){
        this.numero=numero;
    }
    public String getEps(){
        return eps;
    }
    public void setEps(String eps){
        this.eps=eps;
    }
    public String getSangre(){
        return sangre;
    }
    public void setSangre(String sangre){
        this.sangre=sangre;
    }

}
