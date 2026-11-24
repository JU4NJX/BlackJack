package co.edu.unilibre.service;

import co.edu.unilibre.dao.UsuarioDAO;
import co.edu.unilibre.dto.UsuarioDTO;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.util.Constantes;

public class UsuarioService {

    private UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        if (usuarioDAO == null) {
            throw new IllegalArgumentException("El DAO no puede ser nulo.");
        }
        this.usuarioDAO = usuarioDAO;
    }

    public UsuarioDTO obtenerUsuario() {
        Usuario usuario = obtenerUsuarioModelo();
        return convertirUsuarioDTO(usuario);
    }

    public Usuario obtenerUsuarioModelo() {

        Usuario usuario = usuarioDAO.buscarUsuarioUnico();

        if (usuario == null) {

            usuario = new Usuario(0, "Jugador", "jugador@blackjack.com", Constantes.SALDO_INICIAL, java.time.LocalDateTime.now());

            usuarioDAO.guardar(usuario, null);

            if (usuario.obtenerId() <= 0) {
                throw new IllegalStateException("No fue posible crear el usuario predeterminado.");
            }
        }
        return usuario;
    }

    public double consultarSaldo(int idUsuario) {

        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El identificador del usuario no es válido.");
        }

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe.");
        }
        return usuario.obtenerSaldo();
    }

    public void actualizarSaldo(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (usuario.obtenerId() <= 0) {
            throw new IllegalArgumentException("El identificador del usuario no es válido.");
        }
        if (usuario.obtenerSaldo() < 0 || !Double.isFinite(usuario.obtenerSaldo())) {
            throw new IllegalArgumentException("El saldo no es válido.");
        }

        usuarioDAO.actualizarSaldo(usuario, null);
    }

    private UsuarioDTO convertirUsuarioDTO(Usuario usuario) {

        UsuarioDTO dto = new UsuarioDTO();

        dto.modificarId(usuario.obtenerId());
        dto.modificarNombreUsuario(usuario.obtenerNombreUsuario());
        dto.modificarCorreo(usuario.obtenerCorreo());
        dto.modificarSaldo(usuario.obtenerSaldo());

        return dto;
    }
}
