package co.edu.unilibre.model;

import co.edu.unilibre.model.enums.Resultados;

public class Apuesta {

    private double montoInicial;
    private double montoTotal;
    private double montoAdicional;
    private boolean liquidada;

    private static final double PAGO_BLACKJACK = 2.5;
    private static final double PAGO_NORMAL = 2.0;

    public Apuesta(double montoInicial) {
        if (montoInicial <= 0) {
            throw new IllegalArgumentException("La apuesta inicial debe ser mayor que cero.");
        }

        this.montoInicial = montoInicial;
        this.montoTotal = montoInicial;
        this.montoAdicional = 0;
        this.liquidada = false;
    }

    // Métodos de negocio

    public boolean aumentarApuesta(double cantidad) {
        if (liquidada || cantidad <= 0) {
            return false;
        }

        montoAdicional += cantidad;
        montoTotal += cantidad;
        return true;
    }

    public double obtenerRetorno(Resultados resultado) {
        if (resultado == null) {
            throw new IllegalArgumentException("El resultado no puede ser nulo.");
        }

        switch (resultado) {
            case GANADA:
                return montoTotal * PAGO_NORMAL;
            case BLACKJACK:
                return montoTotal * PAGO_BLACKJACK;
            case EMPATE:
                return montoTotal;
            case PERDIDA:
            default:
                return 0;
        }
    }

    public void liquidar() {
        liquidada = true;
    }

    public boolean estaLiquidada() {
        return liquidada;
    }

    // Métodos de encapsulamiento

    public double obtenerMontoInicial() {
        return montoInicial;
    }

    public void modificarMontoInicial(double montoInicial) {
        if (montoInicial <= 0) {
            throw new IllegalArgumentException("El monto inicial debe ser mayor que cero.");
        }
        this.montoInicial = montoInicial;
    }

    public double obtenerMontoTotal() {
        return montoTotal;
    }

    public void modificarMontoTotal(double montoTotal) {
        if (montoTotal < 0) {
            throw new IllegalArgumentException("El monto total no puede ser negativo.");
        }
        this.montoTotal = montoTotal;
    }

    public double obtenerMontoAdicional() {
        return montoAdicional;
    }

    public void modificarMontoAdicional(double montoAdicional) {
        if (montoAdicional < 0) {
            throw new IllegalArgumentException("El monto adicional no puede ser negativo.");
        }
        this.montoAdicional = montoAdicional;
    }

    public boolean obtenerLiquidada() {
        return liquidada;
    }

    public void modificarLiquidada(boolean liquidada) {
        this.liquidada = liquidada;
    }
}
