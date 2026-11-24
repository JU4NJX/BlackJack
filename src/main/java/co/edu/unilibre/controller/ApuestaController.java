package co.edu.unilibre.controller;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.service.ApuestaService;
import co.edu.unilibre.service.UsuarioService;

public class ApuestaController {

    private ApuestaService apuestaService;
    private UsuarioService usuarioService;

    public ApuestaController(ApuestaService apuestaService, UsuarioService usuarioService) {
        if (apuestaService == null || usuarioService == null) {
            throw new IllegalArgumentException("Los servicios no pueden ser nulos.");
        }
        this.apuestaService = apuestaService;
        this.usuarioService = usuarioService;
    }

    public void seleccionarApuesta(double monto) {
        Usuario usuario = usuarioService.obtenerUsuarioModelo();
        if (!validarSaldo(monto)) {
            throw new IllegalArgumentException("La apuesta no es válida o el saldo es insuficiente.");
        }
        Apuesta apuesta = new Apuesta(monto);
        apuestaService.registrarApuesta(apuesta, usuario);
    }

    public boolean validarSaldo(double monto) {
        Usuario usuario = usuarioService.obtenerUsuarioModelo();
        return apuestaService.validarApuesta(usuario.obtenerSaldo(), monto);
    }
}
