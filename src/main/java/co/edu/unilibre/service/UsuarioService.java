package co.edu.unilibre.service;

import co.edu.unilibre.dao.UsuarioDAO;
import co.edu.unilibre.dto.UsuarioDTO;

public class UsuarioService {

    private UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO){
    }

    public UsuarioDTO registarUsuario(String nombre, String correo){
        return null;
    }

    public UsuarioDTO autenticarUsuario(String correo){
        return null;
    }

    public UsuarioDTO obtenerUsuario(Long id){
        return null;
    }

    public void actualizarSaldo(Long idUsuario, int saldo){
    }

}
