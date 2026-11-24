package co.edu.unilibre.model;

import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;

public class Carta {

    private Palo palo;
    private ValorCarta valor;
    private int valorNumerico;

    public Carta(Palo palo, ValorCarta valor) {
        if (palo == null || valor == null) {
            throw new IllegalArgumentException("El palo y el valor son obligatorios.");
        }
        this.palo = palo;
        this.valor = valor;
        this.valorNumerico = calcularValorNumerico(valor);
    }

    private int calcularValorNumerico(ValorCarta valor) {
        switch (valor) {
            case AS:
                return 11;
            case J:
                return 10;
            case Q:
                return 10;
            case K:
                return 10;
            case DIEZ:
                return 10;
            default:
                return valor.ordinal() + 1;
        }
    }

    // Métodos de negocio

    public int obtenerValor() {
        return valorNumerico;
    }

    public boolean esAs() {
        return valor == ValorCarta.AS;
    }

    public boolean esFigura() {
        return valor == ValorCarta.J || valor == ValorCarta.Q || valor == ValorCarta.K;
    }

    // Métodos de encapsulamiento

    public Palo obtenerPalo() {
        return palo;
    }

    public void modificarPalo(Palo palo) {
        if (palo == null) {
            throw new IllegalArgumentException("El palo no puede ser nulo.");
        }
        this.palo = palo;
    }

    public ValorCarta obtenerValorCarta() {
        return valor;
    }

    public void modificarValorCarta(ValorCarta valor) {
        if (valor == null) {
            throw new IllegalArgumentException("El valor no puede ser nulo.");
        }
        this.valor = valor;
        this.valorNumerico = calcularValorNumerico(valor);
    }

    public int obtenerValorNumerico() {
        return valorNumerico;
    }

    public void modificarValorNumerico(int valorNumerico) {
        if (valorNumerico < 1 || valorNumerico > 11) {
            throw new IllegalArgumentException("El valor numérico debe estar entre 1 y 11.");
        }
        this.valorNumerico = valorNumerico;
    }
}