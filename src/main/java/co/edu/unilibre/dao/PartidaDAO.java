
package co.edu.unilibre.dao;

import co.edu.unilibre.model.HistorialPartida;
import co.edu.unilibre.model.enums.Resultado;
import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.datebase.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {

    private ConexionDB conexionDB;

    public PartidaDAO() {
        this.conexionDB = new ConexionDB();
    }

    // Guardar partida

    public void guardar(HistorialPartida partida, int idUsuario,
                        Connection conexion) {

        String sql = "INSERT INTO partidas " + "(id_usuario, fecha, resultado, apuesta_total, " + "puntos_ganados, puntos_perdidos, saldo_final) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);

            sentencia.setTimestamp(
                    2, Timestamp.valueOf(partida.obtenerFecha())
            );

            if (partida.obtenerResultado() != null) {
                sentencia.setString(
                        3, partida.obtenerResultado().name()
                );
            } else {
                sentencia.setNull(3, java.sql.Types.VARCHAR);
            }

            sentencia.setDouble(4, partida.obtenerApuestaTotal());
            sentencia.setDouble(5, partida.obtenerPuntosGanados());
            sentencia.setDouble(6, partida.obtenerPuntosPerdidos());
            sentencia.setDouble(7, partida.obtenerSaldoFinal());

            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la partida.", e);
        }
    }

    // Consultar historial de un usuario

    public List<HistorialDTO> consultarHistorial(int idUsuario) {

        List<HistorialDTO> historial = new ArrayList<>();

        String sql = "SELECT * FROM partidas "
                + "WHERE id_usuario = ? ORDER BY fecha DESC";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    HistorialDTO dto = new HistorialDTO();

                    dto.modificarId(resultado.getInt("id"));

                    dto.modificarFecha(
                            resultado.getTimestamp("fecha").toLocalDateTime()
                    );

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
                    "Error al consultar el historial de partidas.", e
            );
        }

        return historial;
    }

    // Consultar partida por ID

    public HistorialPartida consultarPorId(int id) {

        String sql = "SELECT * FROM partidas WHERE id = ?";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {

                    HistorialPartida partida = new HistorialPartida(
                            resultado.getInt("id"),
                            resultado.getTimestamp("fecha").toLocalDateTime(),
                            resultado.getDouble("apuesta_total"),
                            resultado.getDouble("saldo_final")
                    );

                    String resultadoPartida =
                            resultado.getString("resultado");

                    if (resultadoPartida != null) {
                        partida.modificarResultado(
                                Resultado.valueOf(resultadoPartida)
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
                    "Error al consultar la partida por ID.", e
            );
        }

        return null;
    }
}

