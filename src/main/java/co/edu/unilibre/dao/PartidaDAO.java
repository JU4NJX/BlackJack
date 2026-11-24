package co.edu.unilibre.dao;

import co.edu.unilibre.datebase.ConexionDB;
import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.model.HistorialPartida;
import co.edu.unilibre.model.enums.Resultados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {

    private ConexionDB conexionDB;

    public PartidaDAO() {
        this.conexionDB = new ConexionDB();
    }

    // Guardar partida y obtener el ID generado.

    public int guardar(
            HistorialPartida partida,
            int idUsuario,
            Connection conexion) {

        if (partida == null || conexion == null) {
            throw new IllegalArgumentException(
                    "La partida y la conexión no pueden ser nulas."
            );
        }

        String sql =
                "INSERT INTO partidas " +
                        "(id_usuario, fecha, resultado, apuesta_total, " +
                        "puntos_ganados, puntos_perdidos, saldo_final) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(
                             sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setInt(1, idUsuario);
            sentencia.setTimestamp(
                    2, Timestamp.valueOf(partida.obtenerFecha())
            );

            if (partida.obtenerResultado() == null) {
                sentencia.setNull(3, java.sql.Types.VARCHAR);
            } else {
                sentencia.setString(
                        3, partida.obtenerResultado().name()
                );
            }

            sentencia.setDouble(
                    4, partida.obtenerApuestaTotal()
            );

            sentencia.setDouble(
                    5, partida.obtenerPuntosGanados()
            );

            sentencia.setDouble(
                    6, partida.obtenerPuntosPerdidos()
            );

            sentencia.setDouble(
                    7, partida.obtenerSaldoFinal()
            );

            int filas = sentencia.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                        "No se pudo guardar la partida."
                );
            }

            try (ResultSet claves =
                         sentencia.getGeneratedKeys()) {

                if (claves.next()) {
                    int id = claves.getInt(1);
                    partida.modificarId(id);
                    return id;
                }

                throw new SQLException(
                        "No se pudo obtener el ID de la partida."
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al guardar la partida.", e
            );
        }
    }

    // Actualizar partida existente.

    public void actualizar(
            HistorialPartida partida,
            Connection conexion) {

        if (partida == null || conexion == null) {
            throw new IllegalArgumentException(
                    "La partida y la conexión no pueden ser nulas."
            );
        }

        String sql =
                "UPDATE partidas SET " +
                        "fecha = ?, resultado = ?, apuesta_total = ?, " +
                        "puntos_ganados = ?, puntos_perdidos = ?, saldo_final = ? " +
                        "WHERE id = ?";

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setTimestamp(
                    1, Timestamp.valueOf(partida.obtenerFecha())
            );

            if (partida.obtenerResultado() == null) {
                sentencia.setNull(2, java.sql.Types.VARCHAR);
            } else {
                sentencia.setString(
                        2, partida.obtenerResultado().name()
                );
            }

            sentencia.setDouble(
                    3, partida.obtenerApuestaTotal()
            );

            sentencia.setDouble(
                    4, partida.obtenerPuntosGanados()
            );

            sentencia.setDouble(
                    5, partida.obtenerPuntosPerdidos()
            );

            sentencia.setDouble(
                    6, partida.obtenerSaldoFinal()
            );

            sentencia.setInt(7, partida.obtenerId());

            int filas = sentencia.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                        "No se encontró la partida para actualizar."
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al actualizar la partida.", e
            );
        }
    }

    // Consultar historial de un usuario.

    public List<HistorialDTO> consultarHistorial(int idUsuario) {

        List<HistorialDTO> historial = new ArrayList<>();

        String sql =
                "SELECT * FROM partidas " +
                        "WHERE id_usuario = ? " +
                        "ORDER BY fecha DESC";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    HistorialDTO dto = new HistorialDTO();

                    dto.modificarId(
                            resultado.getInt("id")
                    );

                    Timestamp fecha =
                            resultado.getTimestamp("fecha");

                    if (fecha != null) {
                        dto.modificarFecha(
                                fecha.toLocalDateTime()
                        );
                    }

                    dto.modificarResultado(
                            resultado.getString("resultado")
                    );

                    dto.modificarApuesta(
                            resultado.getDouble("apuesta_total")
                    );

                    dto.modificarPuntosGanados(
                            resultado.getDouble("puntos_ganados")
                    );

                    dto.modificarPuntosPerdidos(
                            resultado.getDouble("puntos_perdidos")
                    );

                    dto.modificarSaldoFinal(
                            resultado.getDouble("saldo_final")
                    );

                    historial.add(dto);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar el historial.", e
            );
        }

        return historial;
    }

    // Consultar partida por ID.

    public HistorialPartida consultarPorId(int id) {

        String sql =
                "SELECT * FROM partidas WHERE id = ?";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {

                    Timestamp fecha =
                            resultado.getTimestamp("fecha");

                    HistorialPartida partida =
                            new HistorialPartida(
                                    resultado.getInt("id"),
                                    fecha.toLocalDateTime(),
                                    resultado.getDouble("apuesta_total"),
                                    resultado.getDouble("saldo_final")
                            );

                    String resultadoPartida =
                            resultado.getString("resultado");

                    if (resultadoPartida != null) {
                        partida.modificarResultado(
                                Resultados.valueOf(resultadoPartida)
                        );
                    }

                    partida.modificarPuntosGanados(
                            resultado.getDouble("puntos_ganados")
                    );

                    partida.modificarPuntosPerdidos(
                            resultado.getDouble("puntos_perdidos")
                    );

                    return partida;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar la partida.", e
            );
        }

        return null;
    }

    // Consultar partidas que quedaron pendientes.

    public List<HistorialPartida> consultarPartidasPendientes(
            int idUsuario) {

        List<HistorialPartida> pendientes = new ArrayList<>();

        String sql =
                "SELECT * FROM partidas " +
                        "WHERE id_usuario = ? AND resultado IS NULL " +
                        "ORDER BY fecha ASC";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    HistorialPartida partida =
                            new HistorialPartida(
                                    resultado.getInt("id"),
                                    resultado.getTimestamp("fecha")
                                            .toLocalDateTime(),
                                    resultado.getDouble("apuesta_total"),
                                    resultado.getDouble("saldo_final")
                            );

                    pendientes.add(partida);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar las partidas pendientes.", e
            );
        }

        return pendientes;
    }
}