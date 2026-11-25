package model;

import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartaTest {

    @Test
    void constructor_DebeCrearCartaValida() {
        Carta carta = new Carta(Palo.PICAS, ValorCarta.K);

        assertEquals(Palo.PICAS, carta.obtenerPalo());
        assertEquals(ValorCarta.K, carta.obtenerValorCarta());
        assertEquals(10, carta.obtenerValor());
        assertEquals(10, carta.obtenerValorNumerico());
    }

    @Test
    void constructor_DebeRechazarPaloNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Carta(null, ValorCarta.CINCO)
        );
    }

    @Test
    void constructor_DebeRechazarValorNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Carta(Palo.TREBOLES, null)
        );
    }

    @Test
    void calcularValorNumerico_DebeAsignarValoresCorrectosATodasLasCartas() {
        assertEquals(11, new Carta(Palo.PICAS, ValorCarta.AS).obtenerValor());
        assertEquals(2, new Carta(Palo.PICAS, ValorCarta.DOS).obtenerValor());
        assertEquals(3, new Carta(Palo.PICAS, ValorCarta.TRES).obtenerValor());
        assertEquals(4, new Carta(Palo.PICAS, ValorCarta.CUATRO).obtenerValor());
        assertEquals(5, new Carta(Palo.PICAS, ValorCarta.CINCO).obtenerValor());
        assertEquals(6, new Carta(Palo.PICAS, ValorCarta.SEIS).obtenerValor());
        assertEquals(7, new Carta(Palo.PICAS, ValorCarta.SIETE).obtenerValor());
        assertEquals(8, new Carta(Palo.PICAS, ValorCarta.OCHO).obtenerValor());
        assertEquals(9, new Carta(Palo.PICAS, ValorCarta.NUEVE).obtenerValor());
        assertEquals(10, new Carta(Palo.PICAS, ValorCarta.DIEZ).obtenerValor());
        assertEquals(10, new Carta(Palo.PICAS, ValorCarta.J).obtenerValor());
        assertEquals(10, new Carta(Palo.PICAS, ValorCarta.Q).obtenerValor());
        assertEquals(10, new Carta(Palo.PICAS, ValorCarta.K).obtenerValor());
    }

    @Test
    void esAs_DebeIdentificarUnicamenteAlAs() {
        assertTrue(new Carta(Palo.CORAZONES, ValorCarta.AS).esAs());
        assertFalse(new Carta(Palo.CORAZONES, ValorCarta.DOS).esAs());
        assertFalse(new Carta(Palo.CORAZONES, ValorCarta.K).esAs());
    }

    @Test
    void esFigura_DebeIdentificarSoloJQyK() {
        assertTrue(new Carta(Palo.DIAMANTES, ValorCarta.J).esFigura());
        assertTrue(new Carta(Palo.DIAMANTES, ValorCarta.Q).esFigura());
        assertTrue(new Carta(Palo.DIAMANTES, ValorCarta.K).esFigura());
        assertFalse(new Carta(Palo.DIAMANTES, ValorCarta.DIEZ).esFigura());
        assertFalse(new Carta(Palo.DIAMANTES, ValorCarta.AS).esFigura());
        assertFalse(new Carta(Palo.DIAMANTES, ValorCarta.SIETE).esFigura());
    }

    @Test
    void modificarPalo_DebeActualizarPaloYRechazarNulo() {
        Carta carta = new Carta(Palo.PICAS, ValorCarta.OCHO);

        carta.modificarPalo(Palo.DIAMANTES);
        assertEquals(Palo.DIAMANTES, carta.obtenerPalo());

        assertThrows(IllegalArgumentException.class, () -> carta.modificarPalo(null));
    }

    @Test
    void modificarValorCarta_DebeRecalcularValorNumericoYRechazarNulo() {
        Carta carta = new Carta(Palo.CORAZONES, ValorCarta.TRES);
        assertEquals(3, carta.obtenerValor());

        carta.modificarValorCarta(ValorCarta.AS);
        assertEquals(ValorCarta.AS, carta.obtenerValorCarta());
        assertEquals(11, carta.obtenerValor());

        carta.modificarValorCarta(ValorCarta.Q);
        assertEquals(10, carta.obtenerValor());

        assertThrows(IllegalArgumentException.class, () -> carta.modificarValorCarta(null));
    }

    @Test
    void modificarValorNumerico_DebeValidarRangoPermitido() {
        Carta carta = new Carta(Palo.TREBOLES, ValorCarta.AS);

        carta.modificarValorNumerico(1);
        assertEquals(1, carta.obtenerValorNumerico());

        carta.modificarValorNumerico(11);
        assertEquals(11, carta.obtenerValorNumerico());

        assertThrows(IllegalArgumentException.class, () -> carta.modificarValorNumerico(0));
        assertThrows(IllegalArgumentException.class, () -> carta.modificarValorNumerico(-5));
        assertThrows(IllegalArgumentException.class, () -> carta.modificarValorNumerico(12));
    }
}
