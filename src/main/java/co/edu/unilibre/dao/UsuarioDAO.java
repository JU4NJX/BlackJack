package co.edu.unilibre.dao;

import co.edu.unilibre.datebase.ConexionDB;
import co.edu.unilibre.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class UsuarioDAO {

    private ConexionDB conexionDB;

    public UsuarioDAO() {
        this.conexionDB = new ConexionDB();
    }

    protected UsuarioDAO(boolean modoPrueba) {
        if (modoPrueba) {
            this.conexionDB = null;
        } else {
            this.conexionDB = new ConexionDB();
        }
    }

    public void guardar(Usuario usuario, Connection conexion) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo."
            );
        }

        String sql = "INSERT INTO usuarios "
                + "(nombre_usuario, correo, saldo, fecha_registro) "
                + "VALUES (?, ?, ?, ?)";

        Connection conexionTrabajo = conexion;
        boolean cerrarConexion = false;

        try {

            if (conexionTrabajo == null) {
                conexionTrabajo = conexionDB.obtenerConexion();
                cerrarConexion = true;
            }

            try (PreparedStatement sentencia = conexionTrabajo.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )) {

                sentencia.setString(1, usuario.obtenerNombreUsuario());
                sentencia.setString(2, usuario.obtenerCorreo());
                sentencia.setDouble(3, usuario.obtenerSaldo());
                sentencia.setTimestamp(
                        4,
                        Timestamp.valueOf(usuario.obtenerFechaRegistro())
                );

                sentencia.executeUpdate();

                try (ResultSet resultado = sentencia.getGeneratedKeys()) {

                    if (resultado.next()) {
                        usuario.modificarId(resultado.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al guardar el usuario.",
                    e
            );

        } finally {

            if (cerrarConexion && conexionTrabajo != null) {
                try {
                    conexionTrabajo.close();
                } catch (SQLException e) {
                    throw new RuntimeException(
                            "Error al cerrar la conexión.",
                            e
                    );
                }
            }
        }
    }

    public Usuario buscarUsuarioUnico() {

        String sql = "SELECT * FROM usuarios ORDER BY id ASC LIMIT 1";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            if (resultado.next()) {
                return mapearUsuario(resultado);
            }

            return null;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar el usuario único.",
                    e
            );
        }
    }

    public Usuario buscarPorCorreo(String correo) {

        String sql = "SELECT * FROM usuarios WHERE correo = ?";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, correo);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar usuario por correo.",
                    e
            );
        }

        return null;
    }


    public Usuario buscarPorId(int id) {

        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conexion = conexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al buscar usuario por ID.",
                    e
            );
        }

        return null;
    }


    public void actualizarSaldo(Usuario usuario, Connection conexion) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo."
            );
        }

        String sql = "UPDATE usuarios SET saldo = ? WHERE id = ?";

        Connection conexionTrabajo = conexion;
        boolean cerrarConexion = false;

        try {

            if (conexionTrabajo == null) {
                conexionTrabajo = conexionDB.obtenerConexion();
                cerrarConexion = true;
            }

            try (PreparedStatement sentencia =
                         conexionTrabajo.prepareStatement(sql)) {

                sentencia.setDouble(1, usuario.obtenerSaldo());
                sentencia.setInt(2, usuario.obtenerId());

                int filasActualizadas = sentencia.executeUpdate();

                if (filasActualizadas == 0) {
                    throw new IllegalArgumentException(
                            "No existe el usuario que se desea actualizar."
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al actualizar el saldo.",
                    e
            );

        } finally {

            if (cerrarConexion && conexionTrabajo != null) {
                try {
                    conexionTrabajo.close();
                } catch (SQLException e) {
                    throw new RuntimeException(
                            "Error al cerrar la conexión.",
                            e
                    );
                }
            }
        }
    }

    private Usuario mapearUsuario(ResultSet resultado)
            throws SQLException {

        return new Usuario(
                resultado.getInt("id"),
                resultado.getString("nombre_usuario"),
                resultado.getString("correo"),
                resultado.getDouble("saldo"),
                resultado.getTimestamp("fecha_registro").toLocalDateTime()
        );
    }
}
