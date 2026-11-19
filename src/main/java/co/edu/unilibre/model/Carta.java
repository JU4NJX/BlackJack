package co.edu.unilibre.model;

import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;

public class Carta {

    private Palo palo;
    private ValorCarta valor;

    public Carta(Palo palo, ValorCarta valor) {
        this.palo = palo;
        this.valor = valor;
    }

    public int obtenerValorNumerico() {
        return 0;
    }

    public boolean esAs(){
        return false;
    }

}
