package co.edu.unilibre.strategy;

import co.edu.unilibre.model.Mano;

public class EstrategiaClasica implements EstrategiaCrupier {

    @Override
    public boolean debePedirCarta(Mano mano) {
        return mano.calcularPuntuacion() < 17;
    }
}