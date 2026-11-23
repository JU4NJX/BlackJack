package co.edu.unilibre.dto;

import java.time.LocalDateTime;

public class HistorialDTO {

    private int id;
    private LocalDateTime fecha;
    private String resultado;
    private double apuesta;
    private double puntosGanados;
    private double puntosPerdidos;
    private double saldoFinal;

    public int obtenerId() {
        return id;
    }

    public void modificarId(int id) {
        this.id = id;
    }

    public LocalDateTime obtenerFecha() {
        return fecha;
    }

    public void modificarFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String obtenerResultado() {
        return resultado;
    }

    public void modificarResultado(String resultado) {
        this.resultado = resultado;
    }

    public double obtenerApuesta() {
        return apuesta;
    }

    public void modificarApuesta(double apuesta) {
        this.apuesta = apuesta;
    }

    public double obtenerPuntosGanados() {
        return puntosGanados;
    }

    public void modificarPuntosGanados(double puntosGanados) {
        this.puntosGanados = puntosGanados;
    }

    public double obtenerPuntosPerdidos() {
        return puntosPerdidos;
    }

    public void modificarPuntosPerdidos(double puntosPerdidos) {
        this.puntosPerdidos = puntosPerdidos;
    }

    public double obtenerSaldoFinal() {
        return saldoFinal;
    }

    public void modificarSaldoFinal(double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
}