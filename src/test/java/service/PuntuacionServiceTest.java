package service;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mano;
import co.edu.unilibre.model.ManoJugador;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.Resultados;
import co.edu.unilibre.model.enums.ValorCarta;
import co.edu.unilibre.service.PuntuacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PuntuacionServiceTest {

    private PuntuacionService puntuacionService;

    @BeforeEach
    void configurar() {
        puntuacionService = new PuntuacionService();
    }

    private Mano crearMano(ValorCarta... valores) {
        Mano mano = new Mano();
        for (ValorCarta valor : valores) {
            mano.agregarCarta(new Carta(Palo.PICAS, valor));
        }
        return mano;
    }

    private ManoJugador crearManoJugador(ValorCarta... valores) {
        ManoJugador mano = new ManoJugador(new Apuesta(100.0));
        for (ValorCarta valor : valores) {
            mano.agregarCarta(new Carta(Palo.CORAZONES, valor));
        }
        return mano;
    }

    // =========================================
    // CALCULAR PUNTUACION
    // =========================================

    @Test
    void calcularPuntuacion_DebeCalcularCorrectamente() {
        Mano mano = crearMano(ValorCarta.AS, ValorCarta.SIETE, ValorCarta.CINCO);
        assertEquals(13, puntuacionService.calcularPuntuacion(mano));
    }

    @Test
    void calcularPuntuacion_DebeRechazarManoNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> puntuacionService.calcularPuntuacion(null)
        );
    }

    // =========================================
    // TIENE BLACKJACK
    // =========================================

    @Test
    void tieneBlackjack_DebeIdentificarBlackjackNatural() {
        assertTrue(puntuacionService.tieneBlackjack(crearMano(ValorCarta.AS, ValorCarta.K)));
        assertFalse(puntuacionService.tieneBlackjack(crearMano(ValorCarta.SIETE, ValorCarta.SIETE, ValorCarta.SIETE)));
        assertFalse(puntuacionService.tieneBlackjack(crearMano(ValorCarta.DIEZ, ValorCarta.NUEVE)));
    }

    @Test
    void tieneBlackjack_DebeRechazarManoNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> puntuacionService.tieneBlackjack(null)
        );
    }

    // =========================================
    // DETERMINAR RESULTADO
    // =========================================

    @Test
    void determinarResultado_DebeRechazarManosNulas() {
        ManoJugador jugador = crearManoJugador(ValorCarta.DIEZ, ValorCarta.OCHO);
        Mano crupier = crearMano(ValorCarta.DIEZ, ValorCarta.SIETE);

        assertThrows(IllegalArgumentException.class, () -> puntuacionService.determinarResultado(null, crupier));
        assertThrows(IllegalArgumentException.class, () -> puntuacionService.determinarResultado(jugador, null));
    }

    @Test
    void determinarResultado_AmbosBlackjackNaturalDebeSerEmpate() {
        ManoJugador jugador = crearManoJugador(ValorCarta.AS, ValorCarta.K);
        Mano crupier = crearMano(ValorCarta.AS, ValorCarta.DIEZ);

        assertEquals(Resultados.EMPATE, puntuacionService.determinarResultado(jugador, crupier));
    }

    @Test
    void determinarResultado_SoloJugadorBlackjackDebeSerBlackjack() {
        ManoJugador jugador = crearManoJugador(ValorCarta.AS, ValorCarta.Q);
        Mano crupier = crearMano(ValorCarta.SIETE, ValorCarta.SIETE, ValorCarta.SIETE);

        assertEquals(Resultados.BLACKJACK, puntuacionService.determinarResultado(jugador, crupier));
    }

    @Test
    void determinarResultado_SoloCrupierBlackjackDebeSerPerdida() {
        ManoJugador jugador = crearManoJugador(ValorCarta.SIETE, ValorCarta.SIETE, ValorCarta.SIETE);
        Mano crupier = crearMano(ValorCarta.AS, ValorCarta.J);

        assertEquals(Resultados.PERDIDA, puntuacionService.determinarResultado(jugador, crupier));
    }

    @Test
    void determinarResultado_JugadorSupera21DebeSerPerdidaAunqueCrupierTambienSupere() {
        ManoJugador jugador = crearManoJugador(ValorCarta.DIEZ, ValorCarta.NUEVE, ValorCarta.CINCO);
        Mano crupier = crearMano(ValorCarta.DIEZ, ValorCarta.OCHO, ValorCarta.SEIS);

        assertEquals(Resultados.PERDIDA, puntuacionService.determinarResultado(jugador, crupier));
    }

    @Test
    void determinarResultado_CrupierSupera21DebeSerGanada() {
        ManoJugador jugador = crearManoJugador(ValorCarta.DIEZ, ValorCarta.OCHO);
        Mano crupier = crearMano(ValorCarta.DIEZ, ValorCarta.SEIS, ValorCarta.OCHO);

        assertEquals(Resultados.GANADA, puntuacionService.determinarResultado(jugador, crupier));
    }

    @Test
    void determinarResultado_DebeCompararPuntuacionesCuandoNingunoSePasa() {
        assertEquals(
                Resultados.GANADA,
                puntuacionService.determinarResultado(
                        crearManoJugador(ValorCarta.DIEZ, ValorCarta.NUEVE),
                        crearMano(ValorCarta.DIEZ, ValorCarta.OCHO)
                )
        );
        assertEquals(
                Resultados.PERDIDA,
                puntuacionService.determinarResultado(
                        crearManoJugador(ValorCarta.DIEZ, ValorCarta.SIETE),
                        crearMano(ValorCarta.DIEZ, ValorCarta.NUEVE)
                )
        );
        assertEquals(
                Resultados.EMPATE,
                puntuacionService.determinarResultado(
                        crearManoJugador(ValorCarta.DIEZ, ValorCarta.OCHO),
                        crearMano(ValorCarta.NUEVE, ValorCarta.NUEVE)
                )
        );
    }

    // =========================================
    // COMPARAR PUNTUACIONES
    // =========================================

    @Test
    void compararPuntuaciones_DebeRechazarManosNulas() {
        Mano mano = crearMano(ValorCarta.DIEZ, ValorCarta.OCHO);

        assertThrows(IllegalArgumentException.class, () -> puntuacionService.compararPuntuaciones(null, mano));
        assertThrows(IllegalArgumentException.class, () -> puntuacionService.compararPuntuaciones(mano, null));
    }

    @Test
    void compararPuntuaciones_DebeEvaluarTodosLosCasos() {
        assertEquals(
                Resultados.PERDIDA,
                puntuacionService.compararPuntuaciones(
                        crearMano(ValorCarta.DIEZ, ValorCarta.DIEZ, ValorCarta.DOS),
                        crearMano(ValorCarta.DIEZ, ValorCarta.SIETE)
                )
        );
        assertEquals(
                Resultados.GANADA,
                puntuacionService.compararPuntuaciones(
                        crearMano(ValorCarta.DIEZ, ValorCarta.SIETE),
                        crearMano(ValorCarta.DIEZ, ValorCarta.SEIS, ValorCarta.SIETE)
                )
        );
        assertEquals(
                Resultados.GANADA,
                puntuacionService.compararPuntuaciones(
                        crearMano(ValorCarta.DIEZ, ValorCarta.DIEZ),
                        crearMano(ValorCarta.DIEZ, ValorCarta.NUEVE)
                )
        );
        assertEquals(
                Resultados.PERDIDA,
                puntuacionService.compararPuntuaciones(
                        crearMano(ValorCarta.DIEZ, ValorCarta.OCHO),
                        crearMano(ValorCarta.DIEZ, ValorCarta.NUEVE)
                )
        );
        assertEquals(
                Resultados.EMPATE,
                puntuacionService.compararPuntuaciones(
                        crearMano(ValorCarta.DIEZ, ValorCarta.NUEVE),
                        crearMano(ValorCarta.DIEZ, ValorCarta.NUEVE)
                )
        );
    }
}
