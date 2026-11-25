package model;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mano;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {

    private Mazo crearMazoConCartas(Carta... cartas) {
        Mazo mazo = new Mazo();
        for (Carta carta : cartas) {
            mazo.agregarCarta(carta);
        }
        return mazo;
    }

    private Partida crearPartidaConCartas(Carta... cartas) {
        Mazo mazo = crearMazoConCartas(cartas);
        Partida partida = new Partida(1, mazo, new EstadoInicial());
        partida.agregarManoJugador(new ManoJugador(new Apuesta(100.0)));
        return partida;
    }

    // =========================================
    // CONSTRUCTOR
    // =========================================

    @Test
    void constructor_DebeValidarParametrosNulos() {
        assertThrows(IllegalArgumentException.class, () -> new Partida(1, null, new EstadoInicial()));
        assertThrows(IllegalArgumentException.class, () -> new Partida(1, new Mazo(), null));
    }

    // =========================================
    // INICIAR Y REPARTIR
    // =========================================

    @Test
    void iniciar_DebeRepartirCuatroCartasYCambiarATurnoJugador() {
        Carta j1 = new Carta(Palo.PICAS, ValorCarta.SIETE);
        Carta c1 = new Carta(Palo.CORAZONES, ValorCarta.DIEZ);
        Carta j2 = new Carta(Palo.DIAMANTES, ValorCarta.OCHO);
        Carta c2 = new Carta(Palo.TREBOLES, ValorCarta.SEIS);

        Partida partida = crearPartidaConCartas(j1, c1, j2, c2);
        partida.iniciar();

        assertNotNull(partida.obtenerFechaInicio());
        assertInstanceOf(EstadoTurnoJugador.class, partida.obtenerEstadoActual());
        assertEquals(List.of(j1, j2), partida.obtenerManosJugador().get(0).obtenerCartas());
        assertEquals(List.of(c1, c2), partida.obtenerManoCrupier().obtenerCartas());
    }

    @Test
    void iniciar_NoDebePermitirIniciarDosVeces() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.SIETE),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO)
        );
        partida.iniciar();

        assertThrows(IllegalStateException.class, partida::iniciar);
    }

    @Test
    void repartirCartasIniciales_DebeFallarSiFaltanCartasOSinManoODobleReparto() {
        // Sin mano de jugador
        Partida sinMano = new Partida(1, crearMazoConCartas(
                new Carta(Palo.PICAS, ValorCarta.DOS),
                new Carta(Palo.PICAS, ValorCarta.TRES),
                new Carta(Palo.PICAS, ValorCarta.CUATRO),
                new Carta(Palo.PICAS, ValorCarta.CINCO)
        ), new EstadoInicial());
        assertThrows(IllegalStateException.class, sinMano::repartirCartasIniciales);

        // Menos de 4 cartas en el mazo
        Partida pocasCartas = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.DOS),
                new Carta(Palo.PICAS, ValorCarta.TRES)
        );
        assertThrows(IllegalStateException.class, pocasCartas::repartirCartasIniciales);

        // Ya fueron repartidas
        Partida yaRepartida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.DOS),
                new Carta(Palo.PICAS, ValorCarta.TRES),
                new Carta(Palo.PICAS, ValorCarta.CUATRO),
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.PICAS, ValorCarta.SEIS)
        );
        yaRepartida.repartirCartasIniciales();
        assertThrows(IllegalStateException.class, yaRepartida::repartirCartasIniciales);
    }

    @Test
    void agregarManoJugador_DebeRechazarNulaODuplicada() {
        Partida partida = new Partida(1, new Mazo(), new EstadoInicial());
        assertThrows(IllegalArgumentException.class, () -> partida.agregarManoJugador(null));

        partida.agregarManoJugador(new ManoJugador(new Apuesta(100.0)));
        assertThrows(IllegalStateException.class, () -> partida.agregarManoJugador(new ManoJugador(new Apuesta(100.0))));
    }

    // =========================================
    // PEDIR CARTA Y PLANTARSE
    // =========================================

    @Test
    void pedirCarta_DebeAgregarCartaYFinalizarSiAlcanza21OSupera21() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.PICAS, ValorCarta.CINCO) // suma 21
        );
        partida.iniciar();

        partida.pedirCarta(0);

        ManoJugador mano = partida.obtenerManosJugador().get(0);
        assertEquals(21, mano.calcularPuntuacion());
        assertTrue(mano.estaFinalizada());

        // No permite pedir más cartas tras finalizar
        assertThrows(IllegalStateException.class, () -> partida.pedirCarta(0));
    }

    @Test
    void pedirCarta_DebeFallarConIndiceInvalidoOMazoVacio() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.CUATRO),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.CINCO),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO)
        );
        partida.iniciar();

        assertThrows(IndexOutOfBoundsException.class, () -> partida.pedirCarta(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> partida.pedirCarta(1));
        // Mazo ya quedó vacío tras repartir 4 cartas
        assertThrows(IllegalStateException.class, () -> partida.pedirCarta(0));
    }

    @Test
    void plantarse_DebeMarcarManoPlantadaYRechazarSegundaVez() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.SIETE),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO)
        );
        partida.iniciar();

        partida.plantarse(0);

        assertTrue(partida.obtenerManosJugador().get(0).obtenerPlantada());
        assertThrows(IllegalStateException.class, () -> partida.plantarse(0));
    }

    // =========================================
    // DOBLAR APUESTA
    // =========================================

    @Test
    void doblarApuesta_DebeDuplicarApuestaRepartirUnaCartaYFinalizarMano() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.PICAS, ValorCarta.DIEZ)
        );
        partida.iniciar();

        boolean resultado = partida.doblarApuesta(0);

        assertTrue(resultado);
        ManoJugador mano = partida.obtenerManosJugador().get(0);
        assertEquals(200.0, mano.obtenerApuesta().obtenerMontoTotal(), 0.001);
        assertEquals(3, mano.obtenerCantidadCartas());
        assertEquals(21, mano.calcularPuntuacion());
        assertTrue(mano.obtenerDoblada());
        assertTrue(mano.estaFinalizada());
    }

    @Test
    void doblarApuesta_DebeRetornarFalseSiMazoVacioONoPuedeDoblar() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO)
        );
        partida.iniciar();

        // Mazo vacío
        assertFalse(partida.doblarApuesta(0));
    }

    // =========================================
    // DIVIDIR MANO
    // =========================================

    @Test
    void dividirMano_DebeCrearDosManosConCartasAdicionales() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.OCHO),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.OCHO),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.PICAS, ValorCarta.TRES),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ)
        );
        partida.iniciar();

        assertTrue(partida.dividirMano());
        assertEquals(2, partida.obtenerManosJugador().size());

        ManoJugador primera = partida.obtenerManosJugador().get(0);
        ManoJugador segunda = partida.obtenerManosJugador().get(1);

        assertTrue(primera.obtenerDividida());
        assertTrue(segunda.obtenerDividida());
        assertEquals(11, primera.calcularPuntuacion());
        assertEquals(18, segunda.calcularPuntuacion());
        assertEquals(100.0, segunda.obtenerApuesta().obtenerMontoInicial(), 0.001);
        assertFalse(primera.estaFinalizada());
        assertFalse(segunda.estaFinalizada());
    }

    @Test
    void dividirMano_DivisionDeAsesDebeFinalizarAmbasManosAutomaticamente() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.AS),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.AS),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.NUEVE)
        );
        partida.iniciar();

        assertTrue(partida.dividirMano());
        assertEquals(2, partida.obtenerManosJugador().size());

        for (ManoJugador mano : partida.obtenerManosJugador()) {
            assertTrue(mano.obtenerAsDividido());
            assertTrue(mano.estaFinalizada());
            assertEquals(2, mano.obtenerCantidadCartas());
        }
    }

    @Test
    void dividirMano_DebeRetornarFalseSiNoCumpleCondiciones() {
        // Sin manos
        Partida sinManos = new Partida(1, new Mazo(), new EstadoInicial());
        assertFalse(sinManos.dividirMano());

        // Cartas distintas
        Partida distintas = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.SIETE),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.OCHO),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.PICAS, ValorCarta.DOS),
                new Carta(Palo.CORAZONES, ValorCarta.TRES)
        );
        distintas.iniciar();
        assertFalse(distintas.dividirMano());

        // Menos de 2 cartas en mazo para repartir tras split
        Partida pocasCartas = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.OCHO),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.OCHO),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.PICAS, ValorCarta.DOS)
        );
        pocasCartas.iniciar();
        assertFalse(pocasCartas.dividirMano());
    }

    // =========================================
    // CRUPIER, FINALIZACION Y ENCAPSULAMIENTO
    // =========================================

    @Test
    void pedirCartaCrupier_DebeAgregarCartaYFallarConMazoVacio() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.SIETE),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO),
                new Carta(Palo.PICAS, ValorCarta.OCHO)
        );
        partida.iniciar();

        partida.pedirCartaCrupier();
        assertEquals(3, partida.obtenerManoCrupier().obtenerCantidadCartas());
        assertEquals(18, partida.obtenerManoCrupier().calcularPuntuacion());

        assertThrows(IllegalStateException.class, partida::pedirCartaCrupier);
    }

    @Test
    void finalizar_DebeMarcarFinalizadaYImpedirAccionesPosteriores() {
        Partida partida = crearPartidaConCartas(
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.CORAZONES, ValorCarta.SEIS),
                new Carta(Palo.DIAMANTES, ValorCarta.SIETE),
                new Carta(Palo.TREBOLES, ValorCarta.CUATRO)
        );
        partida.iniciar();

        partida.finalizar();

        assertTrue(partida.estaFinalizada());
        assertTrue(partida.obtenerFinalizada());
        assertNotNull(partida.obtenerFechaFin());

        assertThrows(IllegalStateException.class, () -> partida.pedirCarta(0));
        assertThrows(IllegalStateException.class, () -> partida.plantarse(0));
        assertThrows(IllegalStateException.class, partida::ejecutarTurnoCrupier);
        assertThrows(IllegalStateException.class, () -> partida.modificarFinalizada(false));
    }

    @Test
    void encapsulamiento_DebeValidarYActualizarPropiedades() {
        Partida partida = new Partida(1, new Mazo(), new EstadoInicial());
        LocalDateTime ahora = LocalDateTime.now();

        partida.modificarId(99);
        assertEquals(99, partida.obtenerId());

        partida.modificarFechaInicio(ahora);
        assertEquals(ahora, partida.obtenerFechaInicio());

        partida.modificarFechaFin(ahora.plusMinutes(5));
        assertEquals(ahora.plusMinutes(5), partida.obtenerFechaFin());

        Mazo nuevoMazo = new Mazo();
        partida.modificarMazo(nuevoMazo);
        assertSame(nuevoMazo, partida.obtenerMazo());
        assertThrows(IllegalArgumentException.class, () -> partida.modificarMazo(null));

        Mano manoCrupier = new Mano();
        partida.modificarManoCrupier(manoCrupier);
        assertSame(manoCrupier, partida.obtenerManoCrupier());
        assertThrows(IllegalArgumentException.class, () -> partida.modificarManoCrupier(null));

        List<ManoJugador> manos = Collections.singletonList(new ManoJugador(new Apuesta(100.0)));
        partida.modificarManosJugador(manos);
        assertEquals(1, partida.obtenerManosJugador().size());
        assertThrows(IllegalArgumentException.class, () -> partida.modificarManosJugador(null));
        assertThrows(IllegalArgumentException.class, () -> partida.modificarManosJugador(Arrays.asList((ManoJugador) null)));

        partida.modificarEstadoActual(new EstadoTurnoCrupier());
        assertInstanceOf(EstadoTurnoCrupier.class, partida.obtenerEstadoActual());
        assertThrows(IllegalArgumentException.class, () -> partida.modificarEstadoActual(null));

        partida.modificarFinalizada(true);
        assertTrue(partida.estaFinalizada());
        assertThrows(IllegalStateException.class, () -> partida.modificarFinalizada(false));

        partida.modificarEstadoActual(new EstadoFinalizado());
        assertInstanceOf(EstadoFinalizado.class, partida.obtenerEstadoActual());
    }
}
