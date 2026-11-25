package model;

import co.edu.unilibre.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;
    private LocalDateTime fecha;

    @BeforeEach
    void configurar() {
        fecha = LocalDateTime.now();
        usuario = new Usuario(1, "Jugador", "jugador@blackjack.com", 1000.0, fecha);
    }

    @Test
    void depositarPuntos_DebeSumarSaldoYRechazarMontosNoPositivos() {
        usuario.depositarPuntos(500.0);
        assertEquals(1500.0, usuario.obtenerSaldo(), 0.001);

        assertThrows(IllegalArgumentException.class, () -> usuario.depositarPuntos(0.0));
        assertThrows(IllegalArgumentException.class, () -> usuario.depositarPuntos(-100.0));
    }

    @Test
    void descontarPuntos_DebeDescontarSoloCuandoHaySaldoYCantidadValida() {
        assertTrue(usuario.descontarPuntos(400.0));
        assertEquals(600.0, usuario.obtenerSaldo(), 0.001);

        assertFalse(usuario.descontarPuntos(0.0));
        assertFalse(usuario.descontarPuntos(-50.0));
        assertFalse(usuario.descontarPuntos(700.0));
        assertEquals(600.0, usuario.obtenerSaldo(), 0.001);
    }

    @Test
    void tieneSaldoYRecuperarPuntos_DebeComportarseCorrectamente() {
        assertTrue(usuario.tieneSaldo(1000.0));
        assertTrue(usuario.tieneSaldo(0.0));
        assertFalse(usuario.tieneSaldo(-1.0));
        assertFalse(usuario.tieneSaldo(1000.01));

        usuario.recuperarPuntos();
        assertEquals(0.0, usuario.obtenerSaldo(), 0.001);
    }

    @Test
    void encapsulamiento_DebePermitirConsultarYModificarAtributos() {
        usuario.modificarId(5);
        assertEquals(5, usuario.obtenerId());

        usuario.modificarNombreUsuario("NuevoNombre");
        assertEquals("NuevoNombre", usuario.obtenerNombreUsuario());

        usuario.modificarCorreo("nuevo@blackjack.com");
        assertEquals("nuevo@blackjack.com", usuario.obtenerCorreo());

        usuario.modificarSaldo(250.0);
        assertEquals(250.0, usuario.obtenerSaldo(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> usuario.modificarSaldo(-1.0));

        LocalDateTime nuevaFecha = fecha.plusDays(1);
        usuario.modificarFechaRegistro(nuevaFecha);
        assertEquals(nuevaFecha, usuario.obtenerFechaRegistro());
    }
}
