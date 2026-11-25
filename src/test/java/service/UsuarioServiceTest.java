package service;

import co.edu.unilibre.dao.UsuarioDAO;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.service.UsuarioService;
import co.edu.unilibre.dto.UsuarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {

    private UsuarioService usuarioService;
    private UsuarioDAOFalso usuarioDAO;

    @BeforeEach
    void configurar() {
        usuarioDAO = new UsuarioDAOFalso();
        usuarioService = new UsuarioService(usuarioDAO);
    }

    @Test
    void obtenerUsuarioExistente() {

        Usuario usuario = crearUsuario();
        usuarioDAO.usuarioGuardado = usuario;

        UsuarioDTO resultado = usuarioService.obtenerUsuario();

        assertNotNull(resultado);
        assertEquals(1, resultado.obtenerId());
        assertEquals("Jugador", resultado.obtenerNombreUsuario());
        assertEquals("jugador@blackjack.com", resultado.obtenerCorreo());
        assertEquals(1000.0, resultado.obtenerSaldo());
    }

    @Test
    void crearUsuarioPredeterminadoCuandoNoExiste() {

        UsuarioDTO resultado = usuarioService.obtenerUsuario();

        assertNotNull(resultado);
        assertNotNull(usuarioDAO.usuarioGuardado);
        assertEquals("Jugador",
                usuarioDAO.usuarioGuardado.obtenerNombreUsuario());
        assertTrue(usuarioDAO.usuarioGuardado.obtenerId() > 0);
    }

    @Test
    void crearUsuarioPredeterminado_DebeFallarSiNoAsignaId() {
        usuarioDAO.asignarIdAlGuardar = false;

        assertThrows(
                IllegalStateException.class,
                () -> usuarioService.obtenerUsuarioModelo()
        );
    }

    @Test
    void obtenerUsuarioModelo() {

        Usuario usuario = crearUsuario();
        usuarioDAO.usuarioGuardado = usuario;

        Usuario resultado = usuarioService.obtenerUsuarioModelo();

        assertNotNull(resultado);
        assertEquals(1, resultado.obtenerId());
        assertEquals(1000.0, resultado.obtenerSaldo());
    }

    @Test
    void consultarSaldoCorrectamente() {

        usuarioDAO.usuarioGuardado = crearUsuario();

        double saldo = usuarioService.consultarSaldo(1);

        assertEquals(1000.0, saldo);
    }

    @Test
    void consultarSaldoConIdInvalido() {

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.consultarSaldo(0)
        );
    }

    @Test
    void consultarSaldoDeUsuarioInexistente() {

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.consultarSaldo(2)
        );
    }

    @Test
    void actualizarSaldoCorrectamente() {

        Usuario usuario = crearUsuario();
        usuario.modificarSaldo(1500.0);

        usuarioDAO.usuarioGuardado = usuario;

        usuarioService.actualizarSaldo(usuario);

        assertEquals(1500.0,
                usuarioDAO.usuarioGuardado.obtenerSaldo());
        assertTrue(usuarioDAO.saldoActualizado);
    }

    @Test
    void actualizarSaldoConUsuarioNulo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.actualizarSaldo(null)
        );
    }

    @Test
    void actualizarSaldoConIdInvalidoOSaldoNoFinito() {
        Usuario sinId = new Usuario(0, "Jugador", "jugador@blackjack.com", 1000.0, LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> usuarioService.actualizarSaldo(sinId));

        Usuario saldoNaN = new Usuario(1, "Jugador", "jugador@blackjack.com", Double.NaN, LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> usuarioService.actualizarSaldo(saldoNaN));
    }

    @Test
    void actualizarSaldoNegativo() {

        Usuario usuario = crearUsuario();

        assertThrows(
                IllegalArgumentException.class,
                () -> usuario.modificarSaldo(-100.0)
        );
    }

    @Test
    void constructorConDaoNulo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new UsuarioService(null)
        );
    }

    private Usuario crearUsuario() {

        return new Usuario(
                1,
                "Jugador",
                "jugador@blackjack.com",
                1000.0,
                LocalDateTime.now()
        );
    }

    private static class UsuarioDAOFalso extends UsuarioDAO {

        private Usuario usuarioGuardado;
        private boolean saldoActualizado;
        private boolean asignarIdAlGuardar = true;

        private UsuarioDAOFalso() {
            super(true);
        }

        @Override
        public Usuario buscarUsuarioUnico() {
            return usuarioGuardado;
        }

        @Override
        public Usuario buscarPorId(int id) {

            if (usuarioGuardado != null
                    && usuarioGuardado.obtenerId() == id) {
                return usuarioGuardado;
            }

            return null;
        }

        @Override
        public void guardar(Usuario usuario,
                            java.sql.Connection conexion) {

            if (asignarIdAlGuardar) {
                usuario.modificarId(1);
            }
            usuarioGuardado = usuario;
        }

        @Override
        public void actualizarSaldo(Usuario usuario,
                                    java.sql.Connection conexion) {

            usuarioGuardado = usuario;
            saldoActualizado = true;
        }
    }
}
