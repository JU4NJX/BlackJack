
package co.edu.unilibre.datebase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private String URL;
    private String usuario;
    private String clave;

    public ConexionDB() {
        this.URL = System.getenv("DB_URL");
        this.usuario = System.getenv("DB_USER");
        this.clave = System.getenv("DB_PASSWORD");

        if (URL == null || usuario == null || clave == null) {
            throw new IllegalStateException("Faltan las variables de entorno de la base de datos.");
        }
    }

    public String obtenerURL() {
        return URL;
    }

    public void modificarURL(String URL) {
        this.URL = URL;
    }

    public String obtenerUsuario() {
        return usuario;
    }

    public void modificarUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String obtenerClave() {
        return clave;
    }

    public void modificarClave(String clave) {
        this.clave = clave;
    }

    public Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(
                    URL, usuario, clave
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con PostgreSQL.", e);
        }
    }

    public void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error al cerrar la conexión.", e);
            }
        }
    }
}