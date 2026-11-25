package util;

import co.edu.unilibre.util.Validador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorTest {

    @Test
    void validarCorreo_DebeAceptarValidosYRechazarInvalidos() {
        assertTrue(Validador.validarCorreo("jugador@blackjack.com"));
        assertTrue(Validador.validarCorreo("nombre.apellido+1@uni.edu.co"));

        assertFalse(Validador.validarCorreo(null));
        assertFalse(Validador.validarCorreo(""));
        assertFalse(Validador.validarCorreo("sin-arroba.com"));
        assertFalse(Validador.validarCorreo("usuario@dominio"));
        assertFalse(Validador.validarCorreo("usuario@dominio.c"));
    }

    @Test
    void validarClave_DebeExigirAlMenos8Caracteres() {
        assertTrue(Validador.validarClave("12345678"));
        assertTrue(Validador.validarClave("claveSegura2026"));

        assertFalse(Validador.validarClave(null));
        assertFalse(Validador.validarClave("1234567"));
        assertFalse(Validador.validarClave(""));
    }

    @Test
    void validarMonto_DebeAceptarSoloFinitosPositivos() {
        assertTrue(Validador.validarMonto(0.01));
        assertTrue(Validador.validarMonto(100.0));

        assertFalse(Validador.validarMonto(0.0));
        assertFalse(Validador.validarMonto(-10.0));
        assertFalse(Validador.validarMonto(Double.NaN));
        assertFalse(Validador.validarMonto(Double.POSITIVE_INFINITY));
        assertFalse(Validador.validarMonto(Double.NEGATIVE_INFINITY));
    }

    @Test
    void validarNombreUsuario_DebeValidarLongitudEntre3Y30SinEspaciosExtremos() {
        assertTrue(Validador.validarNombreUsuario("Ana"));
        assertTrue(Validador.validarNombreUsuario("  Jugador123  "));
        assertTrue(Validador.validarNombreUsuario("a".repeat(30)));

        assertFalse(Validador.validarNombreUsuario(null));
        assertFalse(Validador.validarNombreUsuario("  "));
        assertFalse(Validador.validarNombreUsuario("ab"));
        assertFalse(Validador.validarNombreUsuario("a".repeat(31)));
    }
}
