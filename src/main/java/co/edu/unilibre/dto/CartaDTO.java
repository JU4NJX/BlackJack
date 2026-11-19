package co.edu.unilibre.dto;

import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;

public class CartaDTO {

    private Palo palo;
    private ValorCarta valor;
    private boolean oculta;

    public CartaDTO(Palo palo, ValorCarta valor) {
        this.palo = palo;
        this.valor = valor;
        this.oculta = true;
    }

    //ENCAPSULAMIENTO

    public Palo obtenerPalo() {
        return palo;
    }

    public void modificarPalo(Palo palo) {
        this.palo = palo;
    }

    public ValorCarta obtenerValor() {
        return valor;
    }

    public void modificarValor(ValorCarta valor) {
        this.valor = valor;
    }

    public boolean obtenerOculta() {
        return oculta;
    }

    public void modificarOculta(boolean oculta) {
        this.oculta = oculta;
    }
}
