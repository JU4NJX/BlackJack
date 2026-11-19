package co.edu.unilibre.dao;

import co.edu.unilibre.model.Usuario;

import java.sql.Connection;

public class UsuarioDAO {
    public boolean registrar(Usuario usuario){
        return true;
    }

    public Usuario obtenerPorld(Long id){
        return null;
    }

    public Usuario obtenerPorCorreo(String correo){
        return null;
    }

    public void actualizarSaldo(Usuario usuario , Connection conexion){
        return;
    }

    public boolean existeCorreo(String correo){
        return false;
    }
}
