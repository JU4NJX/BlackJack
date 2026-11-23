package co.edu.unilibre.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo {

    private List<Carta> cartas;

    public Mazo() {
        this.cartas = new ArrayList<>();
    }

    // Métodos de negocio

    public void barajar() {
        Collections.shuffle(cartas);
    }

    public Carta repartirCarta() {
        if (estaVacio()) {
            throw new IllegalStateException("No hay cartas disponibles en el mazo.");
        }

        return cartas.remove(0);
    }

    public boolean estaVacio() {
        return cartas.isEmpty();
    }

    public void agregarCarta(Carta carta) {
        if (carta == null) {
            throw new IllegalArgumentException("No se puede agregar una carta nula.");
        }

        cartas.add(carta);
    }

    public int cantidadCartas() {
        return cartas.size();
    }

    // Métodos de encapsulamiento

    public List<Carta> obtenerCartas() {
        return Collections.unmodifiableList(cartas);
    }

    public void modificarCartas(List<Carta> cartas) {
        if (cartas == null) {
            throw new IllegalArgumentException("La lista de cartas no puede ser nula.");
        }

        this.cartas = new ArrayList<>(cartas);
    }
}
