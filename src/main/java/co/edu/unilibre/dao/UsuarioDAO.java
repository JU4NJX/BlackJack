
package co.edu.unilibre.dao;

import co.edu.unilibre.datebase.ConexionDB;
import co.edu.unilibre.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UsuarioDAO {

    private ConexionDB conexionDB;

    public UsuarioDAO() {
        this.conexionDB = new ConexionDB();
    }

    // Guardar usuario

    public void guardar(Usuario usuario, Connection conexion) {

        String sql = "INSERT INTO usuarios " + "(nombre_usuario, correo, clave_hash, saldo, fecha_registro) " + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, usuario.obtenerNombreUsuario());
            sentencia.setString(2, usuario.obtenerCorreo());
            sentencia.setString(3, usuario.obtenerClaveHash());
            sentencia.setDouble(4, usuario.obtenerSaldo());
            sentencia.setTimestamp(
                    5, Timestamp.valueOf(usuario.obtenerFechaRegistro())
            );

            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el usuario.", e);
        }
    }

    // Buscar usuario por correo

    public Usuario buscarPorCorreo(String correo) {

        String sql = "SELECT * FROM usuarios WHERE correo = ?";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, correo);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {

                    return new Usuario(
                            resultado.getInt("id"),
                            resultado.getString("nombre_usuario"),
                            resultado.getString("correo"),
                            resultado.getString("clave_hash"),
                            resultado.getDouble("saldo"),
                            resultado.getTimestamp("fecha_registro")
                                    .toLocalDateTime()
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar usuario por correo.", e
            );
        }

        return null;
    }

    // Buscar usuario por ID

    public Usuario buscarPorId(int id) {

        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {

                    return new Usuario(
                            resultado.getInt("id"),
                            resultado.getString("nombre_usuario"),
                            resultado.getString("correo"),
                            resultado.getString("clave_hash"),
                            resultado.getDouble("saldo"),
                            resultado.getTimestamp("fecha_registro")
                                    .toLocalDateTime()
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al buscar usuario por ID.", e
            );
        }

        return null;
    }

    // Actualizar saldo

    public void actualizarSaldo(Usuario usuario, Connection conexion) {

        String sql = "UPDATE usuarios SET saldo = ? WHERE id = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setDouble(1, usuario.obtenerSaldo());
            sentencia.setInt(2, usuario.obtenerId());

            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el saldo.", e);
        }
    }
}
