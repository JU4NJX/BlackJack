package co.edu.unilibre.datebase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private String URL;
    private String usuario;
    private String clave;

    public ConexionDB() {
        this.URL = "jdbc:postgresql://ep-weathered-voice-bek0iffz-pooler.c-9.us-east-2.aws.neon.tech/neondb?sslmode=require";
        this.usuario = "neondb_owner";
        this.clave = "npg_z7lBWgTj2SfV";
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
            return DriverManager.getConnection(URL, usuario, clave);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos Neon", e
            );
        }
    }

    public void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error al cerrar la conexión", e
                );
            }
        }
    }
}
