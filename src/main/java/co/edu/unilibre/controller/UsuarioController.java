package co.edu.unilibre.controller;

import co.edu.unilibre.dto.UsuarioDTO;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.service.UsuarioService;

public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        if (usuarioService == null) {
            throw new IllegalArgumentException("El servicio de usuario no puede ser nulo.");
        }
        this.usuarioService = usuarioService;
    }

    public UsuarioDTO obtenerUsuario() {
        return usuarioService.obtenerUsuario();
    }
    public double consultarSaldo(int idUsuario) {
        return usuarioService.consultarSaldo(idUsuario);
    }
    public void actualizarSaldo(Usuario usuario) {
        usuarioService.actualizarSaldo(usuario);
    }
}
