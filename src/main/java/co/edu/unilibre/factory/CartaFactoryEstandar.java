package co.edu.unilibre.factory;

import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;

public class CartaFactoryEstandar extends CartaFactory {

    @Override
    public Carta crearCarta(Palo palo, ValorCarta valor) {
        return new Carta(palo, valor);
    }

}
