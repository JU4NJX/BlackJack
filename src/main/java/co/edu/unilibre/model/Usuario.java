package co.edu.unilibre.model;

import java.util.stream.Stream;

public class Usuario {
    private long id;
    private String nombre;
    private String correo;
    private int saldoPuntos;

    public Usuario (String nombre, String correo){
        this.nombre = nombre;
        this.correo = correo;
    }

    //Encapsulamiento

    public long obtenerId (){return id;}
    public void modificarId (long id){this.id = id;}

    public String obtenerNombre (){return nombre;}
    public void modificarNombre (String nombre) {this.nombre = nombre;}

    public String obtenerCorreo (){return nombre;}
    public void modificarCorreo (){this.correo = correo;}

    public int obtenerSaldoPuntos (){return saldoPuntos;}
    public void modificarSaldoPuntos (int saldoPuntos) {this.saldoPuntos = saldoPuntos;}
}
