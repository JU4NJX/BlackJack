package co.edu.unilibre.model;

public class ManoJugador {

    private Apuesta apuesta;
    private boolean doblada;
    private boolean dividida;
    private boolean manoDeAsesDivididos;
    private boolean activa;
    private boolean plantada;
    private boolean pasada;

    public ManoJugador(Apuesta apuesta){
        this.apuesta = apuesta;
    }

    public void pedirCarta(Mazo mazo){
    }

    public void plantarse(){
    }

    public void marcarDoblada(){
    }

    public Carta separarCarta(){
        return null;
    }

    public void finalizarMano(){
    }

    public boolean puedePedirCarta(){
        return false;
    }

    public boolean puedeDoblar(){
        return false;
    }
    public boolean puedeDividir(){
        return false;
    }

}
