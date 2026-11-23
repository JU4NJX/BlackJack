package co.edu.unilibre.factory;

import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;

public abstract class CartaFactory {

    public abstract Carta crearCarta(Palo palo, ValorCarta valor);

}

