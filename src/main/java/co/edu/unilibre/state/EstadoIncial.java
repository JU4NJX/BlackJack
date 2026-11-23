
package co.edu.unilibre.state;

import co.edu.unilibre.model.Partida;

public class EstadoInicial implements Estado {

    @Override
    public void iniciar(Partida partida) {
        partida.iniciar();
        partida.modificarEstadoActual(new EstadoTurnoJugador());
    }

    @Override
    public void pedirCarta(Partida partida, int indiceMano) {
        throw new IllegalStateException("No se pueden pedir cartas antes de iniciar la partida."
        );
    }

    @Override
    public void plantarse(Partida partida, int indiceMano) {
        throw new IllegalStateException("No se puede plantar antes de iniciar la partida."
        );
    }

    @Override
    public void ejecutarTurno(Partida partida) {
        throw new IllegalStateException("No se puede ejecutar el turno antes de iniciar la partida."
        );
    }
}

