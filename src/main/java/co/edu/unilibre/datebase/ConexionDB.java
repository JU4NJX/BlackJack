package co.edu.unilibre.datebase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private String usuario;
    private String clave;
    private String url;
    private String host;
    private String puerto;
    private String bd;

    public ConexionDB() {

        this.usuario = "neondb_owner";
        this.clave = "npg_z7lBWgTj2SfV";
        this.host = "ep-weathered-voice-bek0iffz-pooler.c-9.us-east-2.aws.neon.tech";
        this.puerto = "5432";
        this.bd = "neondb";

        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank() || host == null || host.isBlank() || puerto == null || puerto.isBlank() || bd == null || bd.isBlank()) {

            throw new IllegalStateException("Faltan las variables de entorno de la base de datos.");
        }

        this.url = "jdbc:postgresql://" + host + ":" + puerto + "/" + bd + "?sslmode=require&channel_binding=require";
    }

    public Connection obtenerConexion() {

        try {
            return DriverManager.getConnection(url, usuario, clave);

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