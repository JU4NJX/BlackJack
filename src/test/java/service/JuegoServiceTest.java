package service;

import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.factory.CartaFactory;
import co.edu.unilibre.factory.CartaFactoryEstandar;
import co.edu.unilibre.factory.MazoFactory;
import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mazo;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.model.enums.EstadoPartida;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.Resultados;
import co.edu.unilibre.model.enums.ValorCarta;
import co.edu.unilibre.observer.Observador;
import co.edu.unilibre.service.JuegoService;
import co.edu.unilibre.strategy.EstrategiaClasica;
import co.edu.unilibre.strategy.EstrategiaCrupier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
            public Carta crearCarta(
                    Palo palo,
                    ValorCarta valor) {
                return new Carta(palo, valor);
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

    private JuegoService crearJuegoConCartas(EstrategiaCrupier estrategia, ApuestaServicePrueba apuestaSrv, Carta... cartas) {
        MazoFactory factoryDeterminista = new MazoFactory(new CartaFactoryEstandar()) {
            @Override
            public Mazo crearMazo() {
                Mazo mazo = new Mazo();
                for (Carta carta : cartas) {
                    mazo.agregarCarta(carta);
                }
                return mazo;
            }
        };
        return new JuegoService(estrategia, factoryDeterminista, apuestaSrv);
    }

    private Usuario crearUsuario(double saldo) {
        return new Usuario(1, "Jugador", "jugador@blackjack.com", saldo, LocalDateTime.now());
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
        assertDoesNotThrow(() -> juegoService.agregarObservador(observador));
    }

    @Test
    void agregarObservadorDuplicado() {
        Observador observador = dto -> {};
        juegoService.agregarObservador(observador);
        juegoService.agregarObservador(observador);
        assertDoesNotThrow(() -> juegoService.eliminarObservador(observador));
    }

    @Test
    void eliminarObservadorInexistente() {
        Observador observador = dto -> {};
        assertDoesNotThrow(() -> juegoService.eliminarObservador(observador));
    }

    @Test
    void eliminarObservadorNulo() {
        assertDoesNotThrow(() -> juegoService.eliminarObservador(null));
    }

    @Test
    void notificarSinPartidaActiva() {
        assertDoesNotThrow(() -> juegoService.notificarObservadores());
    }

    // =====================================================
    // PRUEBAS SIN PARTIDA ACTIVA
    // =====================================================

    @Test
    void pedirCartaSinPartidaActiva() {
        assertThrows(IllegalStateException.class, () -> juegoService.pedirCarta(0));
    }

    @Test
    void plantarseSinPartidaActiva() {
        assertThrows(IllegalStateException.class, () -> juegoService.plantarse(0));
    }

    @Test
    void doblarApuestaSinPartidaActiva() {
        assertThrows(IllegalStateException.class, () -> juegoService.doblarApuesta(0));
    }

    @Test
    void dividirManoSinPartidaActiva() {
        assertThrows(IllegalStateException.class, () -> juegoService.dividirMano());
    }

    @Test
    void ejecutarTurnoCrupierSinPartidaActiva() {
        assertThrows(IllegalStateException.class, () -> juegoService.ejecutarTurnoCrupier());
    }

    @Test
    void finalizarPartidaSinPartidaActiva() {
        assertThrows(IllegalStateException.class, () -> juegoService.finalizarPartida());
    }

    // =====================================================
    // PRUEBAS DE FLUJO DE PARTIDA
    // =====================================================

    @Test
    void iniciarPartida_DebeValidarUsuarioApuestaYPartidaEnCurso() {
        Usuario usuario = crearUsuario(1000.0);

        assertThrows(IllegalArgumentException.class, () -> juegoService.iniciarPartida(null, 100.0));
        assertThrows(IllegalArgumentException.class, () -> juegoService.iniciarPartida(usuario, 50.0));
        assertThrows(IllegalArgumentException.class, () -> juegoService.iniciarPartida(usuario, 2000.0));

        PartidaDTO dto = juegoService.iniciarPartida(usuario, 100.0);

        assertNotNull(dto);
        assertEquals(EstadoPartida.TURNO_JUGADOR, dto.obtenerEstado());
        assertEquals(2, dto.obtenerCartasJugador().size());
        assertEquals(2, dto.obtenerCartasCrupier().size());
        assertTrue(dto.obtenerCartasCrupier().get(0).obtenerVisible());
        assertFalse(dto.obtenerCartasCrupier().get(1).obtenerVisible());
        assertEquals(0, dto.obtenerPuntuacionCrupier());

        // No debe permitir iniciar otra partida mientras una está activa
        assertThrows(IllegalStateException.class, () -> juegoService.iniciarPartida(usuario, 100.0));
    }

    @Test
    void pedirCartaYPlantarse_DebeEjecutarTurnoCrupierYDeterminarVictoria() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.DIEZ),     // J1
                new Carta(Palo.CORAZONES, ValorCarta.SEIS), // C1
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS), // J2 (total 16)
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),  // C2 (total 14)
                new Carta(Palo.PICAS, ValorCarta.CUATRO),   // J3 (total 20)
                new Carta(Palo.CORAZONES, ValorCarta.CUATRO)// C3 (total 18)
        );
        Usuario usuario = crearUsuario(1000.0);

        PartidaDTO inicio = servicio.iniciarPartida(usuario, 100.0);
        assertEquals(16, inicio.obtenerPuntuacionJugador());

        PartidaDTO trasPedir = servicio.pedirCarta(0);
        assertEquals(EstadoPartida.TURNO_JUGADOR, trasPedir.obtenerEstado());
        assertEquals(20, trasPedir.obtenerPuntuacionJugador());

        PartidaDTO finalDto = servicio.plantarse(0);
        assertEquals(EstadoPartida.FINALIZADA, finalDto.obtenerEstado());
        assertEquals(20, finalDto.obtenerPuntuacionJugador());
        assertEquals(18, finalDto.obtenerPuntuacionCrupier());
        assertEquals(Resultados.GANADA, finalDto.obtenerResultado());
        assertTrue(finalDto.obtenerCartasCrupier().get(1).obtenerVisible());
    }

    @Test
    void pedirCarta_CuandoSupera21DebeFinalizarYPerder() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.PICAS, ValorCarta.NUEVE) // 25 -> bust
        );

        servicio.iniciarPartida(crearUsuario(1000.0), 100.0);
        PartidaDTO dto = servicio.pedirCarta(0);

        assertEquals(EstadoPartida.FINALIZADA, dto.obtenerEstado());
        assertEquals(25, dto.obtenerPuntuacionJugador());
        assertEquals(Resultados.PERDIDA, dto.obtenerResultado());
    }

    @Test
    void blackjackNatural_DebeRetornarResultadoBlackjack() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.AS),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ),
                new Carta(Palo.DIAMANTES, ValorCarta.K),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO)
        );

        PartidaDTO inicio = servicio.iniciarPartida(crearUsuario(1000.0), 100.0);
        assertTrue(inicio.obtenerManosFinalizadas().get(0));

        PartidaDTO fin = servicio.ejecutarTurnoCrupier();
        assertEquals(EstadoPartida.FINALIZADA, fin.obtenerEstado());
        assertEquals(Resultados.BLACKJACK, fin.obtenerResultado());
    }

    @Test
    void doblarApuesta_DebeDuplicarApuestaRepartirCartaYFinalizar() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.PICAS, ValorCarta.DIEZ) // jugador llega a 21
        );

        servicio.iniciarPartida(crearUsuario(1000.0), 100.0);
        PartidaDTO dto = servicio.doblarApuesta(0);

        assertEquals(EstadoPartida.FINALIZADA, dto.obtenerEstado());
        assertEquals(200.0, dto.obtenerApuestaTotal(), 0.001);
        assertEquals(21, dto.obtenerPuntuacionJugador());
        assertEquals(Resultados.GANADA, dto.obtenerResultado());
    }

    @Test
    void doblarApuesta_DebeFallarConIndiceInvalidoOProcesamientoRechazado() {
        ApuestaServicePrueba apuestaRechazada = new ApuestaServicePrueba() {
            @Override
            public boolean procesarDobleApuesta(Apuesta apuesta, Usuario usuario) {
                return false;
            }
        };

        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                apuestaRechazada,
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.TREBOLES, ValorCarta.SIETE),
                new Carta(Palo.PICAS, ValorCarta.DIEZ)
        );

        servicio.iniciarPartida(crearUsuario(1000.0), 100.0);

        assertThrows(IllegalArgumentException.class, () -> servicio.doblarApuesta(-1));
        assertThrows(IllegalArgumentException.class, () -> servicio.doblarApuesta(2));
        assertThrows(IllegalStateException.class, () -> servicio.doblarApuesta(0));
    }

    @Test
    void dividirMano_DebeCrearDosManosYFinalizarTrasPlantarseEnAmbas() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.OCHO),     // J1
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ), // C1
                new Carta(Palo.DIAMANTES, ValorCarta.OCHO), // J2
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),  // C2 (18)
                new Carta(Palo.PICAS, ValorCarta.DIEZ),     // mano 1 -> 18 (EMPATE)
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ)  // mano 2 -> 18 (EMPATE)
        );

        PartidaDTO inicio = servicio.iniciarPartida(crearUsuario(1000.0), 100.0);
        assertTrue(inicio.obtenerPuedeDividir());

        PartidaDTO trasSplit = servicio.dividirMano();
        assertEquals(EstadoPartida.TURNO_JUGADOR, trasSplit.obtenerEstado());
        assertEquals(2, trasSplit.obtenerManosJugador().size());
        assertEquals(200.0, trasSplit.obtenerApuestaTotal(), 0.001);
        assertFalse(trasSplit.obtenerPuedeDividir());

        PartidaDTO trasMano1 = servicio.plantarse(0);
        assertEquals(EstadoPartida.TURNO_JUGADOR, trasMano1.obtenerEstado());
        assertTrue(trasMano1.obtenerManosFinalizadas().get(0));
        assertFalse(trasMano1.obtenerManosFinalizadas().get(1));

        PartidaDTO fin = servicio.plantarse(1);
        assertEquals(EstadoPartida.FINALIZADA, fin.obtenerEstado());
        assertEquals(Resultados.EMPATE, fin.obtenerResultado());
    }

    @Test
    void dividirManoDeAses_DebeFinalizarAutomaticamenteYNoPagarComoBlackjack() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.AS),       // J1
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ), // C1
                new Carta(Palo.DIAMANTES, ValorCarta.AS),   // J2
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),  // C2 (18)
                new Carta(Palo.PICAS, ValorCarta.K),        // mano 1 -> 21 (GANADA, no BLACKJACK)
                new Carta(Palo.CORAZONES, ValorCarta.Q)     // mano 2 -> 21 (GANADA, no BLACKJACK)
        );

        servicio.iniciarPartida(crearUsuario(1000.0), 100.0);
        PartidaDTO fin = servicio.dividirMano();

        assertEquals(EstadoPartida.FINALIZADA, fin.obtenerEstado());
        assertEquals(Resultados.GANADA, fin.obtenerResultado());
    }

    @Test
    void dividirMano_CuandoCrupierTieneBlackjackNaturalManoDivididaPierde() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.AS),
                new Carta(Palo.CORAZONES, ValorCarta.AS),
                new Carta(Palo.DIAMANTES, ValorCarta.AS),
                new Carta(Palo.TREBOLES, ValorCarta.K), // Crupier Blackjack natural
                new Carta(Palo.PICAS, ValorCarta.DIEZ),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ)
        );

        servicio.iniciarPartida(crearUsuario(1000.0), 100.0);
        PartidaDTO fin = servicio.dividirMano();

        assertEquals(EstadoPartida.FINALIZADA, fin.obtenerEstado());
        assertEquals(Resultados.PERDIDA, fin.obtenerResultado());
    }

    @Test
    void dividirMano_DebeFallarCuandoNoSePuedeDividir() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.SIETE),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ),
                new Carta(Palo.DIAMANTES, ValorCarta.OCHO),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO),
                new Carta(Palo.PICAS, ValorCarta.DOS),
                new Carta(Palo.CORAZONES, ValorCarta.TRES)
        );

        servicio.iniciarPartida(crearUsuario(1000.0), 100.0);
        assertThrows(IllegalStateException.class, servicio::dividirMano);
    }

    @Test
    void ejecutarTurnoCrupier_DebeFallarSiHayManosActivas() {
        JuegoService servicio = crearJuegoConCartas(
                new EstrategiaClasica(),
                new ApuestaServicePrueba(),
                new Carta(Palo.PICAS, ValorCarta.CINCO),
                new Carta(Palo.CORAZONES, ValorCarta.DIEZ),
                new Carta(Palo.DIAMANTES, ValorCarta.SEIS),
                new Carta(Palo.TREBOLES, ValorCarta.OCHO)
        );

        servicio.iniciarPartida(crearUsuario(1000.0), 100.0);
        assertThrows(IllegalStateException.class, servicio::ejecutarTurnoCrupier);
    }
}
