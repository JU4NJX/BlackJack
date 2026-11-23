
package service;

import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Partida;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.service.ApuestaService;

public class ApuestaServicePrueba extends ApuestaService {

    @Override
    public void registrarApuesta(
            Apuesta apuesta,
            Usuario usuario,
            Partida partida) {
        // Simula el registro exitoso de la apuesta.
    }

    @Override
    public boolean procesarDobleApuesta(
            Apuesta apuesta,
            Usuario usuario) {
        return true;
    }

    @Override
    public boolean procesarSplit(
            Apuesta apuesta,
            Usuario usuario) {
        return true;
    }

    @Override
    public void liquidarPartida(
            Partida partida,
            Usuario usuario) {
        // Simula la liquidación exitosa de la partida.
    }
}
