package co.edu.unilibre.dto;

import co.edu.unilibre.model.enums.EstadoPartida;
import co.edu.unilibre.model.enums.Resultado;

import java.util.ArrayList;
import java.util.List;

public class PartidaDTO {

    private int id;
    private int puntuacionJugador;
    private int puntuacionCrupier;
    private EstadoPartida estado;
    private Resultado resultado;
    private List<CartaDTO> cartasJugador = new ArrayList<>();
    private List<CartaDTO> cartasCrupier = new ArrayList<>();
    private List<List<CartaDTO>> manosJugador = new ArrayList<>();
    private double saldoActual;
    private double apuestaTotal;

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

    public Resultado obtenerResultado() {
        return resultado;
    }

    public void modificarResultado(Resultado resultado) {
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
}