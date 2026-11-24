package co.edu.unilibre.model;

import co.edu.unilibre.model.enums.Resultados;

import java.time.LocalDateTime;

public class HistorialPartida {

    private int id;
    private LocalDateTime fecha;
    private Resultados resultado;
    private double apuestaTotal;
    private double puntosGanados;
    private double puntosPerdidos;
    private double saldoFinal;

    public HistorialPartida(int id, LocalDateTime fecha, double apuestaTotal,
                            double saldoFinal) {
        this.id = id;
        this.fecha = fecha;
        this.apuestaTotal = apuestaTotal;
        this.saldoFinal = saldoFinal;
        this.resultado = null;
        this.puntosGanados = 0;
        this.puntosPerdidos = 0;
    }

    // Métodos de negocio

    public void registrarResultado(Resultados resultado) {
        if (resultado == null) {
            throw new IllegalArgumentException("El resultado no puede ser nulo.");
        }

        this.resultado = resultado;

        switch (resultado) {
            case GANADA:
            case BLACKJACK:
                puntosGanados = calcularRetorno(resultado);
                puntosPerdidos = 0;
                break;

            case PERDIDA:
                puntosGanados = 0;
                puntosPerdidos = apuestaTotal;
                break;

            case EMPATE:
                puntosGanados = 0;
                puntosPerdidos = 0;
                break;
        }
    }

    public double calcularBalance() {
        return puntosGanados - puntosPerdidos;
    }

    private double calcularRetorno(Resultados resultado) {
        switch (resultado) {
            case GANADA:
                return apuestaTotal * 2.0;
            case BLACKJACK:
                return apuestaTotal * 2.5;
            default:
                return 0;
        }
    }

    // Métodos de encapsulamiento

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

    public Resultados obtenerResultado() {
        return resultado;
    }

    public void modificarResultado(Resultados resultado) {
        this.resultado = resultado;
    }

    public double obtenerApuestaTotal() {
        return apuestaTotal;
    }

    public void modificarApuestaTotal(double apuestaTotal) {
        if (apuestaTotal < 0) {
            throw new IllegalArgumentException("La apuesta total no puede ser negativa.");
        }
        this.apuestaTotal = apuestaTotal;
    }

    public double obtenerPuntosGanados() {
        return puntosGanados;
    }

    public void modificarPuntosGanados(double puntosGanados) {
        if (puntosGanados < 0) {
            throw new IllegalArgumentException("Los puntos ganados no pueden ser negativos.");
        }
        this.puntosGanados = puntosGanados;
    }

    public double obtenerPuntosPerdidos() {
        return puntosPerdidos;
    }

    public void modificarPuntosPerdidos(double puntosPerdidos) {
        if (puntosPerdidos < 0) {
            throw new IllegalArgumentException("Los puntos perdidos no pueden ser negativos.");
        }
        this.puntosPerdidos = puntosPerdidos;
    }

    public double obtenerSaldoFinal() {
        return saldoFinal;
    }

    public void modificarSaldoFinal(double saldoFinal) {
        if (saldoFinal < 0) {
            throw new IllegalArgumentException("El saldo final no puede ser negativo.");
        }
        this.saldoFinal = saldoFinal;
    }
}
