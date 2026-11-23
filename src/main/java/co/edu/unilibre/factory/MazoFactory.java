package co.edu.unilibre.factory;

import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mazo;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;

public class MazoFactory {

    private CartaFactory cartaFactory;

    public MazoFactory(CartaFactory cartaFactory) {
        if (cartaFactory == null) {
            throw new IllegalArgumentException(
                    "La fábrica de cartas no puede ser nula."
            );
        }

        this.cartaFactory = cartaFactory;
    }

    public Mazo crearMazo() {
        Mazo mazo = new Mazo();

        for (Palo palo : Palo.values()) {
            for (ValorCarta valor : ValorCarta.values()) {

                Carta carta = cartaFactory.crearCarta(palo, valor);

                mazo.agregarCarta(carta);
            }
        }

        mazo.barajar();

        return mazo;
    }

    // Métodos de encapsulamiento

    public CartaFactory getCartaFactory() {
        return cartaFactory;
    }

    public void setCartaFactory(CartaFactory cartaFactory) {
        if (cartaFactory == null) {
            throw new IllegalArgumentException(
                    "La fábrica de cartas no puede ser nula."
            );
        }

        this.cartaFactory = cartaFactory;
    }

}
