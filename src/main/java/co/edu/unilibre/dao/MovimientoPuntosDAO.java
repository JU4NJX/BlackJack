package co.edu.unilibre.dao;

import co.edu.unilibre.model.MovimientoPuntos;
import co.edu.unilibre.model.enums.TipoMovimiento;
import co.edu.unilibre.datebase.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class MovimientoPuntosDAO {

    private ConexionDB conexionDB;

    public MovimientoPuntosDAO() {
        this.conexionDB = new ConexionDB();
    }

    // Registrar movimiento

    public void registrarMovimiento(int idUsuario, TipoMovimiento tipo, double cantidad, Connection conexion) {

        String sql = "INSERT INTO movimientos_puntos " + "(id_usuario, tipo, cantidad) " + "VALUES (?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idUsuario);
            sentencia.setString(2, tipo.name());
            sentencia.setDouble(3, cantidad);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al registrar el movimiento de puntos.", e
            );
        }
    }

    // Consultar movimientos de un usuario

    public List<MovimientoPuntos> consultarMovimientos(int idUsuario) {

        List<MovimientoPuntos> movimientos = new ArrayList<>();

        String sql = "SELECT * FROM movimientos_puntos " + "WHERE id_usuario = ? ORDER BY fecha DESC";

        try (Connection conexion = conexionDB.obtenerConexion(); PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    MovimientoPuntos movimiento = new MovimientoPuntos(resultado.getInt("id"), resultado.getInt("id_usuario"), TipoMovimiento.valueOf(resultado.getString("tipo")),
                            resultado.getDouble("cantidad"),
                            resultado.getTimestamp("fecha").toLocalDateTime()
                    );

                    movimientos.add(movimiento);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los movimientos de puntos.", e);
        }
        return movimientos;
    }
}