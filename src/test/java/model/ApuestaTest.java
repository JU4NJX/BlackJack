package model;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.enums.Resultados;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApuestaTest {

    @Test
    void constructor_DebeInicializarValoresYValidarMontoPositivo() {
        Apuesta apuesta = new Apuesta(100.0);

        assertEquals(100.0, apuesta.obtenerMontoInicial(), 0.001);
        assertEquals(100.0, apuesta.obtenerMontoTotal(), 0.001);
        assertEquals(0.0, apuesta.obtenerMontoAdicional(), 0.001);
        assertFalse(apuesta.estaLiquidada());
        assertFalse(apuesta.obtenerLiquidada());

        assertThrows(IllegalArgumentException.class, () -> new Apuesta(0.0));
        assertThrows(IllegalArgumentException.class, () -> new Apuesta(-50.0));
    }

    @Test
    void aumentarApuesta_DebeSumarAlTotalYAdicionalMientrasNoEsteLiquidada() {
        Apuesta apuesta = new Apuesta(100.0);

        assertTrue(apuesta.aumentarApuesta(100.0));
        assertEquals(100.0, apuesta.obtenerMontoAdicional(), 0.001);
        assertEquals(200.0, apuesta.obtenerMontoTotal(), 0.001);

        assertFalse(apuesta.aumentarApuesta(0.0));
        assertFalse(apuesta.aumentarApuesta(-20.0));

        apuesta.liquidar();
        assertTrue(apuesta.estaLiquidada());
        assertFalse(apuesta.aumentarApuesta(50.0));
    }

    @Test
    void obtenerRetorno_DebeCalcularSegunResultadoYRechazarNulo() {
        Apuesta apuesta = new Apuesta(200.0);

        assertEquals(400.0, apuesta.obtenerRetorno(Resultados.GANADA), 0.001);
        assertEquals(500.0, apuesta.obtenerRetorno(Resultados.BLACKJACK), 0.001);
        assertEquals(200.0, apuesta.obtenerRetorno(Resultados.EMPATE), 0.001);
        assertEquals(0.0, apuesta.obtenerRetorno(Resultados.PERDIDA), 0.001);

        assertThrows(IllegalArgumentException.class, () -> apuesta.obtenerRetorno(null));
    }

    @Test
    void encapsulamiento_DebeValidarModificaciones() {
        Apuesta apuesta = new Apuesta(100.0);

        apuesta.modificarMontoInicial(150.0);
        assertEquals(150.0, apuesta.obtenerMontoInicial(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> apuesta.modificarMontoInicial(0.0));

        apuesta.modificarMontoTotal(300.0);
        assertEquals(300.0, apuesta.obtenerMontoTotal(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> apuesta.modificarMontoTotal(-1.0));

        apuesta.modificarMontoAdicional(150.0);
        assertEquals(150.0, apuesta.obtenerMontoAdicional(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> apuesta.modificarMontoAdicional(-1.0));

        apuesta.modificarLiquidada(true);
        assertTrue(apuesta.obtenerLiquidada());
    }
}
