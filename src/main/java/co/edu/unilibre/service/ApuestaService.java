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

import co.edu.unilibre.model.enums.Resultado;
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

    // Constructor predeterminado

    public ApuestaService() {

    }

    public ApuestaService(
            UsuarioDAO usuarioDAO,
            PartidaDAO partidaDAO,
            MovimientoPuntosDAO movimientoDAO,
            ConexionDB conexionBD) {

        if (usuarioDAO == null || partidaDAO == null
                || movimientoDAO == null || conexionBD == null) {

            throw new IllegalArgumentException(
                    "Las dependencias no pueden ser nulas."
            );
        }

        this.usuarioDAO = usuarioDAO;
        this.partidaDAO = partidaDAO;
        this.movimientoDAO = movimientoDAO;
        this.conexionBD = conexionBD;
    }

    // Validar apuesta

    public boolean validarApuesta(double saldo, double monto) {

        if (!Double.isFinite(saldo) || saldo < 0
                || !Validador.validarMonto(monto)) {
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

        throw new IllegalStateException(
                "Para registrar una apuesta se necesita identificar "
                        + "al usuario. Utilice registrarApuesta(Apuesta, Usuario)."
        );
    }

    // Registrar apuesta con usuario

    public void registrarApuesta(Apuesta apuesta, Usuario usuario) {

        if (apuesta == null || usuario == null) {
            throw new IllegalArgumentException(
                    "La apuesta y el usuario no pueden ser nulos."
            );
        }

        double monto = apuesta.obtenerMontoInicial();

        if (!validarApuesta(usuario.obtenerSaldo(), monto)) {
            throw new IllegalArgumentException(
                    "La apuesta no es válida o el saldo es insuficiente."
            );
        }

        boolean registrada = descontarYRegistrarMovimiento(
                usuario,
                monto,
                TipoMovimiento.APUESTA
        );

        if (!registrada) {
            throw new IllegalStateException(
                    "No se pudo registrar la apuesta."
            );
        }
    }

    // Registrar apuesta y partida en una misma transacción

    public void registrarApuesta(
            Apuesta apuesta,
            Usuario usuario,
            Partida partida) {

        if (apuesta == null || usuario == null || partida == null) {
            throw new IllegalArgumentException(
                    "La apuesta, el usuario y la partida no pueden ser nulos."
            );
        }

        double monto = apuesta.obtenerMontoInicial();

        if (!validarApuesta(usuario.obtenerSaldo(), monto)) {
            throw new IllegalArgumentException(
                    "La apuesta no es válida o el saldo es insuficiente."
            );
        }

        if (partida.obtenerFechaInicio() == null) {
            throw new IllegalStateException(
                    "La partida debe iniciarse antes de guardarse."
            );
        }

        if (partida.obtenerId() != 0) {
            throw new IllegalStateException(
                    "La partida ya tiene un identificador."
            );
        }

        double saldoAnterior = usuario.obtenerSaldo();
        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            if (!usuario.descontarPuntos(monto)) {
                throw new IllegalStateException(
                        "No se pudo descontar la apuesta."
                );
            }

            usuarioDAO.actualizarSaldo(usuario, conexion);

            movimientoDAO.registrarMovimiento(
                    usuario.obtenerId(),
                    TipoMovimiento.APUESTA,
                    monto,
                    conexion
            );

            HistorialPartida historial = new HistorialPartida(
                    0,
                    partida.obtenerFechaInicio(),
                    monto,
                    usuario.obtenerSaldo()
            );

            int idGenerado = partidaDAO.guardar(
                    historial,
                    usuario.obtenerId(),
                    conexion
            );

            conexion.commit();

            partida.modificarId(idGenerado);

        } catch (SQLException | RuntimeException e) {

            restaurarSaldo(usuario, saldoAnterior);

            if (partida.obtenerId() != 0) {
                partida.modificarId(0);
            }

            revertirTransaccion(conexion);

            throw new IllegalStateException(
                    "No se pudo registrar la apuesta y la partida.",
                    e
            );

        } finally {
            cerrarConexion(conexion);
        }
    }

    // Procesar doble apuesta (firma original del UML)

    public boolean procesarDobleApuesta(Apuesta apuesta) {

        throw new IllegalStateException(
                "Para procesar una doble apuesta se necesita identificar "
                        + "al usuario. Utilice procesarDobleApuesta(Apuesta, Usuario)."
        );
    }

    // Procesar doble apuesta con usuario

    public boolean procesarDobleApuesta(
            Apuesta apuesta,
            Usuario usuario) {

        if (apuesta == null || usuario == null
                || apuesta.estaLiquidada()) {
            return false;
        }

        double montoAdicional = apuesta.obtenerMontoInicial();

        if (!validarApuesta(usuario.obtenerSaldo(), montoAdicional)) {
            return false;
        }

        return descontarYRegistrarMovimiento(
                usuario,
                montoAdicional,
                TipoMovimiento.APUESTA
        );
    }

    // Procesar división (firma original del UML)

    public boolean procesarSplit(Apuesta apuesta) {

        throw new IllegalStateException(
                "Para procesar una división se necesita identificar "
                        + "al usuario. Utilice procesarSplit(Apuesta, Usuario)."
        );
    }

    // Procesar división con usuario

    public boolean procesarSplit(Apuesta apuesta, Usuario usuario) {

        if (apuesta == null || usuario == null
                || apuesta.estaLiquidada()) {
            return false;
        }

        double montoAdicional = apuesta.obtenerMontoInicial();

        if (!validarApuesta(usuario.obtenerSaldo(), montoAdicional)) {
            return false;
        }

        return descontarYRegistrarMovimiento(
                usuario,
                montoAdicional,
                TipoMovimiento.APUESTA
        );
    }

    // Liquidar partida

    public void liquidarPartida(Partida partida, Usuario usuario) {

        if (partida == null || usuario == null) {
            throw new IllegalArgumentException(
                    "La partida y el usuario no pueden ser nulos."
            );
        }

        if (!partida.estaFinalizada()) {
            throw new IllegalStateException(
                    "No se puede liquidar una partida que no ha finalizado."
            );
        }

        List<ManoJugador> manos = partida.obtenerManosJugador();

        if (manos.isEmpty()) {
            throw new IllegalStateException(
                    "La partida no contiene manos para liquidar."
            );
        }

        double apuestaTotal = 0;
        double retornoTotal = 0;
        double puntosPerdidos = 0;

        // Validar y calcular todos los resultados antes de modificar el saldo.

        for (ManoJugador mano : manos) {

            if (mano == null || mano.obtenerApuesta() == null) {
                throw new IllegalStateException(
                        "Existe una mano sin apuesta."
                );
            }

            Apuesta apuesta = mano.obtenerApuesta();
            Resultado resultado = mano.obtenerResultado();

            if (resultado == null) {
                throw new IllegalStateException(
                        "Todas las manos deben tener un resultado asignado."
                );
            }

            if (apuesta.estaLiquidada()) {
                throw new IllegalStateException(
                        "Una de las apuestas ya fue liquidada."
                );
            }

            apuestaTotal += apuesta.obtenerMontoTotal();

            retornoTotal += calcularRetorno(
                    apuesta,
                    resultado
            );

            if (resultado == Resultado.PERDIDA) {
                puntosPerdidos += apuesta.obtenerMontoTotal();
            }
        }

        double saldoAnterior = usuario.obtenerSaldo();
        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            // Acreditar los retornos de las manos.

            for (ManoJugador mano : manos) {

                Apuesta apuesta = mano.obtenerApuesta();
                Resultado resultado = mano.obtenerResultado();

                double retorno = calcularRetorno(
                        apuesta,
                        resultado
                );

                if (retorno > 0) {
                    usuario.depositarPuntos(retorno);
                }

                registrarMovimientoLiquidacion(
                        usuario,
                        apuesta,
                        resultado,
                        retorno,
                        conexion
                );
            }

            // Actualizar saldo del usuario.

            usuarioDAO.actualizarSaldo(usuario, conexion);

            // Crear el registro definitivo del historial.

            HistorialPartida historial = new HistorialPartida(
                    partida.obtenerId(),
                    partida.obtenerFechaInicio(),
                    apuestaTotal,
                    usuario.obtenerSaldo()
            );

            Resultado resultadoGlobal = determinarResultadoGlobal(
                    manos,
                    retornoTotal,
                    apuestaTotal
            );

            historial.modificarResultado(resultadoGlobal);
            historial.modificarPuntosGanados(retornoTotal);
            historial.modificarPuntosPerdidos(puntosPerdidos);
            historial.modificarSaldoFinal(usuario.obtenerSaldo());

            // Actualizar el registro que se creó al iniciar.

            partidaDAO.actualizar(historial, conexion);

            conexion.commit();

            // Marcar las apuestas como liquidadas después del commit.

            for (ManoJugador mano : manos) {
                mano.obtenerApuesta().liquidar();
            }

        } catch (SQLException | RuntimeException e) {

            restaurarSaldo(usuario, saldoAnterior);
            revertirTransaccion(conexion);

            throw new IllegalStateException(
                    "No se pudo liquidar la partida.",
                    e
            );

        } finally {
            cerrarConexion(conexion);
        }
    }

    // Determinar resultado global de la partida

    private Resultado determinarResultadoGlobal(
            List<ManoJugador> manos,
            double retornoTotal,
            double apuestaTotal) {

        if (manos.size() == 1
                && manos.get(0).obtenerResultado() == Resultado.BLACKJACK) {

            return Resultado.BLACKJACK;
        }

        int comparacion = Double.compare(
                retornoTotal,
                apuestaTotal
        );

        if (comparacion > 0) {
            return Resultado.GANADA;
        }

        if (comparacion < 0) {
            return Resultado.PERDIDA;
        }

        return Resultado.EMPATE;
    }

    // Registrar movimiento correspondiente a la liquidación

    private void registrarMovimientoLiquidacion(
            Usuario usuario,
            Apuesta apuesta,
            Resultado resultado,
            double retorno,
            Connection conexion) {

        switch (resultado) {

            case GANADA:
            case BLACKJACK:

                movimientoDAO.registrarMovimiento(
                        usuario.obtenerId(),
                        TipoMovimiento.GANANCIA,
                        retorno,
                        conexion
                );
                break;

            case EMPATE:

                movimientoDAO.registrarMovimiento(
                        usuario.obtenerId(),
                        TipoMovimiento.DEVOLUCION,
                        retorno,
                        conexion
                );
                break;

            case PERDIDA:

                movimientoDAO.registrarMovimiento(
                        usuario.obtenerId(),
                        TipoMovimiento.PERDIDA,
                        apuesta.obtenerMontoTotal(),
                        conexion
                );
                break;
        }
    }

    // Calcular retorno

    public double calcularRetorno(
            Apuesta apuesta,
            Resultado resultado) {

        if (apuesta == null || resultado == null) {
            throw new IllegalArgumentException(
                    "La apuesta y el resultado no pueden ser nulos."
            );
        }

        return apuesta.obtenerRetorno(resultado);
    }

    // Recuperar partidas que quedaron pendientes

    public void aplicarRecuperacionPartidas(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo."
            );
        }

        List<HistorialPartida> pendientes =
                partidaDAO.consultarPartidasPendientes(
                        usuario.obtenerId()
                );

        for (HistorialPartida pendiente : pendientes) {

            Connection conexion = null;

            try {
                conexion = conexionBD.obtenerConexion();
                conexion.setAutoCommit(false);

                // Una partida pendiente se considera perdida.

                pendiente.registrarResultado(Resultado.PERDIDA);
                pendiente.modificarSaldoFinal(usuario.obtenerSaldo());

                partidaDAO.actualizar(pendiente, conexion);

                if (pendiente.obtenerApuestaTotal() > 0) {

                    movimientoDAO.registrarMovimiento(
                            usuario.obtenerId(),
                            TipoMovimiento.PERDIDA,
                            pendiente.obtenerApuestaTotal(),
                            conexion
                    );
                }

                conexion.commit();

            } catch (SQLException | RuntimeException e) {

                revertirTransaccion(conexion);

                throw new IllegalStateException(
                        "No se pudo recuperar una partida pendiente.",
                        e
                );

            } finally {
                cerrarConexion(conexion);
            }
        }
    }

    // Aplicar recuperación de puntos

    public void aplicarRecuperacion(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo."
            );
        }

        if (usuario.obtenerSaldo() > 0) {
            return;
        }

        double saldoAnterior = usuario.obtenerSaldo();
        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            usuario.depositarPuntos(
                    Constantes.PUNTOS_RECUPERACION
            );

            usuarioDAO.actualizarSaldo(usuario, conexion);

            movimientoDAO.registrarMovimiento(
                    usuario.obtenerId(),
                    TipoMovimiento.RECUPERACION,
                    Constantes.PUNTOS_RECUPERACION,
                    conexion
            );

            conexion.commit();

        } catch (SQLException | RuntimeException e) {

            restaurarSaldo(usuario, saldoAnterior);
            revertirTransaccion(conexion);

            throw new IllegalStateException(
                    "No se pudo aplicar la recuperación de puntos.",
                    e
            );

        } finally {
            cerrarConexion(conexion);
        }
    }

    // Descontar saldo y registrar movimiento en una transacción

    private boolean descontarYRegistrarMovimiento(
            Usuario usuario,
            double monto,
            TipoMovimiento tipo) {

        if (usuario == null || !Validador.validarMonto(monto)) {
            return false;
        }

        double saldoAnterior = usuario.obtenerSaldo();
        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            if (!usuario.descontarPuntos(monto)) {
                conexion.rollback();
                return false;
            }

            usuarioDAO.actualizarSaldo(usuario, conexion);

            movimientoDAO.registrarMovimiento(
                    usuario.obtenerId(),
                    tipo,
                    monto,
                    conexion
            );

            conexion.commit();

            return true;

        } catch (SQLException | RuntimeException e) {

            restaurarSaldo(usuario, saldoAnterior);
            revertirTransaccion(conexion);

            throw new IllegalStateException(
                    "No se pudo completar la operación de apuesta.",
                    e
            );

        } finally {
            cerrarConexion(conexion);
        }
    }

    // Restaurar saldo en memoria

    private void restaurarSaldo(
            Usuario usuario,
            double saldoAnterior) {

        usuario.modificarSaldo(saldoAnterior);
    }

    // Revertir transacción

    private void revertirTransaccion(Connection conexion) {

        if (conexion != null) {
            try {
                conexion.rollback();
            } catch (SQLException e) {
                // La excepción original se conserva.
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
