package co.edu.unilibre.state;

import co.edu.unilibre.model.Partida;

public class EstadoTurnoJugador implements Estado {

    @Override
    public void iniciar(Partida partida) {
        throw new IllegalStateException("La partida ya ha sido iniciada.");
    }

    @Override
    public void pedirCarta(Partida partida, int indiceMano) {
        partida.pedirCartaDesdeEstado(indiceMano);
    }

    @Override
    public void plantarse(Partida partida, int indiceMano) {
        partida.plantarseDesdeEstado(indiceMano);
    }

    @Override
    public void ejecutarTurno(Partida partida) {
        partida.modificarEstadoActual(
                new EstadoTurnoCrupier()
        );
    }
}