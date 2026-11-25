package factory;

import co.edu.unilibre.factory.CartaFactory;
import co.edu.unilibre.factory.CartaFactoryEstandar;
import co.edu.unilibre.factory.MazoFactory;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mazo;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartaFactoryTest {

    private CartaFactoryEstandar cartaFactory;
    private MazoFactory mazoFactory;

    @BeforeEach
    void configurar() {
        cartaFactory = new CartaFactoryEstandar();
        mazoFactory = new MazoFactory(cartaFactory);
    }



    @Test
    void crearCarta_DebeCrearCartaConPaloYValorValidos() {
        Carta carta = cartaFactory.crearCarta(Palo.CORAZONES, ValorCarta.AS);

        assertNotNull(carta);
        assertEquals(Palo.CORAZONES, carta.obtenerPalo());
        assertEquals(ValorCarta.AS, carta.obtenerValorCarta());
        assertEquals(11, carta.obtenerValor());
    }

    @Test
    void crearCarta_DebeCrearTodasLasCombinacionesPosibles() {
        for (Palo palo : Palo.values()) {
            for (ValorCarta valor : ValorCarta.values()) {
                Carta carta = cartaFactory.crearCarta(palo, valor);
                assertNotNull(carta);
                assertEquals(palo, carta.obtenerPalo());
                assertEquals(valor, carta.obtenerValorCarta());
            }
        }
    }

    @Test
    void crearCarta_DebeRechazarPaloNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartaFactory.crearCarta(null, ValorCarta.DIEZ)
        );
    }

    @Test
    void crearCarta_DebeRechazarValorNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartaFactory.crearCarta(Palo.PICAS, null)
        );
    }



    @Test
    void mazoFactory_DebeRechazarCartaFactoryNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MazoFactory(null)
        );
    }

    @Test
    void crearMazo_DebeGenerar52CartasUnicas() {
        Mazo mazo = mazoFactory.crearMazo();

        assertNotNull(mazo);
        assertEquals(52, mazo.cantidadCartas());

        Set<String> clavesUnicas = new HashSet<>();
        for (Carta carta : mazo.obtenerCartas()) {
            clavesUnicas.add(carta.obtenerPalo().name() + "_" + carta.obtenerValorCarta().name());
        }

        assertEquals(52, clavesUnicas.size());
    }

    @Test
    void obtenerYModificarCartaFactory_DebeFuncionarCorrectamente() {
        assertSame(cartaFactory, mazoFactory.obtenerCartaFactory());

        CartaFactory nuevaFactory = new CartaFactoryEstandar();
        mazoFactory.modificarCartaFactory(nuevaFactory);

        assertSame(nuevaFactory, mazoFactory.obtenerCartaFactory());
    }

    @Test
    void modificarCartaFactory_DebeRechazarNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> mazoFactory.modificarCartaFactory(null)
        );
    }
}
