package model;

import co.edu.unilibre.factory.CartaFactoryEstandar;
import co.edu.unilibre.factory.MazoFactory;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mazo;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MazoTest {

    private Mazo mazo;

    @BeforeEach
    void configurar() {
        mazo = new Mazo();
    }

    @Test
    void mazoNuevo_DebeEstarVacio() {
        assertTrue(mazo.estaVacio());
        assertEquals(0, mazo.cantidadCartas());
        assertTrue(mazo.obtenerCartas().isEmpty());
    }

    @Test
    void agregarCarta_DebeIncrementarCantidadYRechazarNula() {
        Carta carta = new Carta(Palo.PICAS, ValorCarta.AS);
        mazo.agregarCarta(carta);

        assertFalse(mazo.estaVacio());
        assertEquals(1, mazo.cantidadCartas());

        assertThrows(IllegalArgumentException.class, () -> mazo.agregarCarta(null));
    }

    @Test
    void repartirCarta_DebeRepartirEnOrdenYReducirCantidad() {
        Carta primera = new Carta(Palo.PICAS, ValorCarta.DOS);
        Carta segunda = new Carta(Palo.CORAZONES, ValorCarta.TRES);

        mazo.agregarCarta(primera);
        mazo.agregarCarta(segunda);

        assertSame(primera, mazo.repartirCarta());
        assertEquals(1, mazo.cantidadCartas());

        assertSame(segunda, mazo.repartirCarta());
        assertTrue(mazo.estaVacio());
    }

    @Test
    void repartirCarta_DebeLanzarExcepcionCuandoMazoEstaVacio() {
        assertThrows(
                IllegalStateException.class,
                () -> mazo.repartirCarta()
        );
    }

    @Test
    void barajar_DebeConservarTodasLasCartasSinDuplicados() {
        MazoFactory factory = new MazoFactory(new CartaFactoryEstandar());
        Mazo mazoCompleto = factory.crearMazo();

        mazoCompleto.barajar();

        assertEquals(52, mazoCompleto.cantidadCartas());
        Set<String> cartasUnicas = new HashSet<>();
        for (Carta carta : mazoCompleto.obtenerCartas()) {
            cartasUnicas.add(carta.obtenerPalo() + "_" + carta.obtenerValorCarta());
        }
        assertEquals(52, cartasUnicas.size());
    }

    @Test
    void obtenerCartas_DebeRetornarListaInmodificable() {
        mazo.agregarCarta(new Carta(Palo.DIAMANTES, ValorCarta.CUATRO));
        List<Carta> cartas = mazo.obtenerCartas();

        assertThrows(
                UnsupportedOperationException.class,
                () -> cartas.add(new Carta(Palo.TREBOLES, ValorCarta.CINCO))
        );
    }

    @Test
    void modificarCartas_DebeCopiarListaYRechazarNula() {
        List<Carta> lista = new ArrayList<>();
        lista.add(new Carta(Palo.PICAS, ValorCarta.NUEVE));
        lista.add(new Carta(Palo.CORAZONES, ValorCarta.DIEZ));

        mazo.modificarCartas(lista);
        lista.clear();

        assertEquals(2, mazo.cantidadCartas());
        assertThrows(IllegalArgumentException.class, () -> mazo.modificarCartas(null));
    }
}
