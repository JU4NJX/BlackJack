package strategy;

import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mano;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;
import co.edu.unilibre.strategy.EstrategiaClasica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstrategiaClasicaTest {

    private EstrategiaClasica estrategia;

    @BeforeEach
    void configurar() {
        estrategia = new EstrategiaClasica();
    }

    private Mano crearMano(ValorCarta... valores) {
        Mano mano = new Mano();
        for (ValorCarta valor : valores) {
            mano.agregarCarta(new Carta(Palo.PICAS, valor));
        }
        return mano;
    }

    @Test
    void debePedirCarta_CuandoPuntuacionEsMenorA17() {
        assertTrue(estrategia.debePedirCarta(crearMano(ValorCarta.DIEZ, ValorCarta.SEIS)));
        assertTrue(estrategia.debePedirCarta(crearMano(ValorCarta.AS, ValorCarta.CINCO)));
    }

    @Test
    void noDebePedirCarta_CuandoPuntuacionEs17OMayor() {
        assertFalse(estrategia.debePedirCarta(crearMano(ValorCarta.DIEZ, ValorCarta.SIETE)));
        assertFalse(estrategia.debePedirCarta(crearMano(ValorCarta.AS, ValorCarta.SEIS)));
        assertFalse(estrategia.debePedirCarta(crearMano(ValorCarta.DIEZ, ValorCarta.K)));
    }
}
