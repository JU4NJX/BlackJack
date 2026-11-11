package co.edu.unilibre.model;

import co.edu.unilibre.state.Estado;
import co.edu.unilibre.view.Resultado;

import java.time.LocalDateTime;
import java.util.List;

public class Partida {
    private long id;
    private Usuario usuario;
    private Mazo mazo;
    private List<ManoJugador> manosJugador;
    private Mano manoCrupier;
    private Estado estadoActual;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private Resultado resultado;
    private boolean finalizada;

    public Partida (Usuario usuario, Mazo mazo){
        this.usuario = usuario;
        this.mazo = mazo;
    }

    //Encapsulamiento

    public long obtenerId (){return id;}
    public void modificarId (long id){this.id = id;}

    public Usuario obtenerUsuario (){return usuario;}
    public void modificarUsuario (Usuario usuario) {this.usuario = usuario;}

    public Mazo obtenerMazo (){return mazo;}
    public void modificarMazo (Mazo mazo){this.mazo = mazo;}

    public List<ManoJugador> obtenerManosJugador () {return manosJugador;}
    public void modificarManosJugador (List<ManoJugador> manoJugador){this.manosJugador = manoJugador;}

    public Mano obtenerManoCrupier (){return manoCrupier;}
    public void modificarManoCrupier (Mano manoCrupier) {this.manoCrupier = manoCrupier;}

    public Estado obtenerEstadoActual () {return estadoActual;}
    public void modificarEstadoActual (Estado estadoActual) {this.estadoActual = estadoActual;}

    public LocalDateTime obtenerFechaInicio (){return fechaInicio;}
    public void modificarFechaInicio (LocalDateTime fechaInicio) {this.fechaInicio = fechaInicio;}

    public LocalDateTime obtenerFechaFinalizacion (){return fechaFinalizacion;}
    public void modificarFechaFinalizacion (LocalDateTime fechaFinalizacion) {this.fechaFinalizacion = fechaFinalizacion;}

    public Resultado obtenerResultado (){return resultado;}
    public void modificarResultado (){this.resultado = resultado;}

    public boolean obtenerFinalizada (){return finalizada;}
    public void modificarFinalizada (boolean finalizada) {this.finalizada = finalizada;}

    //Metodos

    public void finalizar (){}
    public void cambiarEstado (Estado estado) {}
    public void iniciar (){}
    public void pedirCarta (int indiceMano) {}
    public void plantarse (int indiceMano) {}
    public void doblarApuesta (int indiceMano) {}
    public void dividirMano (int indiceMano) {}
    public void ejecutarTurnoCrupier (){}
}
