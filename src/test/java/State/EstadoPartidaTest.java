package State;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.ManoJugador;
import co.edu.unilibre.model.Mazo;
import co.edu.unilibre.model.Partida;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;
import co.edu.unilibre.state.EstadoFinalizado;
import co.edu.unilibre.state.EstadoInicial;
import co.edu.unilibre.state.EstadoTurnoCrupier;
import co.edu.unilibre.state.EstadoTurnoJugador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoPartidaTest {

    private Partida crearPartidaPreparada() {
        Mazo mazo = new Mazo();
        mazo.agregarCarta(new Carta(Palo.PICAS, ValorCarta.CINCO));
        mazo.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.DIEZ));
        mazo.agregarCarta(new Carta(Palo.DIAMANTES, ValorCarta.SEIS));
        mazo.agregarCarta(new Carta(Palo.TREBOLES, ValorCarta.SIETE));
        mazo.agregarCarta(new Carta(Palo.PICAS, ValorCarta.CUATRO));

        Partida partida = new Partida(1, mazo, new EstadoInicial());
        partida.agregarManoJugador(new ManoJugador(new Apuesta(100.0)));
        return partida;
    }

    // =========================================
    // ESTADO INICIAL
    // =========================================

    @Test
    void estadoInicial_IniciarConPartidaNulaDebeLanzarExcepcion() {
        EstadoInicial estado = new EstadoInicial();

        assertThrows(IllegalArgumentException.class, () -> estado.iniciar(null));
    }

    @Test
    void estadoInicial_IniciarDebeRepartirYCambiarATurnoJugador() {
        Partida partida = crearPartidaPreparada();

        partida.iniciar();

        assertInstanceOf(EstadoTurnoJugador.class, partida.obtenerEstadoActual());
        assertEquals(2, partida.obtenerManosJugador().get(0).obtenerCantidadCartas());
        assertEquals(2, partida.obtenerManoCrupier().obtenerCantidadCartas());
    }

    @Test
    void estadoInicial_AccionesNoPermitidasDebenLanzarExcepcion() {
        EstadoInicial estado = new EstadoInicial();
        Partida partida = crearPartidaPreparada();

        assertThrows(IllegalStateException.class, () -> estado.pedirCarta(partida, 0));
        assertThrows(IllegalStateException.class, () -> estado.plantarse(partida, 0));
        assertThrows(IllegalStateException.class, () -> estado.ejecutarTurno(partida));
    }

    // =========================================
    // ESTADO TURNO JUGADOR
    // =========================================

    @Test
    void estadoTurnoJugador_IniciarDebeLanzarExcepcion() {
        EstadoTurnoJugador estado = new EstadoTurnoJugador();
        Partida partida = crearPartidaPreparada();

        assertThrows(IllegalStateException.class, () -> estado.iniciar(partida));
    }

    @Test
    void estadoTurnoJugador_DebePermitirPedirCartaYPlantarse() {
        Partida partida = crearPartidaPreparada();
        partida.iniciar();

        partida.pedirCarta(0);
        assertEquals(3, partida.obtenerManosJugador().get(0).obtenerCantidadCartas());
        assertEquals(15, partida.obtenerManosJugador().get(0).calcularPuntuacion());

        partida.plantarse(0);
        assertTrue(partida.obtenerManosJugador().get(0).obtenerPlantada());
    }

    @Test
    void estadoTurnoJugador_EjecutarTurnoDebeCambiarATurnoCrupier() {
        Partida partida = crearPartidaPreparada();
        partida.iniciar();

        partida.ejecutarTurnoCrupier();

        assertInstanceOf(EstadoTurnoCrupier.class, partida.obtenerEstadoActual());
        assertFalse(partida.estaFinalizada());
    }

    // =========================================
    // ESTADO TURNO CRUPIER
    // =========================================

    @Test
    void estadoTurnoCrupier_AccionesDeJugadorOInicioDebenLanzarExcepcion() {
        EstadoTurnoCrupier estado = new EstadoTurnoCrupier();
        Partida partida = crearPartidaPreparada();

        assertThrows(IllegalStateException.class, () -> estado.iniciar(partida));
        assertThrows(IllegalStateException.class, () -> estado.pedirCarta(partida, 0));
        assertThrows(IllegalStateException.class, () -> estado.plantarse(partida, 0));
    }

    @Test
    void estadoTurnoCrupier_EjecutarTurnoDebeFinalizarPartidaYCambiarAEstadoFinalizado() {
        Partida partida = crearPartidaPreparada();
        partida.iniciar();
        partida.modificarEstadoActual(new EstadoTurnoCrupier());

        partida.ejecutarTurnoCrupier();

        assertTrue(partida.estaFinalizada());
        assertInstanceOf(EstadoFinalizado.class, partida.obtenerEstadoActual());
    }

    // =========================================
    // ESTADO FINALIZADO
    // =========================================

    @Test
    void estadoFinalizado_TodasLasAccionesDebenLanzarExcepcion() {
        EstadoFinalizado estado = new EstadoFinalizado();
        Partida partida = crearPartidaPreparada();

        assertThrows(IllegalStateException.class, () -> estado.iniciar(partida));
        assertThrows(IllegalStateException.class, () -> estado.pedirCarta(partida, 0));
        assertThrows(IllegalStateException.class, () -> estado.plantarse(partida, 0));
        assertThrows(IllegalStateException.class, () -> estado.ejecutarTurno(partida));
    }

    // =========================================
    // CICLO COMPLETO DE ESTADOS
    // =========================================

    @Test
    void cicloCompletoDeEstados_DebeTransicionarCorrectamenteHastaFinalizado() {
        Partida partida = crearPartidaPreparada();
        assertInstanceOf(EstadoInicial.class, partida.obtenerEstadoActual());

        // Inicial -> TurnoJugador
        partida.iniciar();
        assertInstanceOf(EstadoTurnoJugador.class, partida.obtenerEstadoActual());

        // Jugador se planta y cede turno -> TurnoCrupier
        partida.plantarse(0);
        partida.ejecutarTurnoCrupier();
        assertInstanceOf(EstadoTurnoCrupier.class, partida.obtenerEstadoActual());

        // Crupier ejecuta turno -> Finalizado
        partida.ejecutarTurnoCrupier();
        assertInstanceOf(EstadoFinalizado.class, partida.obtenerEstadoActual());
        assertTrue(partida.estaFinalizada());
    }
}
