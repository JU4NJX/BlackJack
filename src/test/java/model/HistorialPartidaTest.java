package model;

import co.edu.unilibre.model.HistorialPartida;
import co.edu.unilibre.model.MovimientoPuntos;
import co.edu.unilibre.model.enums.Resultados;
import co.edu.unilibre.model.enums.TipoMovimiento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistorialPartidaTest {

    @Test
    void registrarResultado_DebeCalcularPuntosGanadosPerdidosYBalance() {
        LocalDateTime fecha = LocalDateTime.now();

        HistorialPartida ganada = new HistorialPartida(1, fecha, 100.0, 2100.0);
        ganada.registrarResultado(Resultados.GANADA);
        assertEquals(Resultados.GANADA, ganada.obtenerResultado());
        assertEquals(200.0, ganada.obtenerPuntosGanados(), 0.001);
        assertEquals(0.0, ganada.obtenerPuntosPerdidos(), 0.001);
        assertEquals(200.0, ganada.calcularBalance(), 0.001);

        HistorialPartida blackjack = new HistorialPartida(2, fecha, 100.0, 2150.0);
        blackjack.registrarResultado(Resultados.BLACKJACK);
        assertEquals(250.0, blackjack.obtenerPuntosGanados(), 0.001);
        assertEquals(0.0, blackjack.obtenerPuntosPerdidos(), 0.001);
        assertEquals(250.0, blackjack.calcularBalance(), 0.001);

        HistorialPartida perdida = new HistorialPartida(3, fecha, 100.0, 1900.0);
        perdida.registrarResultado(Resultados.PERDIDA);
        assertEquals(0.0, perdida.obtenerPuntosGanados(), 0.001);
        assertEquals(100.0, perdida.obtenerPuntosPerdidos(), 0.001);
        assertEquals(-100.0, perdida.calcularBalance(), 0.001);

        HistorialPartida empate = new HistorialPartida(4, fecha, 100.0, 2000.0);
        empate.registrarResultado(Resultados.EMPATE);
        assertEquals(0.0, empate.obtenerPuntosGanados(), 0.001);
        assertEquals(0.0, empate.obtenerPuntosPerdidos(), 0.001);
        assertEquals(0.0, empate.calcularBalance(), 0.001);

        assertThrows(IllegalArgumentException.class, () -> empate.registrarResultado(null));
    }

    @Test
    void historialPartida_EncapsulamientoDebeValidarNegativos() {
        LocalDateTime fecha = LocalDateTime.now();
        HistorialPartida historial = new HistorialPartida(1, fecha, 100.0, 1000.0);

        historial.modificarId(20);
        assertEquals(20, historial.obtenerId());

        LocalDateTime nuevaFecha = fecha.plusHours(2);
        historial.modificarFecha(nuevaFecha);
        assertEquals(nuevaFecha, historial.obtenerFecha());

        historial.modificarApuestaTotal(250.0);
        assertEquals(250.0, historial.obtenerApuestaTotal(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> historial.modificarApuestaTotal(-1.0));

        historial.modificarPuntosGanados(500.0);
        assertEquals(500.0, historial.obtenerPuntosGanados(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> historial.modificarPuntosGanados(-1.0));

        historial.modificarPuntosPerdidos(50.0);
        assertEquals(50.0, historial.obtenerPuntosPerdidos(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> historial.modificarPuntosPerdidos(-1.0));

        historial.modificarSaldoFinal(1450.0);
        assertEquals(1450.0, historial.obtenerSaldoFinal(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> historial.modificarSaldoFinal(-1.0));
    }

    @Test
    void movimientoPuntos_DebeEncapsularYValidarCantidad() {
        LocalDateTime fecha = LocalDateTime.now();
        MovimientoPuntos movimiento = new MovimientoPuntos(1, 2, TipoMovimiento.APUESTA, 100.0, fecha);

        assertEquals(1, movimiento.obtenerId());
        assertEquals(2, movimiento.obtenerIdUsuario());
        assertEquals(TipoMovimiento.APUESTA, movimiento.obtenerTipo());
        assertEquals(100.0, movimiento.obtenerCantidad(), 0.001);
        assertEquals(fecha, movimiento.obtenerFecha());

        movimiento.modificarId(10);
        movimiento.modificarIdUsuario(20);
        movimiento.modificarTipo(TipoMovimiento.GANANCIA);
        movimiento.modificarCantidad(200.0);
        LocalDateTime otraFecha = fecha.plusMinutes(5);
        movimiento.modificarFecha(otraFecha);

        assertEquals(10, movimiento.obtenerId());
        assertEquals(20, movimiento.obtenerIdUsuario());
        assertEquals(TipoMovimiento.GANANCIA, movimiento.obtenerTipo());
        assertEquals(200.0, movimiento.obtenerCantidad(), 0.001);
        assertEquals(otraFecha, movimiento.obtenerFecha());

        assertThrows(IllegalArgumentException.class, () -> movimiento.modificarCantidad(-5.0));
    }
}
