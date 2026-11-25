package service;

import co.edu.unilibre.dao.MovimientoPuntosDAO;
import co.edu.unilibre.dao.PartidaDAO;
import co.edu.unilibre.dao.UsuarioDAO;
import co.edu.unilibre.datebase.ConexionDB;
import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.HistorialPartida;
import co.edu.unilibre.model.ManoJugador;
import co.edu.unilibre.model.Mazo;
import co.edu.unilibre.model.Partida;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.model.enums.Resultados;
import co.edu.unilibre.model.enums.TipoMovimiento;
import co.edu.unilibre.service.ApuestaService;
import co.edu.unilibre.state.EstadoFinalizado;
import co.edu.unilibre.state.EstadoInicial;
import co.edu.unilibre.util.Constantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApuestaServiceTest {

    private ApuestaService apuestaService;
    private UsuarioDAOFalso usuarioDAO;
    private PartidaDAOFalso partidaDAO;
    private MovimientoDAOFalso movimientoDAO;
    private ConexionDBFalsa conexionDB;

    @BeforeEach
    void configurar() {
        usuarioDAO = new UsuarioDAOFalso();
        partidaDAO = new PartidaDAOFalso();
        movimientoDAO = new MovimientoDAOFalso();
        conexionDB = new ConexionDBFalsa();
        apuestaService = new ApuestaService(usuarioDAO, partidaDAO, movimientoDAO, conexionDB);
    }

    private Usuario crearUsuario(int id, double saldo) {
        return new Usuario(id, "Jugador", "jugador@blackjack.com", saldo, LocalDateTime.now());
    }

    private Partida crearPartidaFinalizadaConManos(int id, ManoJugador... manos) {
        Partida partida = new Partida(id, new Mazo(), new EstadoFinalizado());
        partida.modificarFechaInicio(LocalDateTime.now());
        partida.modificarManosJugador(Arrays.asList(manos));
        partida.finalizar();
        return partida;
    }

    // =========================================
    // CONSTRUCTORES
    // =========================================

    @Test
    void constructor_DebeRechazarDependenciasNulas() {
        assertThrows(IllegalArgumentException.class, () -> new ApuestaService(null, partidaDAO, movimientoDAO, conexionDB));
        assertThrows(IllegalArgumentException.class, () -> new ApuestaService(usuarioDAO, null, movimientoDAO, conexionDB));
        assertThrows(IllegalArgumentException.class, () -> new ApuestaService(usuarioDAO, partidaDAO, null, conexionDB));
        assertThrows(IllegalArgumentException.class, () -> new ApuestaService(usuarioDAO, partidaDAO, movimientoDAO, null));
        assertDoesNotThrow(() -> new ApuestaService());
    }

    // =========================================
    // VALIDAR APUESTA
    // =========================================

    @Test
    void validarApuesta_DebeAceptarApuestaValida() {
        boolean resultado = apuestaService.validarApuesta(2000.0, 100.0);
        assertTrue(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarSaldoInsuficiente() {
        boolean resultado = apuestaService.validarApuesta(50.0, 100.0);
        assertFalse(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarMontoMenorAlMinimo() {
        assertFalse(apuestaService.validarApuesta(2000.0, 99.99));
    }

    @Test
    void validarApuesta_DebeRechazarMontoNegativo() {
        boolean resultado = apuestaService.validarApuesta(2000.0, -100.0);
        assertFalse(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarMontoCero() {
        boolean resultado = apuestaService.validarApuesta(2000.0, 0.0);
        assertFalse(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarSaldoNegativo() {
        boolean resultado = apuestaService.validarApuesta(-100.0, 50.0);
        assertFalse(resultado);
    }

    @Test
    void validarApuesta_DebeRechazarSaldoNoFinito() {
        assertFalse(apuestaService.validarApuesta(Double.NaN, 100.0));
        assertFalse(apuestaService.validarApuesta(Double.POSITIVE_INFINITY, 100.0));
    }

    // =========================================
    // CALCULAR RETORNO
    // =========================================

    @Test
    void calcularRetorno_DebeCalcularVictoria() {
        Apuesta apuesta = new Apuesta(100.0);
        double retorno = apuestaService.calcularRetorno(apuesta, Resultados.GANADA);
        assertEquals(200.0, retorno, 0.001);
    }

    @Test
    void calcularRetorno_DebeCalcularBlackjack() {
        Apuesta apuesta = new Apuesta(100.0);
        double retorno = apuestaService.calcularRetorno(apuesta, Resultados.BLACKJACK);
        assertEquals(250.0, retorno, 0.001);
    }

    @Test
    void calcularRetorno_DebeCalcularEmpate() {
        Apuesta apuesta = new Apuesta(100.0);
        double retorno = apuestaService.calcularRetorno(apuesta, Resultados.EMPATE);
        assertEquals(100.0, retorno, 0.001);
    }

    @Test
    void calcularRetorno_DebeCalcularPerdida() {
        Apuesta apuesta = new Apuesta(100.0);
        double retorno = apuestaService.calcularRetorno(apuesta, Resultados.PERDIDA);
        assertEquals(0.0, retorno, 0.001);
    }

    @Test
    void calcularRetorno_DebeRechazarApuestaNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> apuestaService.calcularRetorno(null, Resultados.GANADA)
        );
    }

    @Test
    void calcularRetorno_DebeRechazarResultadoNulo() {
        Apuesta apuesta = new Apuesta(100.0);
        assertThrows(
                IllegalArgumentException.class,
                () -> apuestaService.calcularRetorno(apuesta, null)
        );
    }

    // =========================================
    // REGISTRAR APUESTA
    // =========================================

    @Test
    void registrarApuestaSoloApuesta_DebeLanzarExcepcion() {
        assertThrows(
                IllegalStateException.class,
                () -> apuestaService.registrarApuesta(new Apuesta(100.0))
        );
    }

    @Test
    void registrarApuestaConUsuario_DebeDescontarSaldoYRegistrarMovimiento() {
        Usuario usuario = crearUsuario(1, 1000.0);
        Apuesta apuesta = new Apuesta(200.0);

        apuestaService.registrarApuesta(apuesta, usuario);

        assertEquals(800.0, usuario.obtenerSaldo(), 0.001);
        assertTrue(usuarioDAO.actualizado);
        assertEquals(1, movimientoDAO.tiposRegistrados.size());
        assertEquals(TipoMovimiento.APUESTA, movimientoDAO.tiposRegistrados.get(0));
        assertEquals(200.0, movimientoDAO.montosRegistrados.get(0), 0.001);
        assertTrue(conexionDB.commitRealizado);
        assertTrue(conexionDB.conexionCerrada);
    }

    @Test
    void registrarApuestaConUsuario_DebeRechazarDatosInvalidos() {
        Usuario usuario = crearUsuario(1, 500.0);
        Apuesta apuesta = new Apuesta(100.0);

        assertThrows(IllegalArgumentException.class, () -> apuestaService.registrarApuesta(null, usuario));
        assertThrows(IllegalArgumentException.class, () -> apuestaService.registrarApuesta(apuesta, null));
        assertThrows(IllegalArgumentException.class, () -> apuestaService.registrarApuesta(apuesta, crearUsuario(0, 500.0)));
        assertThrows(IllegalArgumentException.class, () -> apuestaService.registrarApuesta(new Apuesta(600.0), usuario));

        apuesta.liquidar();
        assertThrows(IllegalStateException.class, () -> apuestaService.registrarApuesta(apuesta, usuario));
    }

    @Test
    void registrarApuestaConPartida_DebeGuardarPartidaYAsignarId() {
        Usuario usuario = crearUsuario(1, 1000.0);
        Apuesta apuesta = new Apuesta(150.0);
        Partida partida = new Partida(0, new Mazo(), new EstadoInicial());
        partida.modificarFechaInicio(LocalDateTime.now());
        partidaDAO.idAGenerar = 42;

        apuestaService.registrarApuesta(apuesta, usuario, partida);

        assertEquals(42, partida.obtenerId());
        assertEquals(850.0, usuario.obtenerSaldo(), 0.001);
        assertTrue(conexionDB.commitRealizado);
        assertTrue(conexionDB.conexionCerrada);
    }

    @Test
    void registrarApuestaConPartida_DebeValidarPartidaYRevertirAnteError() {
        Usuario usuario = crearUsuario(1, 1000.0);
        Apuesta apuesta = new Apuesta(150.0);

        // Partida nula
        assertThrows(IllegalArgumentException.class, () -> apuestaService.registrarApuesta(apuesta, usuario, null));

        // Sin fecha de inicio
        Partida sinFecha = new Partida(0, new Mazo(), new EstadoInicial());
        assertThrows(IllegalStateException.class, () -> apuestaService.registrarApuesta(apuesta, usuario, sinFecha));

        // Ya tiene ID
        Partida conId = new Partida(5, new Mazo(), new EstadoInicial());
        conId.modificarFechaInicio(LocalDateTime.now());
        assertThrows(IllegalStateException.class, () -> apuestaService.registrarApuesta(apuesta, usuario, conId));

        // Falla al generar ID -> debe revertir transacción y restaurar saldo
        Partida valida = new Partida(0, new Mazo(), new EstadoInicial());
        valida.modificarFechaInicio(LocalDateTime.now());
        partidaDAO.idAGenerar = 0;

        assertThrows(IllegalStateException.class, () -> apuestaService.registrarApuesta(apuesta, usuario, valida));
        assertEquals(1000.0, usuario.obtenerSaldo(), 0.001);
        assertTrue(conexionDB.rollbackRealizado);
        assertTrue(conexionDB.conexionCerrada);
    }

    // =========================================
    // DOBLE APUESTA Y SPLIT
    // =========================================

    @Test
    void procesarDobleApuestaYSplitSinUsuario_DebenLanzarExcepcion() {
        Apuesta apuesta = new Apuesta(100.0);
        assertThrows(IllegalStateException.class, () -> apuestaService.procesarDobleApuesta(apuesta));
        assertThrows(IllegalStateException.class, () -> apuestaService.procesarSplit(apuesta));
    }

    @Test
    void procesarDobleApuesta_DebeDescontarSaldoCuandoEsValido() {
        Usuario usuario = crearUsuario(1, 500.0);
        Apuesta apuesta = new Apuesta(100.0);

        assertTrue(apuestaService.procesarDobleApuesta(apuesta, usuario));
        assertEquals(400.0, usuario.obtenerSaldo(), 0.001);
        assertTrue(conexionDB.commitRealizado);
    }

    @Test
    void procesarDobleApuesta_DebeRetornarFalseSiInvalidaOSaldoInsuficiente() {
        Usuario usuario = crearUsuario(1, 50.0);
        Apuesta apuesta = new Apuesta(100.0);

        assertFalse(apuestaService.procesarDobleApuesta(null, usuario));
        assertFalse(apuestaService.procesarDobleApuesta(apuesta, null));
        assertFalse(apuestaService.procesarDobleApuesta(apuesta, usuario));

        apuesta.liquidar();
        assertFalse(apuestaService.procesarDobleApuesta(apuesta, crearUsuario(1, 500.0)));
    }

    @Test
    void procesarSplit_DebeDescontarSaldoYValidarCondiciones() {
        Usuario usuario = crearUsuario(1, 500.0);
        Apuesta apuesta = new Apuesta(150.0);

        assertTrue(apuestaService.procesarSplit(apuesta, usuario));
        assertEquals(350.0, usuario.obtenerSaldo(), 0.001);

        assertFalse(apuestaService.procesarSplit(new Apuesta(400.0), usuario));
        assertFalse(apuestaService.procesarSplit(null, usuario));
    }

    // =========================================
    // LIQUIDAR PARTIDA
    // =========================================

    @Test
    void liquidarPartida_VictoriaDebeAcreditarDobleYMarcarLiquidada() {
        Usuario usuario = crearUsuario(1, 900.0);
        Apuesta apuesta = new Apuesta(100.0);
        ManoJugador mano = new ManoJugador(apuesta);
        mano.modificarResultado(Resultados.GANADA);

        Partida partida = crearPartidaFinalizadaConManos(10, mano);

        apuestaService.liquidarPartida(partida, usuario);

        assertEquals(1100.0, usuario.obtenerSaldo(), 0.001);
        assertTrue(apuesta.estaLiquidada());
        assertEquals(Resultados.GANADA, partidaDAO.ultimoHistorialActualizado.obtenerResultado());
        assertEquals(TipoMovimiento.GANANCIA, movimientoDAO.tiposRegistrados.get(0));
        assertTrue(conexionDB.commitRealizado);
    }

    @Test
    void liquidarPartida_BlackjackDebeAcreditarPago250PorCiento() {
        Usuario usuario = crearUsuario(1, 900.0);
        Apuesta apuesta = new Apuesta(100.0);
        ManoJugador mano = new ManoJugador(apuesta);
        mano.modificarResultado(Resultados.BLACKJACK);

        Partida partida = crearPartidaFinalizadaConManos(11, mano);

        apuestaService.liquidarPartida(partida, usuario);

        assertEquals(1150.0, usuario.obtenerSaldo(), 0.001);
        assertEquals(Resultados.BLACKJACK, partidaDAO.ultimoHistorialActualizado.obtenerResultado());
        assertTrue(apuesta.estaLiquidada());
    }

    @Test
    void liquidarPartida_EmpateYPerdidaYMultiplesManos() {
        // Empate simple
        Usuario usuarioEmpate = crearUsuario(1, 900.0);
        ManoJugador manoEmpate = new ManoJugador(new Apuesta(100.0));
        manoEmpate.modificarResultado(Resultados.EMPATE);
        apuestaService.liquidarPartida(crearPartidaFinalizadaConManos(12, manoEmpate), usuarioEmpate);
        assertEquals(1000.0, usuarioEmpate.obtenerSaldo(), 0.001);
        assertEquals(Resultados.EMPATE, partidaDAO.ultimoHistorialActualizado.obtenerResultado());
        assertEquals(TipoMovimiento.DEVOLUCION, movimientoDAO.tiposRegistrados.get(0));

        // Pérdida simple
        movimientoDAO.tiposRegistrados.clear();
        Usuario usuarioPerdida = crearUsuario(1, 900.0);
        ManoJugador manoPerdida = new ManoJugador(new Apuesta(100.0));
        manoPerdida.modificarResultado(Resultados.PERDIDA);
        apuestaService.liquidarPartida(crearPartidaFinalizadaConManos(13, manoPerdida), usuarioPerdida);
        assertEquals(900.0, usuarioPerdida.obtenerSaldo(), 0.001);
        assertEquals(Resultados.PERDIDA, partidaDAO.ultimoHistorialActualizado.obtenerResultado());
        assertEquals(100.0, partidaDAO.ultimoHistorialActualizado.obtenerPuntosPerdidos(), 0.001);
        assertEquals(TipoMovimiento.PERDIDA, movimientoDAO.tiposRegistrados.get(0));

        // Dos manos (1 ganada + 1 perdida -> retorno 200 == apuesta 200 -> EMPATE global)
        Usuario usuarioSplit = crearUsuario(1, 800.0);
        ManoJugador m1 = new ManoJugador(new Apuesta(100.0));
        m1.modificarResultado(Resultados.GANADA);
        ManoJugador m2 = new ManoJugador(new Apuesta(100.0));
        m2.modificarResultado(Resultados.PERDIDA);
        apuestaService.liquidarPartida(crearPartidaFinalizadaConManos(14, m1, m2), usuarioSplit);
        assertEquals(1000.0, usuarioSplit.obtenerSaldo(), 0.001);
        assertEquals(Resultados.EMPATE, partidaDAO.ultimoHistorialActualizado.obtenerResultado());
    }

    @Test
    void liquidarPartida_DebeValidarPrecondicionesYRevertirSiFallaBD() {
        Usuario usuario = crearUsuario(1, 1000.0);

        assertThrows(IllegalArgumentException.class, () -> apuestaService.liquidarPartida(null, usuario));
        assertThrows(IllegalArgumentException.class, () -> apuestaService.liquidarPartida(crearPartidaFinalizadaConManos(1), null));

        // Partida no finalizada
        Partida activa = new Partida(1, new Mazo(), new EstadoInicial());
        assertThrows(IllegalStateException.class, () -> apuestaService.liquidarPartida(activa, usuario));

        // Partida con ID inválido
        Partida sinId = crearPartidaFinalizadaConManos(0, new ManoJugador(new Apuesta(100.0)));
        assertThrows(IllegalStateException.class, () -> apuestaService.liquidarPartida(sinId, usuario));

        // Sin manos
        Partida sinManos = crearPartidaFinalizadaConManos(1);
        assertThrows(IllegalStateException.class, () -> apuestaService.liquidarPartida(sinManos, usuario));

        // Mano sin resultado
        Partida sinResultado = crearPartidaFinalizadaConManos(1, new ManoJugador(new Apuesta(100.0)));
        assertThrows(IllegalStateException.class, () -> apuestaService.liquidarPartida(sinResultado, usuario));

        // Apuesta ya liquidada
        Apuesta yaLiquidada = new Apuesta(100.0);
        yaLiquidada.liquidar();
        ManoJugador manoLiquidada = new ManoJugador(yaLiquidada);
        manoLiquidada.modificarResultado(Resultados.GANADA);
        assertThrows(IllegalStateException.class, () -> apuestaService.liquidarPartida(crearPartidaFinalizadaConManos(1, manoLiquidada), usuario));

        // Error en BD -> restaura saldo, hace rollback y no liquida apuesta
        Apuesta apuestaFallo = new Apuesta(100.0);
        ManoJugador manoFallo = new ManoJugador(apuestaFallo);
        manoFallo.modificarResultado(Resultados.GANADA);
        partidaDAO.lanzarErrorAlActualizar = true;

        assertThrows(IllegalStateException.class, () -> apuestaService.liquidarPartida(crearPartidaFinalizadaConManos(15, manoFallo), usuario));
        assertEquals(1000.0, usuario.obtenerSaldo(), 0.001);
        assertFalse(apuestaFallo.estaLiquidada());
        assertTrue(conexionDB.rollbackRealizado);
    }

    // =========================================
    // RECUPERACION DE PARTIDAS Y PUNTOS
    // =========================================

    @Test
    void aplicarRecuperacionPartidas_DebeMarcarPendientesComoPerdidas() {
        Usuario usuario = crearUsuario(1, 500.0);
        HistorialPartida pendiente = new HistorialPartida(7, LocalDateTime.now(), 200.0, 500.0);
        partidaDAO.pendientes.add(null);
        partidaDAO.pendientes.add(pendiente);

        apuestaService.aplicarRecuperacionPartidas(usuario);

        assertEquals(Resultados.PERDIDA, pendiente.obtenerResultado());
        assertEquals(500.0, pendiente.obtenerSaldoFinal(), 0.001);
        assertEquals(TipoMovimiento.PERDIDA, movimientoDAO.tiposRegistrados.get(0));
        assertTrue(conexionDB.commitRealizado);

        assertThrows(IllegalArgumentException.class, () -> apuestaService.aplicarRecuperacionPartidas(null));
    }

    @Test
    void aplicarRecuperacion_DebeAcreditarSoloCuandoSaldoEsCero() {
        Usuario usuarioConSaldo = crearUsuario(1, 100.0);
        apuestaService.aplicarRecuperacion(usuarioConSaldo);
        assertEquals(100.0, usuarioConSaldo.obtenerSaldo(), 0.001);
        assertFalse(usuarioDAO.actualizado);

        Usuario usuarioSinSaldo = crearUsuario(1, 0.0);
        apuestaService.aplicarRecuperacion(usuarioSinSaldo);
        assertEquals(Constantes.PUNTOS_RECUPERACION, usuarioSinSaldo.obtenerSaldo(), 0.001);
        assertEquals(TipoMovimiento.RECUPERACION, movimientoDAO.tiposRegistrados.get(0));
        assertTrue(conexionDB.commitRealizado);

        // Si conexión retorna null -> restaura saldo a 0 y lanza excepción
        Usuario usuarioFallo = crearUsuario(1, 0.0);
        conexionDB.retornarNulo = true;
        assertThrows(IllegalStateException.class, () -> apuestaService.aplicarRecuperacion(usuarioFallo));
        assertEquals(0.0, usuarioFallo.obtenerSaldo(), 0.001);

        assertThrows(IllegalArgumentException.class, () -> apuestaService.aplicarRecuperacion(null));
    }

    // =========================================
    // DOBLES DE PRUEBA
    // =========================================

    private static class UsuarioDAOFalso extends UsuarioDAO {
        boolean actualizado;

        UsuarioDAOFalso() {
            super(true);
        }

        @Override
        public void actualizarSaldo(Usuario usuario, Connection conexion) {
            actualizado = true;
        }
    }

    private static class PartidaDAOFalso extends PartidaDAO {
        int idAGenerar = 1;
        HistorialPartida ultimoHistorialActualizado;
        boolean lanzarErrorAlActualizar;
        final List<HistorialPartida> pendientes = new ArrayList<>();

        @Override
        public int guardar(HistorialPartida partida, int idUsuario, Connection conexion) {
            return idAGenerar;
        }

        @Override
        public void actualizar(HistorialPartida partida, Connection conexion) {
            if (lanzarErrorAlActualizar) {
                throw new RuntimeException("Error simulado al actualizar partida");
            }
            ultimoHistorialActualizado = partida;
        }

        @Override
        public List<HistorialPartida> consultarPartidasPendientes(int idUsuario) {
            return pendientes;
        }
    }

    private static class MovimientoDAOFalso extends MovimientoPuntosDAO {
        final List<TipoMovimiento> tiposRegistrados = new ArrayList<>();
        final List<Double> montosRegistrados = new ArrayList<>();

        @Override
        public void registrarMovimiento(int idUsuario, TipoMovimiento tipo, double cantidad, Connection conexion) {
            tiposRegistrados.add(tipo);
            montosRegistrados.add(cantidad);
        }
    }

    private static class ConexionDBFalsa extends ConexionDB {
        boolean commitRealizado;
        boolean rollbackRealizado;
        boolean conexionCerrada;
        boolean retornarNulo;

        @Override
        public Connection obtenerConexion() {
            if (retornarNulo) {
                return null;
            }
            commitRealizado = false;
            rollbackRealizado = false;
            conexionCerrada = false;

            return (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "commit":
                                commitRealizado = true;
                                return null;
                            case "rollback":
                                rollbackRealizado = true;
                                return null;
                            case "close":
                                conexionCerrada = true;
                                return null;
                            case "setAutoCommit":
                                return null;
                            default:
                                return null;
                        }
                    }
            );
        }

        @Override
        public void cerrarConexion(Connection conexion) {
            conexionCerrada = true;
        }
    }
}
