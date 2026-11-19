package co.edu.unilibre.factory;

import co.edu.unilibre.model.Mazo;

public class MazoFactory {

    private CartaFactory cartaFactory;

    public MazoFactory(CartaFactory cartaFactory) {
        this.cartaFactory = cartaFactory;
    }

    public Mazo createMazo() {
        Mazo mazo = new Mazo();
        return mazo;
    }

}
