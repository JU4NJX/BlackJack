package co.edu.unilibre.state;

import co.edu.unilibre.model.Partida;

public class EstadoTurnoCrupier implements Estado {
    @Override
    public void iniciar(Partida partida) {
        throw new IllegalStateException("La partida ya ha sido iniciada.");
    }

    @Override
    public void pedirCarta(Partida partida, int indiceMano) {
        throw new IllegalStateException("No se pueden pedir cartas durante el turno del crupier.");
    }

    @Override
    public void plantarse(Partida partida, int indiceMano) {
        throw new IllegalStateException("El jugador no puede realizar movimientos durante el turno del crupier.");
    }

    @Override
    public void ejecutarTurno(Partida partida) {
        partida.ejecutarTurnoCrupierDesdeEstado();
        partida.finalizar();
        partida.modificarEstadoActual(new EstadoFinalizado());
    }
}


