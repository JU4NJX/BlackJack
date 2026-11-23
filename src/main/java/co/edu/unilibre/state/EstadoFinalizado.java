package co.edu.unilibre.state;

import co.edu.unilibre.model.Partida;

public class EstadoFinalizado implements Estado {

    @Override
    public void iniciar(Partida partida) {
        throw new IllegalStateException("La partida ya ha finalizado.");
    }

    @Override
    public void pedirCarta(Partida partida, int indiceMano) {
        throw new IllegalStateException("No se pueden pedir cartas en una partida finalizada.");
    }

    @Override
    public void plantarse(Partida partida, int indiceMano) {
        throw new IllegalStateException("No se puede plantar en una partida finalizada.");
    }

    @Override
    public void ejecutarTurno(Partida partida) {
        throw new IllegalStateException("No se pueden ejecutar turnos en una partida finalizada.");
    }
}