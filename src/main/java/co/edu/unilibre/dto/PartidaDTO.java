package co.edu.unilibre.dto;

import co.edu.unilibre.model.enums.EstadoPartida;
import co.edu.unilibre.model.enums.Resultados;

import java.util.ArrayList;
import java.util.List;

public class PartidaDTO {

    private int id;
    private int puntuacionJugador;
    private int puntuacionCrupier;
    private EstadoPartida estado;
    private Resultados resultado;
    private List<CartaDTO> cartasJugador = new ArrayList<>();
    private List<CartaDTO> cartasCrupier = new ArrayList<>();
    private List<List<CartaDTO>> manosJugador = new ArrayList<>();
    private double saldoActual;
    private double apuestaTotal;
    private double puntosResultado;
    private List<Boolean> manosFinalizadas = new ArrayList<>();
    private boolean puedeDividir;

    public int obtenerId() {
        return id;
    }

    public void modificarId(int id) {
        this.id = id;
    }

    public int obtenerPuntuacionJugador() {
        return puntuacionJugador;
    }

    public void modificarPuntuacionJugador(int puntuacionJugador) {
        this.puntuacionJugador = puntuacionJugador;
    }

    public int obtenerPuntuacionCrupier() {
        return puntuacionCrupier;
    }

    public void modificarPuntuacionCrupier(int puntuacionCrupier) {
        this.puntuacionCrupier = puntuacionCrupier;
    }

    public EstadoPartida obtenerEstado() {
        return estado;
    }

    public void modificarEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    public Resultados obtenerResultado() {
        return resultado;
    }

    public void modificarResultado(Resultados resultado) {
        this.resultado = resultado;
    }

    public List<CartaDTO> obtenerCartasJugador() {
        return cartasJugador;
    }

    public void modificarCartasJugador(List<CartaDTO> cartasJugador) {
        this.cartasJugador = cartasJugador;
    }

    public List<CartaDTO> obtenerCartasCrupier() {
        return cartasCrupier;
    }

    public void modificarCartasCrupier(List<CartaDTO> cartasCrupier) {
        this.cartasCrupier = cartasCrupier;
    }

    public List<List<CartaDTO>> obtenerManosJugador() {
        return manosJugador;
    }

    public void modificarManosJugador(List<List<CartaDTO>> manosJugador) {
        this.manosJugador = manosJugador;
    }

    public double obtenerSaldoActual() {
        return saldoActual;
    }

    public void modificarSaldoActual(double saldoActual) {
        this.saldoActual = saldoActual;
    }

    public double obtenerApuestaTotal() {
        return apuestaTotal;
    }

    public void modificarApuestaTotal(double apuestaTotal) {
        this.apuestaTotal = apuestaTotal;
    }

    public double obtenerPuntosResultado() {return puntosResultado;}

    public void modificarPuntosResultado(double puntosResultado) {this.puntosResultado = puntosResultado;}

    public List<Boolean> obtenerManosFinalizadas() {
        return manosFinalizadas;
    }

    public void modificarManosFinalizadas(List<Boolean> manosFinalizadas) {
        if (manosFinalizadas == null) {
            throw new IllegalArgumentException("La lista de estados de las manos no puede ser nula.");
        }
        this.manosFinalizadas = new ArrayList<>(manosFinalizadas);
    }

    public boolean obtenerPuedeDividir() {return puedeDividir;}

    public void modificarPuedeDividir(boolean puedeDividir) {this.puedeDividir = puedeDividir;}
}