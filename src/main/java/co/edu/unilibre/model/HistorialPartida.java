package co.edu.unilibre.model;

import co.edu.unilibre.view.Resultado;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HistorialPartida {
    private long id;
    private long idUsuario;
    private LocalDateTime fecha;
    private Resultado resultado;
    private int apuestaTotal;
    private int puntosGanadosPerdidos;
    private int saldoFinal;

    public HistorialPartida (){}

    //Encapsulamiento

    public long obtenerId (){
        return id;
    }
    public void modificarId (long id){
        this.id = id;
    }

    public long obtenerIdUsuario (){
        return idUsuario;
    }
    public void modificarIdUsuario(long idUsuario){
        this.idUsuario = idUsuario;
    }

    public LocalDateTime obtenerFecha (){
        return fecha;
    }
    public void modificarFecha (LocalDateTime fecha){
        this.fecha = fecha;
    }

    public Resultado obtenerResultado (){
        return resultado;
    }
    public  void modificarResultado (Resultado resultado){
        this.resultado = resultado;
    }

    public int obtenerApuestaTotal (){
        return apuestaTotal;
    }
    public void modificarApuestaTotal (){
        this.apuestaTotal = apuestaTotal;
    }

    public int obtenerPuntosGanadosPerdidos (){
        return puntosGanadosPerdidos;
    }
    public void modificarPuntosGanadosPerdidos (int puntosGanadosPerdidos){
        this.puntosGanadosPerdidos = puntosGanadosPerdidos;
    }

    public int obtenerSaldoFinal (){
        return saldoFinal;
    }
    public void modificarSaldoFinal (int saldoFinal){
        this.saldoFinal = saldoFinal;
    }
}
