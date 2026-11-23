package service;

import co.edu.unilibre.factory.CartaFactory;
import co.edu.unilibre.factory.MazoFactory;
import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Mano;
import co.edu.unilibre.model.Partida;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;
import co.edu.unilibre.observer.Observador;
import co.edu.unilibre.service.JuegoService;
import co.edu.unilibre.strategy.EstrategiaCrupier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JuegoServiceTest {

    private JuegoService juegoService;
    private EstrategiaCrupier estrategiaCrupier;
    private MazoFactory mazoFactory;
    private ApuestaServicePrueba apuestaService;

    @BeforeEach
    void configurar() {

        estrategiaCrupier = mano -> false;

        CartaFactory cartaFactory = new CartaFactory() {
            @Override
            public co.edu.unilibre.model.Carta crearCarta(
                    Palo palo,
                    ValorCarta valor) {
                return new co.edu.unilibre.model.Carta(palo, valor);
            }
        };

        mazoFactory = new MazoFactory(cartaFactory);
        apuestaService = new ApuestaServicePrueba();

        juegoService = new JuegoService(
                estrategiaCrupier,
                mazoFactory,
                apuestaService
        );
    }

    // =====================================================
    // PRUEBAS DE CONSTRUCTOR
    // =====================================================

    @Test
    void constructorConDependenciasValidas() {

        assertNotNull(juegoService);
    }

    @Test
    void constructorConEstrategiaNula() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new JuegoService(null, mazoFactory)
        );
    }

    @Test
    void constructorConMazoFactoryNula() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new JuegoService(estrategiaCrupier, null)
        );
    }

    @Test
    void constructorConAmbasDependenciasNulas() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new JuegoService(null, null)
        );
    }

    @Test
    void constructorConApuestaServiceNulo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new JuegoService(
                        estrategiaCrupier,
                        mazoFactory,
                        null
                )
        );
    }

    // =====================================================
    // PRUEBAS DE OBSERVADORES
    // =====================================================

    @Test
    void agregarObservadorNulo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> juegoService.agregarObservador(null)
        );
    }

    @Test
    void agregarObservadorValido() {

        Observador observador = dto -> {};

        assertDoesNotThrow(
                () -> juegoService.agregarObservador(observador)
        );
    }

    @Test
    void agregarObservadorDuplicado() {

        Observador observador = dto -> {};

        juegoService.agregarObservador(observador);
        juegoService.agregarObservador(observador);

        assertDoesNotThrow(
                () -> juegoService.eliminarObservador(observador)
        );
    }

    @Test
    void eliminarObservadorInexistente() {

        Observador observador = dto -> {};

        assertDoesNotThrow(
                () -> juegoService.eliminarObservador(observador)
        );
    }

    @Test
    void eliminarObservadorNulo() {

        assertDoesNotThrow(
                () -> juegoService.eliminarObservador(null)
        );
    }

    @Test
    void notificarSinPartidaActiva() {

        assertDoesNotThrow(
                () -> juegoService.notificarObservadores()
        );
    }

    // =====================================================
    // PRUEBAS SIN PARTIDA ACTIVA
    // =====================================================

    @Test
    void pedirCartaSinPartidaActiva() {

        assertThrows(
                IllegalStateException.class,
                () -> juegoService.pedirCarta(0)
        );
    }

    @Test
    void plantarseSinPartidaActiva() {

        assertThrows(
                IllegalStateException.class,
                () -> juegoService.plantarse(0)
        );
    }

    @Test
    void doblarApuestaSinPartidaActiva() {

        assertThrows(
                IllegalStateException.class,
                () -> juegoService.doblarApuesta(0)
        );
    }

    @Test
    void dividirManoSinPartidaActiva() {

        assertThrows(
                IllegalStateException.class,
                () -> juegoService.dividirMano()
        );
    }

    @Test
    void ejecutarTurnoCrupierSinPartidaActiva() {

        assertThrows(
                IllegalStateException.class,
                () -> juegoService.ejecutarTurnoCrupier()
        );
    }

    @Test
    void finalizarPartidaSinPartidaActiva() {

        assertThrows(
                IllegalStateException.class,
                () -> juegoService.finalizarPartida()
        );
    }
}