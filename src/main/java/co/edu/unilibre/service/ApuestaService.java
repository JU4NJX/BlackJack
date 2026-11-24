package co.edu.unilibre.service;

import co.edu.unilibre.dao.UsuarioDAO;
import co.edu.unilibre.dao.PartidaDAO;
import co.edu.unilibre.dao.MovimientoPuntosDAO;
import co.edu.unilibre.datebase.ConexionDB;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Partida;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.model.ManoJugador;
import co.edu.unilibre.model.HistorialPartida;

import co.edu.unilibre.model.enums.Resultados;
import co.edu.unilibre.model.enums.TipoMovimiento;

import co.edu.unilibre.util.Constantes;
import co.edu.unilibre.util.Validador;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;

public class ApuestaService {

    private UsuarioDAO usuarioDAO;
    private PartidaDAO partidaDAO;
    private MovimientoPuntosDAO movimientoDAO;
    private ConexionDB conexionBD;

    public ApuestaService (UsuarioDAO usuarioDAO, PartidaDAO partidaDAO, MovimientoPuntosDAO movimientoDAO, ConexionDB conexionBD) {

        if (usuarioDAO == null || partidaDAO == null || movimientoDAO == null || conexionBD == null) {
            throw new IllegalArgumentException("Las dependencias de ApuestaService no pueden ser nulas.");
        }
        this.usuarioDAO = usuarioDAO;
        this.partidaDAO = partidaDAO;
        this.movimientoDAO = movimientoDAO;
        this.conexionBD = conexionBD;
    }

    public ApuestaService() {
        this.usuarioDAO = new UsuarioDAO();
        this.partidaDAO = new PartidaDAO();
        this.movimientoDAO = new MovimientoPuntosDAO();
        this.conexionBD = new ConexionDB();
    }

    // Validar apuesta

    public boolean validarApuesta(double saldo, double monto) {

        if (!Double.isFinite(saldo) || saldo < 0 || !Validador.validarMonto(monto)) {
            return false;
        }

        boolean apuestaPermitida = false;

        for (double apuesta : Constantes.APUESTAS_PERMITIDAS) {

            if (Double.compare(apuesta, monto) == 0) {
                apuestaPermitida = true;
                break;
            }
        }

        return apuestaPermitida && saldo >= monto;
    }

    // Registrar apuesta (firma original del UML)

    public void registrarApuesta(Apuesta apuesta) {

        throw new IllegalStateException("Para registrar una apuesta se necesita identificar " + "al usuario. Utilice registrarApuesta(Apuesta, Usuario).");
    }

    // Registrar apuesta con usuario

    public void registrarApuesta(Apuesta apuesta, Usuario usuario) {

        validarDatosApuesta(apuesta, usuario);

        double monto = apuesta.obtenerMontoInicial();

        if (!validarApuesta(usuario.obtenerSaldo(), monto)) {
            throw new IllegalArgumentException("La apuesta no es válida o el saldo es insuficiente.");
        }

        boolean registrada = descontarYRegistrarMovimiento(usuario, monto, TipoMovimiento.APUESTA);

        if (!registrada) {
            throw new IllegalStateException("No se pudo registrar la apuesta.");
        }
    }

    // Registrar apuesta y partida en una misma transacción

    public void registrarApuesta(Apuesta apuesta, Usuario usuario, Partida partida) {

        validarDatosApuesta(apuesta, usuario);

        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }

        double monto = apuesta.obtenerMontoInicial();

        if (!validarApuesta(usuario.obtenerSaldo(), monto)) {
            throw new IllegalArgumentException("La apuesta no es válida o el saldo es insuficiente.");
        }

        if (partida.obtenerFechaInicio() == null) {
            throw new IllegalStateException("La partida debe iniciarse antes de guardarse.");
        }

        if (partida.obtenerId() != 0) {
            throw new IllegalStateException("La partida ya tiene un identificador.");
        }

        double saldoAnterior = usuario.obtenerSaldo();
        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();

            if (conexion == null) {
                throw new SQLException("No se pudo obtener la conexión.");
            }

            conexion.setAutoCommit(false);

            if (!usuario.descontarPuntos(monto)) {
                throw new IllegalStateException("No se pudo descontar la apuesta.");
            }

            usuarioDAO.actualizarSaldo(usuario, conexion);

            movimientoDAO.registrarMovimiento(usuario.obtenerId(), TipoMovimiento.APUESTA, monto, conexion);

            HistorialPartida historial = new HistorialPartida(0, partida.obtenerFechaInicio(), monto, usuario.obtenerSaldo());

            int idGenerado = partidaDAO.guardar(historial, usuario.obtenerId(), conexion);

            if (idGenerado <= 0) {
                throw new SQLException("No se pudo generar el identificador de la partida.");
            }

            conexion.commit();

            partida.modificarId(idGenerado);

        } catch (SQLException | RuntimeException e) {

            restaurarSaldo(usuario, saldoAnterior);

            revertirTransaccion(conexion);

            throw new IllegalStateException("No se pudo registrar la apuesta y la partida.", e);

        } finally {cerrarConexion(conexion);}
    }

    // Procesar doble apuesta (firma original del UML)

    public boolean procesarDobleApuesta(Apuesta apuesta) {

        throw new IllegalStateException("Para procesar una doble apuesta se necesita identificar " + "al usuario. Utilice procesarDobleApuesta(Apuesta, Usuario).");
    }

    // Procesar doble apuesta con usuario

    public boolean procesarDobleApuesta(Apuesta apuesta, Usuario usuario) {

        if (!validarApuestaPendiente(apuesta, usuario)) {
            return false;
        }

        double montoAdicional = apuesta.obtenerMontoInicial();

        if (!validarApuesta(usuario.obtenerSaldo(), montoAdicional)) {
            return false;
        }

        return descontarYRegistrarMovimiento(usuario, montoAdicional, TipoMovimiento.APUESTA
        );
    }

    // Procesar división (firma original del UML)

    public boolean procesarSplit(Apuesta apuesta) {

        throw new IllegalStateException("Para procesar una división se necesita identificar " + "al usuario. Utilice procesarSplit(Apuesta, Usuario).");
    }

    // Procesar división con usuario

    public boolean procesarSplit(Apuesta apuesta, Usuario usuario) {

        if (!validarApuestaPendiente(apuesta, usuario)) {
            return false;
        }

        double montoAdicional = apuesta.obtenerMontoInicial();

        if (!validarApuesta(usuario.obtenerSaldo(), montoAdicional)) {
            return false;
        }

        return descontarYRegistrarMovimiento(usuario, montoAdicional, TipoMovimiento.APUESTA
        );
    }

    // Liquidar partida

    public void liquidarPartida(Partida partida, Usuario usuario) {

        if (partida == null || usuario == null) {
            throw new IllegalArgumentException("La partida y el usuario no pueden ser nulos.");
        }

        validarUsuarioPersistido(usuario);

        if (!partida.estaFinalizada()) {
            throw new IllegalStateException("No se puede liquidar una partida que no ha finalizado.");
        }

        if (partida.obtenerId() <= 0) {
            throw new IllegalStateException("La partida no tiene un identificador válido.");
        }

        List<ManoJugador> manos = partida.obtenerManosJugador();

        if (manos == null || manos.isEmpty()) {
            throw new IllegalStateException("La partida no contiene manos para liquidar.");
        }

        double apuestaTotal = 0;
        double retornoTotal = 0;
        double puntosPerdidos = 0;

        // Validar y calcular resultados antes de modificar el saldo.

        for (ManoJugador mano : manos) {

            if (mano == null || mano.obtenerApuesta() == null) {
                throw new IllegalStateException("Existe una mano sin apuesta.");
            }

            Apuesta apuesta = mano.obtenerApuesta();
            Resultados resultado = mano.obtenerResultado();

            if (resultado == null) {
                throw new IllegalStateException("Todas las manos deben tener un resultado asignado.");
            }

            if (apuesta.estaLiquidada()) {
                throw new IllegalStateException("Una de las apuestas ya fue liquidada.");
            }

            apuestaTotal += apuesta.obtenerMontoTotal();

            retornoTotal += calcularRetorno(apuesta, resultado);

            if (resultado == Resultados.PERDIDA) {
                puntosPerdidos += apuesta.obtenerMontoTotal();
            }
        }

        if (!Double.isFinite(apuestaTotal) || !Double.isFinite(retornoTotal) || !Double.isFinite(puntosPerdidos)) {
            throw new IllegalStateException("Los valores de liquidación no son válidos.");
        }

        double saldoAnterior = usuario.obtenerSaldo();
        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();

            if (conexion == null) {
                throw new SQLException("No se pudo obtener la conexión.");
            }

            conexion.setAutoCommit(false);

            // Acreditar retornos de las manos.

            for (ManoJugador mano : manos) {

                Apuesta apuesta = mano.obtenerApuesta();
                Resultados resultado = mano.obtenerResultado();

                double retorno = calcularRetorno(apuesta, resultado);

                if (retorno > 0) {usuario.depositarPuntos(retorno);}

                registrarMovimientoLiquidacion(usuario, apuesta, resultado, retorno, conexion);
            }

            // Actualizar saldo del usuario.

            usuarioDAO.actualizarSaldo(usuario, conexion);

            // Crear registro definitivo del historial.

            HistorialPartida historial = new HistorialPartida(partida.obtenerId(), partida.obtenerFechaInicio(), apuestaTotal, usuario.obtenerSaldo());

            Resultados resultadoGlobal = determinarResultadoGlobal(manos, retornoTotal, apuestaTotal);

            historial.modificarResultado(resultadoGlobal);
            historial.modificarPuntosGanados(retornoTotal);
            historial.modificarPuntosPerdidos(puntosPerdidos);
            historial.modificarSaldoFinal(usuario.obtenerSaldo());

            // Actualizar el registro creado al iniciar la partida.

            partidaDAO.actualizar(historial, conexion);

            conexion.commit();

            // Marcar apuestas liquidadas después del commit.

            for (ManoJugador mano : manos) {mano.obtenerApuesta().liquidar();}

        } catch (SQLException | RuntimeException e) {

            restaurarSaldo(usuario, saldoAnterior);
            revertirTransaccion(conexion);

            throw new IllegalStateException("No se pudo liquidar la partida.", e);

        } finally {cerrarConexion(conexion);}
    }

    // Determinar resultado global de la partida

    private Resultados determinarResultadoGlobal(List<ManoJugador> manos, double retornoTotal, double apuestaTotal) {

        if (manos.size() == 1 && manos.get(0).obtenerResultado() == Resultados.BLACKJACK) {
            return Resultados.BLACKJACK;
        }

        int comparacion = Double.compare(retornoTotal, apuestaTotal);

        if (comparacion > 0) {
            return Resultados.GANADA;
        }

        if (comparacion < 0) {
            return Resultados.PERDIDA;
        }

        return Resultados.EMPATE;
    }

    // Registrar movimiento correspondiente a la liquidación

    private void registrarMovimientoLiquidacion(Usuario usuario, Apuesta apuesta, Resultados resultado, double retorno, Connection conexion) {
        switch (resultado) {

            case GANADA:
            case BLACKJACK:
                movimientoDAO.registrarMovimiento(usuario.obtenerId(), TipoMovimiento.GANANCIA, retorno, conexion);
                break;

            case EMPATE:
                movimientoDAO.registrarMovimiento(usuario.obtenerId(), TipoMovimiento.DEVOLUCION, retorno, conexion);
                break;

            case PERDIDA:
                movimientoDAO.registrarMovimiento(usuario.obtenerId(), TipoMovimiento.PERDIDA, apuesta.obtenerMontoTotal(), conexion);
                break;
        }
    }

    // Calcular retorno

    public double calcularRetorno(Apuesta apuesta, Resultados resultado) {
        if (apuesta == null || resultado == null) {
            throw new IllegalArgumentException("La apuesta y el resultado no pueden ser nulos.");
        }
        return apuesta.obtenerRetorno(resultado);
    }

    // Recuperar partidas que quedaron pendientes

    public void aplicarRecuperacionPartidas(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        validarUsuarioPersistido(usuario);

        List<HistorialPartida> pendientes = partidaDAO.consultarPartidasPendientes(usuario.obtenerId());

        if (pendientes == null) {
            return;
        }

        for (HistorialPartida pendiente : pendientes) {
            if (pendiente == null) {
                continue;
            }
            Connection conexion = null;
            try {
                conexion = conexionBD.obtenerConexion();
                if (conexion == null) {
                    throw new SQLException("No se pudo obtener la conexión.");
                }
                conexion.setAutoCommit(false);

                // Una partida pendiente se considera perdida.

                pendiente.registrarResultado(Resultados.PERDIDA);
                pendiente.modificarSaldoFinal(usuario.obtenerSaldo());

                partidaDAO.actualizar(pendiente, conexion);

                if (pendiente.obtenerApuestaTotal() > 0) {
                    movimientoDAO.registrarMovimiento(usuario.obtenerId(), TipoMovimiento.PERDIDA, pendiente.obtenerApuestaTotal(), conexion);
                }

                conexion.commit();

            } catch (SQLException | RuntimeException e) {

                revertirTransaccion(conexion);

                throw new IllegalStateException("No se pudo recuperar una partida pendiente.", e);

            } finally {cerrarConexion(conexion);}
        }
    }

    // Aplicar recuperación de puntos

    public void aplicarRecuperacion(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        validarUsuarioPersistido(usuario);

        if (usuario.obtenerSaldo() > 0) {
            return;
        }

        double saldoAnterior = usuario.obtenerSaldo();
        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();

            if (conexion == null) {
                throw new SQLException("No se pudo obtener la conexión.");
            }

            conexion.setAutoCommit(false);

            usuario.depositarPuntos(Constantes.PUNTOS_RECUPERACION);

            usuarioDAO.actualizarSaldo(usuario, conexion);

            movimientoDAO.registrarMovimiento(usuario.obtenerId(), TipoMovimiento.RECUPERACION, Constantes.PUNTOS_RECUPERACION, conexion);

            conexion.commit();

        } catch (SQLException | RuntimeException e) {

            restaurarSaldo(usuario, saldoAnterior);
            revertirTransaccion(conexion);

            throw new IllegalStateException("No se pudo aplicar la recuperación de puntos.", e);

        } finally {cerrarConexion(conexion);}
    }

    // Descontar saldo y registrar movimiento en una transacción

    private boolean descontarYRegistrarMovimiento(Usuario usuario, double monto, TipoMovimiento tipo) {
        if (usuario == null || tipo == null || !Validador.validarMonto(monto)) {
            return false;
        }
        validarUsuarioPersistido(usuario);
        if (usuario.obtenerSaldo() < monto) {
            return false;
        }
        double saldoAnterior = usuario.obtenerSaldo();
        Connection conexion = null;
        try {
            conexion = conexionBD.obtenerConexion();
            if (conexion == null) {
                throw new SQLException("No se pudo obtener la conexión.");
            }

            conexion.setAutoCommit(false);

            if (!usuario.descontarPuntos(monto)) {
                revertirTransaccion(conexion);
                return false;
            }

            usuarioDAO.actualizarSaldo(usuario, conexion);

            movimientoDAO.registrarMovimiento(usuario.obtenerId(), tipo, monto, conexion);

            conexion.commit();

            return true;

        } catch (SQLException | RuntimeException e) {

            restaurarSaldo(usuario, saldoAnterior);
            revertirTransaccion(conexion);

            throw new IllegalStateException("No se pudo completar la operación de apuesta.", e);

        } finally {cerrarConexion(conexion);}
    }

    // Validar datos de apuesta

    private void validarDatosApuesta(Apuesta apuesta, Usuario usuario) {

        if (apuesta == null || usuario == null) {
            throw new IllegalArgumentException("La apuesta y el usuario no pueden ser nulos.");
        }

        validarUsuarioPersistido(usuario);

        if (apuesta.estaLiquidada()) {
            throw new IllegalStateException("La apuesta ya fue liquidada.");
        }
    }

    // Validar apuesta pendiente

    private boolean validarApuestaPendiente(Apuesta apuesta, Usuario usuario) {

        if (apuesta == null || usuario == null || apuesta.estaLiquidada()) {
            return false;
        }
        validarUsuarioPersistido(usuario);
        return true;
    }

    // Validar usuario persistido

    private void validarUsuarioPersistido(Usuario usuario) {

        if (usuario == null || usuario.obtenerId() <= 0) {
            throw new IllegalArgumentException("El usuario debe estar registrado en la base de datos.");
        }

        if (!Double.isFinite(usuario.obtenerSaldo()) || usuario.obtenerSaldo() < 0) {
            throw new IllegalArgumentException("El saldo del usuario no es válido.");
        }
    }

    // Restaurar saldo en memoria

    private void restaurarSaldo(Usuario usuario, double saldoAnterior) {
        if (usuario != null) {
            usuario.modificarSaldo(saldoAnterior);
        }
    }

    // Revertir transacción

    private void revertirTransaccion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.rollback();
            } catch (SQLException e) {
                // Se conserva la excepción original.
            }
        }
    }

    // Cerrar conexión

    private void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            conexionBD.cerrarConexion(conexion);
        }
    }
}