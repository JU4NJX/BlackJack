package co.edu.unilibre.controller;

import co.edu.unilibre.dto.UsuarioDTO;
import co.edu.unilibre.service.UsuarioService;

import java.util.stream.Stream;

public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public UsuarioDTO registarUsuario(String nombre, String coreo){
        return null;
    }

    public UsuarioDTO iniciarSesion(String correo){
        return null;
    }

    public UsuarioDTO obtenerUsuario(Long id){
        return null;
    }

}
