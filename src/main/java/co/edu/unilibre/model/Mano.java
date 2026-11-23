package co.edu.unilibre.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mano {

    protected List<Carta> cartas;

    public Mano() {
        this.cartas = new ArrayList<>();
    }

    // Métodos de negocio

    public void agregarCarta(Carta carta) {
        if (carta == null) {
            throw new IllegalArgumentException("No se puede agregar una carta nula.");
        }

        cartas.add(carta);
    }

    public int calcularPuntuacion() {
        int puntuacion = 0;
        int cantidadAses = 0;

        for (Carta carta : cartas) {
            puntuacion += carta.obtenerValor();

            if (carta.esAs()) {
                cantidadAses++;
            }
        }

        while (puntuacion > 21 && cantidadAses > 0) {
            puntuacion -= 10;
            cantidadAses--;
        }

        return puntuacion;
    }

    public boolean tieneBlackjackNatural() {
        return cartas.size() == 2 && calcularPuntuacion() == 21;
    }

    public boolean superaVeintiuno() {
        return calcularPuntuacion() > 21;
    }

    public int obtenerCantidadCartas() {
        return cartas.size();
    }

    public List<Carta> obtenerCartas() {
        return Collections.unmodifiableList(cartas);
    }

    // Método de encapsulamiento

    public void modificarCartas(List<Carta> cartas) {
        if (cartas == null) {
            throw new IllegalArgumentException("La lista de cartas no puede ser nula.");
        }

        this.cartas = new ArrayList<>(cartas);
    }
}