package model;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mano;
import co.edu.unilibre.model.ManoJugador;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.Resultados;
import co.edu.unilibre.model.enums.ValorCarta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManoTest {

    private Mano mano;

    @BeforeEach
    void configurar() {
        mano = new Mano();
    }


    @Test
    void manoVacia_DebeTenerCeroPuntosYCeroCartas() {
        assertEquals(0, mano.calcularPuntuacion());
        assertEquals(0, mano.obtenerCantidadCartas());
        assertFalse(mano.tieneBlackjackNatural());
        assertFalse(mano.superaVeintiuno());
    }

    @Test
    void agregarCarta_DebeRechazarCartaNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> mano.agregarCarta(null)
        );
    }

    @Test
    void calcularPuntuacion_DebeSumarCartasNumericasYFiguras() {
        mano.agregarCarta(new Carta(Palo.PICAS, ValorCarta.SIETE));
        mano.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.K));

        assertEquals(17, mano.calcularPuntuacion());
        assertEquals(2, mano.obtenerCantidadCartas());
    }

    @Test
    void calcularPuntuacion_DebeAjustarAsDe11A1CuandoSupera21() {
        mano.agregarCarta(new Carta(Palo.PICAS, ValorCarta.AS));
        mano.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.NUEVE));
        assertEquals(20, mano.calcularPuntuacion());

        mano.agregarCarta(new Carta(Palo.TREBOLES, ValorCarta.CINCO));
        assertEquals(15, mano.calcularPuntuacion());
        assertFalse(mano.superaVeintiuno());
    }

    @Test
    void calcularPuntuacion_DebeAjustarMultiplesAsesCorrectamente() {
        mano.agregarCarta(new Carta(Palo.PICAS, ValorCarta.AS));
        mano.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.AS));
        assertEquals(12, mano.calcularPuntuacion());

        mano.agregarCarta(new Carta(Palo.DIAMANTES, ValorCarta.NUEVE));
        assertEquals(21, mano.calcularPuntuacion());

        mano.agregarCarta(new Carta(Palo.TREBOLES, ValorCarta.AS));
        assertEquals(12, mano.calcularPuntuacion());
    }

    @Test
    void tieneBlackjackNatural_DebeSerTrueSoloConDosCartasQueSuman21() {
        mano.agregarCarta(new Carta(Palo.PICAS, ValorCarta.AS));
        mano.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.J));

        assertTrue(mano.tieneBlackjackNatural());
    }

    @Test
    void tieneBlackjackNatural_DebeSerFalseConTresCartasQueSuman21() {
        mano.agregarCarta(new Carta(Palo.PICAS, ValorCarta.SIETE));
        mano.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.SIETE));
        mano.agregarCarta(new Carta(Palo.DIAMANTES, ValorCarta.SIETE));

        assertEquals(21, mano.calcularPuntuacion());
        assertFalse(mano.tieneBlackjackNatural());
    }

    @Test
    void superaVeintiuno_DebeDetectarCuandoSePasaDe21() {
        mano.agregarCarta(new Carta(Palo.PICAS, ValorCarta.DIEZ));
        mano.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.Q));
        mano.agregarCarta(new Carta(Palo.DIAMANTES, ValorCarta.DOS));

        assertEquals(22, mano.calcularPuntuacion());
        assertTrue(mano.superaVeintiuno());
    }

    @Test
    void obtenerCartas_DebeRetornarListaInmodificable() {
        mano.agregarCarta(new Carta(Palo.PICAS, ValorCarta.CINCO));
        List<Carta> cartas = mano.obtenerCartas();

        assertThrows(
                UnsupportedOperationException.class,
                () -> cartas.add(new Carta(Palo.CORAZONES, ValorCarta.DOS))
        );
    }

    @Test
    void modificarCartas_DebeCopiarListaYRechazarNula() {
        List<Carta> lista = new ArrayList<>();
        lista.add(new Carta(Palo.PICAS, ValorCarta.OCHO));

        mano.modificarCartas(lista);
        lista.clear();

        assertEquals(1, mano.obtenerCantidadCartas());
        assertThrows(IllegalArgumentException.class, () -> mano.modificarCartas(null));
    }


    @Test
    void manoJugador_DebeRechazarApuestaNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ManoJugador(null)
        );
    }

    @Test
    void manoJugador_PlantarseDebeFinalizarYImpedirPedirCarta() {
        ManoJugador manoJugador = new ManoJugador(new Apuesta(100.0));
        manoJugador.agregarCarta(new Carta(Palo.PICAS, ValorCarta.CINCO));
        manoJugador.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.SEIS));

        assertTrue(manoJugador.puedePedirCarta());
        assertFalse(manoJugador.estaFinalizada());

        manoJugador.plantarse();

        assertTrue(manoJugador.obtenerPlantada());
        assertTrue(manoJugador.estaFinalizada());
        assertFalse(manoJugador.puedePedirCarta());
    }

    @Test
    void manoJugador_DoblarDebeDuplicarApuestaYMarcarDoblada() {
        Apuesta apuesta = new Apuesta(100.0);
        ManoJugador manoJugador = new ManoJugador(apuesta);
        manoJugador.agregarCarta(new Carta(Palo.PICAS, ValorCarta.CINCO));
        manoJugador.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.SEIS));

        assertTrue(manoJugador.puedeDoblar());

        manoJugador.doblar();

        assertTrue(manoJugador.obtenerDoblada());
        assertEquals(200.0, apuesta.obtenerMontoTotal(), 0.001);
        assertFalse(manoJugador.puedePedirCarta());
        assertFalse(manoJugador.puedeDoblar());
    }

    @Test
    void manoJugador_DoblarDebeFallarCuandoNoCumpleCondiciones() {
        ManoJugador manoJugador = new ManoJugador(new Apuesta(100.0));
        manoJugador.agregarCarta(new Carta(Palo.PICAS, ValorCarta.CINCO));

        // Solo tiene una carta
        assertFalse(manoJugador.puedeDoblar());
        assertThrows(IllegalStateException.class, manoJugador::doblar);

        // Con dos cartas pero apuesta liquidada
        manoJugador.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.SEIS));
        manoJugador.obtenerApuesta().liquidar();
        assertFalse(manoJugador.puedeDoblar());
        assertThrows(IllegalStateException.class, manoJugador::doblar);
    }

    @Test
    void manoJugador_PuedeDividirConCartasDelMismoValor() {
        ManoJugador manoJugador = new ManoJugador(new Apuesta(100.0));
        manoJugador.agregarCarta(new Carta(Palo.PICAS, ValorCarta.OCHO));
        manoJugador.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.OCHO));

        assertTrue(manoJugador.puedeDividir());

        manoJugador.modificarDividida(true);
        assertFalse(manoJugador.puedeDividir());
    }

    @Test
    void manoJugador_NoPuedeDividirCartasDeDiferenteValorCarta() {
        ManoJugador manoJugador = new ManoJugador(new Apuesta(100.0));
        manoJugador.agregarCarta(new Carta(Palo.PICAS, ValorCarta.DIEZ));
        manoJugador.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.K));

        assertFalse(manoJugador.puedeDividir());
    }

    @Test
    void manoJugador_ReglaEspecialDivisionDeAses() {
        ManoJugador manoJugador = new ManoJugador(new Apuesta(100.0));
        manoJugador.agregarCarta(new Carta(Palo.PICAS, ValorCarta.AS));
        manoJugador.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.AS));

        assertTrue(manoJugador.puedeDividir());

        manoJugador.modificarAsDividido(true);
        assertTrue(manoJugador.obtenerAsDividido());
        assertFalse(manoJugador.puedeDividir());
    }

    @Test
    void manoJugador_BlackjackNaturalFinalizaManoYImpidePedirCarta() {
        ManoJugador manoJugador = new ManoJugador(new Apuesta(100.0));
        manoJugador.agregarCarta(new Carta(Palo.PICAS, ValorCarta.AS));
        manoJugador.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.K));

        assertTrue(manoJugador.estaFinalizada());
        assertFalse(manoJugador.puedePedirCarta());
        assertFalse(manoJugador.puedeDoblar());
    }

    @Test
    void manoJugador_EncapsulamientoCompleto() {
        ManoJugador manoJugador = new ManoJugador(new Apuesta(100.0));
        Apuesta nuevaApuesta = new Apuesta(250.0);

        manoJugador.modificarApuesta(nuevaApuesta);
        assertSame(nuevaApuesta, manoJugador.obtenerApuesta());
        assertThrows(IllegalArgumentException.class, () -> manoJugador.modificarApuesta(null));

        manoJugador.modificarResultado(Resultados.GANADA);
        assertEquals(Resultados.GANADA, manoJugador.obtenerResultado());

        manoJugador.modificarPlantada(true);
        assertTrue(manoJugador.obtenerPlantada());

        manoJugador.modificarDoblada(true);
        assertTrue(manoJugador.obtenerDoblada());

        manoJugador.modificarDividida(true);
        assertTrue(manoJugador.obtenerDividida());

        manoJugador.finalizarMano();
        assertTrue(manoJugador.estaFinalizada());
    }
}
