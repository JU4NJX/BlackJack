package co.edu.unilibre.state;

import co.edu.unilibre.model.Partida;

public interface Estado {
    void iniciar(Partida partida);
    void pedirCarta(Partida partida, int indiceMano);
    void plantarse(Partida partida, int indiceMano);
    void ejecutarTurno(Partida partida);
}

