package controller;

import co.edu.unilibre.controller.ApuestaController;
import co.edu.unilibre.controller.HistorialController;
import co.edu.unilibre.controller.JuegoController;
import co.edu.unilibre.controller.UsuarioController;
import co.edu.unilibre.dao.UsuarioDAO;
import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.dto.UsuarioDTO;
import co.edu.unilibre.factory.CartaFactoryEstandar;
import co.edu.unilibre.factory.MazoFactory;
import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.service.ApuestaService;
import co.edu.unilibre.service.HistorialService;
import co.edu.unilibre.service.JuegoService;
import co.edu.unilibre.service.UsuarioService;
import co.edu.unilibre.strategy.EstrategiaClasica;
import org.junit.jupiter.api.Test;
import service.ApuestaServicePrueba;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllersTest {

    // =========================================
    // USUARIO CONTROLLER
    // =========================================

    @Test
    void usuarioController_DebeValidarConstructorYDelegarOperaciones() {
        assertThrows(IllegalArgumentException.class, () -> new UsuarioController(null));

        Usuario usuario = new Usuario(1, "Jugador", "jugador@blackjack.com", 1200.0, LocalDateTime.now());
        UsuarioService usuarioService = new UsuarioService(new UsuarioDAO(true) {
            @Override
            public Usuario buscarUsuarioUnico() {
                return usuario;
            }

            @Override
            public Usuario buscarPorId(int id) {
                return id == 1 ? usuario : null;
            }

            @Override
            public void actualizarSaldo(Usuario u, java.sql.Connection c) {
                usuario.modificarSaldo(u.obtenerSaldo());
            }
        });

        UsuarioController controller = new UsuarioController(usuarioService);

        UsuarioDTO dto = controller.obtenerUsuario();
        assertEquals(1, dto.obtenerId());
        assertEquals(1200.0, controller.consultarSaldo(1), 0.001);

        usuario.modificarSaldo(1500.0);
        controller.actualizarSaldo(usuario);
        assertEquals(1500.0, controller.consultarSaldo(1), 0.001);
    }

    // =========================================
    // APUESTA CONTROLLER
    // =========================================

    @Test
    void apuestaController_DebeValidarConstructorSaldoYRegistro() {
        Usuario usuario = new Usuario(1, "Jugador", "jugador@blackjack.com", 500.0, LocalDateTime.now());
        UsuarioService usuarioService = new UsuarioService(new UsuarioDAO(true) {
            @Override
            public Usuario buscarUsuarioUnico() {
                return usuario;
            }
        });

        final double[] montoRegistrado = {0.0};
        ApuestaService apuestaService = new ApuestaServicePrueba() {
            @Override
            public void registrarApuesta(Apuesta apuesta, Usuario u) {
                montoRegistrado[0] = apuesta.obtenerMontoInicial();
            }
        };

        assertThrows(IllegalArgumentException.class, () -> new ApuestaController(null, usuarioService));
        assertThrows(IllegalArgumentException.class, () -> new ApuestaController(apuestaService, null));

        ApuestaController controller = new ApuestaController(apuestaService, usuarioService);

        assertTrue(controller.validarSaldo(200.0));
        assertFalse(controller.validarSaldo(600.0));
        assertFalse(controller.validarSaldo(50.0));

        controller.seleccionarApuesta(200.0);
        assertEquals(200.0, montoRegistrado[0], 0.001);

        assertThrows(IllegalArgumentException.class, () -> controller.seleccionarApuesta(600.0));
    }

    // =========================================
    // HISTORIAL CONTROLLER
    // =========================================

    @Test
    void historialController_DebeValidarConstructorYCargarHistorial() {
        assertThrows(IllegalArgumentException.class, () -> new HistorialController(null));

        HistorialDTO registro = new HistorialDTO();
        registro.modificarId(1);
        registro.modificarResultado("GANADA");

        HistorialService historialService = new HistorialService() {
            @Override
            public List<HistorialDTO> consultarHistorial(int idUsuario) {
                return Collections.singletonList(registro);
            }
        };

        HistorialController controller = new HistorialController(historialService);
        List<HistorialDTO> resultado = controller.cargarHistorial(1);

        assertEquals(1, resultado.size());
        assertEquals("GANADA", resultado.get(0).obtenerResultado());
    }

    // =========================================
    // JUEGO CONTROLLER
    // =========================================

    @Test
    void juegoController_DebeValidarConstructorYDelegarTodasLasAcciones() {
        assertThrows(IllegalArgumentException.class, () -> new JuegoController(null));

        final boolean[] llamadas = new boolean[5];
        JuegoService juegoServiceFalso = new JuegoService(
                new EstrategiaClasica(),
                new MazoFactory(new CartaFactoryEstandar()),
                new ApuestaServicePrueba()
        ) {
            @Override
            public PartidaDTO iniciarPartida(double apuesta) {
                llamadas[0] = true;
                return new PartidaDTO();
            }

            @Override
            public PartidaDTO pedirCarta(int indiceMano) {
                llamadas[1] = true;
                return new PartidaDTO();
            }

            @Override
            public PartidaDTO plantarse(int indiceMano) {
                llamadas[2] = true;
                return new PartidaDTO();
            }

            @Override
            public PartidaDTO doblarApuesta(int indiceMano) {
                llamadas[3] = true;
                return new PartidaDTO();
            }

            @Override
            public PartidaDTO dividirMano() {
                llamadas[4] = true;
                return new PartidaDTO();
            }
        };

        JuegoController controller = new JuegoController(juegoServiceFalso);
        controller.iniciarPartida(100.0);
        controller.pedirCarta(0);
        controller.plantarse(0);
        controller.doblarApuesta(0);
        controller.dividirMano();

        for (boolean llamada : llamadas) {
            assertTrue(llamada);
        }
    }
}
