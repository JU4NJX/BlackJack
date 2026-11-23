package service;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.enums.Resultado;

import co.edu.unilibre.service.ApuestaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApuestaServiceTest {

    private ApuestaService apuestaService;

    @BeforeEach
    void configurar() {
        apuestaService = new ApuestaService();
    }

    // =========================================
    // VALIDAR APUESTA
    // =========================================

    @Test
    void validarApuesta_DebeAceptarApuestaValida() {

        boolean resultado = apuestaService.validarApuesta(
                2000.0,
                100.0
        );

        assertTrue(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarSaldoInsuficiente() {

        boolean resultado = apuestaService.validarApuesta(
                50.0,
                100.0
        );

        assertFalse(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarMontoNegativo() {

        boolean resultado = apuestaService.validarApuesta(
                2000.0,
                -100.0
        );

        assertFalse(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarMontoCero() {

        boolean resultado = apuestaService.validarApuesta(
                2000.0,
                0.0
        );

        assertFalse(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarSaldoNegativo() {

        boolean resultado = apuestaService.validarApuesta(
                -100.0,
                50.0
        );

        assertFalse(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarSaldoNoFinito() {

        assertFalse(apuestaService.validarApuesta(
                Double.NaN,
                100.0
        ));

        assertFalse(apuestaService.validarApuesta(
                Double.POSITIVE_INFINITY,
                100.0
        ));
    }

    // =========================================
    // CALCULAR RETORNO
    // =========================================

    @Test
    void calcularRetorno_DebeCalcularVictoria() {

        Apuesta apuesta = new Apuesta(100.0);

        double retorno = apuestaService.calcularRetorno(
                apuesta,
                Resultado.GANADA
        );

        assertEquals(200.0, retorno, 0.001);
    }

    @Test
    void calcularRetorno_DebeCalcularBlackjack() {

        Apuesta apuesta = new Apuesta(100.0);

        double retorno = apuestaService.calcularRetorno(
                apuesta,
                Resultado.BLACKJACK
        );

        assertEquals(250.0, retorno, 0.001);
    }

    @Test
    void calcularRetorno_DebeCalcularEmpate() {

        Apuesta apuesta = new Apuesta(100.0);

        double retorno = apuestaService.calcularRetorno(
                apuesta,
                Resultado.EMPATE
        );

        assertEquals(100.0, retorno, 0.001);
    }

    @Test
    void calcularRetorno_DebeCalcularPerdida() {

        Apuesta apuesta = new Apuesta(100.0);

        double retorno = apuestaService.calcularRetorno(
                apuesta,
                Resultado.PERDIDA
        );

        assertEquals(0.0, retorno, 0.001);
    }

    @Test
    void calcularRetorno_DebeRechazarApuestaNula() {

        assertThrows(
                IllegalArgumentException.class,
                () -> apuestaService.calcularRetorno(
                        null,
                        Resultado.GANADA
                )
        );
    }

    @Test
    void calcularRetorno_DebeRechazarResultadoNulo() {

        Apuesta apuesta = new Apuesta(100.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> apuestaService.calcularRetorno(
                        apuesta,
                        null
                )
        );
    }

}
