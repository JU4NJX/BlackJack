package observer;

import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.factory.CartaFactoryEstandar;
import co.edu.unilibre.factory.MazoFactory;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mazo;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.model.enums.EstadoPartida;
import co.edu.unilibre.model.enums.Palo;
import co.edu.unilibre.model.enums.ValorCarta;
import co.edu.unilibre.observer.Observador;
import co.edu.unilibre.observer.ObservadorJuego;
import co.edu.unilibre.service.JuegoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ApuestaServicePrueba;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObservadorJugadorTest {

    private JuegoService juegoService;
    private Usuario usuario;

    @BeforeEach
    void configurar() {
        MazoFactory mazoFactoryDeterminista = new MazoFactory(new CartaFactoryEstandar()) {
            @Override
            public Mazo crearMazo() {
                Mazo mazo = new Mazo();
                mazo.agregarCarta(new Carta(Palo.PICAS, ValorCarta.CINCO));
                mazo.agregarCarta(new Carta(Palo.CORAZONES, ValorCarta.DIEZ));
                mazo.agregarCarta(new Carta(Palo.DIAMANTES, ValorCarta.SEIS));
                mazo.agregarCarta(new Carta(Palo.TREBOLES, ValorCarta.SIETE));
                mazo.agregarCarta(new Carta(Palo.PICAS, ValorCarta.CUATRO));
                return mazo;
            }
        };

        juegoService = new JuegoService(
                mano -> mano.calcularPuntuacion() < 17,
                mazoFactoryDeterminista,
                new ApuestaServicePrueba()
        );

        usuario = new Usuario(1, "Jugador", "jugador@blackjack.com", 2000.0, LocalDateTime.now());
    }

    @Test
    void observador_DebeRecibirNotificacionAlIniciarPedirYFinalizarPartida() {
        List<PartidaDTO> notificaciones = new ArrayList<>();
        Observador observador = notificaciones::add;

        juegoService.agregarObservador(observador);

        juegoService.iniciarPartida(usuario, 100.0);
        assertEquals(1, notificaciones.size());
        assertEquals(EstadoPartida.TURNO_JUGADOR, notificaciones.get(0).obtenerEstado());
        assertEquals(11, notificaciones.get(0).obtenerPuntuacionJugador());

        juegoService.pedirCarta(0);
        assertEquals(2, notificaciones.size());
        assertEquals(15, notificaciones.get(1).obtenerPuntuacionJugador());

        juegoService.plantarse(0);
        assertEquals(3, notificaciones.size());
        assertEquals(EstadoPartida.FINALIZADA, notificaciones.get(2).obtenerEstado());
        assertNotNull(notificaciones.get(2).obtenerResultado());
    }

    @Test
    void multiplesObservadores_DebenSerNotificadosTodos() {
        List<PartidaDTO> lista1 = new ArrayList<>();
        List<PartidaDTO> lista2 = new ArrayList<>();

        juegoService.agregarObservador(lista1::add);
        juegoService.agregarObservador(lista2::add);

        juegoService.iniciarPartida(usuario, 100.0);

        assertEquals(1, lista1.size());
        assertEquals(1, lista2.size());
    }

    @Test
    void observadorDuplicado_DebeRecibirUnaSolaNotificacionPorEvento() {
        List<PartidaDTO> notificaciones = new ArrayList<>();
        Observador observador = notificaciones::add;

        juegoService.agregarObservador(observador);
        juegoService.agregarObservador(observador);

        juegoService.iniciarPartida(usuario, 100.0);

        assertEquals(1, notificaciones.size());
    }

    @Test
    void eliminarObservador_DebeDejarDeRecibirNotificaciones() {
        List<PartidaDTO> notificaciones1 = new ArrayList<>();
        List<PartidaDTO> notificaciones2 = new ArrayList<>();
        Observador observador1 = notificaciones1::add;
        Observador observador2 = notificaciones2::add;

        juegoService.agregarObservador(observador1);
        juegoService.agregarObservador(observador2);

        juegoService.iniciarPartida(usuario, 100.0);
        assertEquals(1, notificaciones1.size());
        assertEquals(1, notificaciones2.size());

        juegoService.eliminarObservador(observador1);

        juegoService.pedirCarta(0);
        assertEquals(1, notificaciones1.size());
        assertEquals(2, notificaciones2.size());
    }

    @Test
    void notificarSinPartidaActiva_NoDebeNotificarObservadores() {
        List<PartidaDTO> notificaciones = new ArrayList<>();
        juegoService.agregarObservador(notificaciones::add);

        juegoService.notificarObservadores();

        assertTrue(notificaciones.isEmpty());
    }

    @Test
    void agregarObservadorNulo_DebeLanzarExcepcion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> juegoService.agregarObservador(null)
        );
    }

    @Test
    void observadorJuego_DebePermitirInstanciacionYActualizacionSinErrores() {
        ObservadorJuego observadorJuego = new ObservadorJuego(null);
        PartidaDTO dto = new PartidaDTO();

        assertDoesNotThrow(() -> observadorJuego.actualizar(dto));
        assertDoesNotThrow(() -> observadorJuego.actualizar(null));
    }
}
