package co.edu.unilibre.model;


import java.util.List;

public class Mano {

    private List<Carta> cartas;

    public Mano() {
    }

    public void agregarCarta(Carta carta) {
    }

    public int calcularPuntuacion() {
        return 0;
    }

    public boolean esBlackjackNatural() {
        return false;
    }

    public boolean haSuperadoVeintiuno() {
        return false;
    }

    public void limpiar() {
    }

    public List<Carta> obtenerCartas() {
        return cartas;
    }

    public void modificarCartas(List<Carta> cartas) {
        this.cartas = cartas;
    }
}
