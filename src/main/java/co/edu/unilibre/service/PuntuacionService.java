package co.edu.unilibre.service;

import co.edu.unilibre.model.Mano;
import co.edu.unilibre.model.ManoJugador;
import co.edu.unilibre.model.enums.Resultado;

public class PuntuacionService {

    public int calcularPuntuacion(Mano mano) {
        if (mano == null) {
            throw new IllegalArgumentException(
                    "La mano no puede ser nula."
            );
        }

        return mano.calcularPuntuacion();
    }

    public boolean tieneBlackjack(Mano mano) {
        if (mano == null) {
            throw new IllegalArgumentException(
                    "La mano no puede ser nula."
            );
        }

        return mano.tieneBlackjackNatural();
    }

    public Resultado determinarResultado(
            ManoJugador jugador,
            Mano crupier) {

        if (jugador == null || crupier == null) {
            throw new IllegalArgumentException(
                    "Las manos no pueden ser nulas."
            );
        }

        boolean blackjackJugador = tieneBlackjack(jugador);
        boolean blackjackCrupier = tieneBlackjack(crupier);

        // Ambos tienen blackjack natural.
        if (blackjackJugador && blackjackCrupier) {
            return Resultado.EMPATE;
        }

        // El jugador tiene blackjack natural.
        if (blackjackJugador) {
            return Resultado.BLACKJACK;
        }

        // El crupier tiene blackjack natural.
        if (blackjackCrupier) {
            return Resultado.PERDIDA;
        }

        // El jugador supera los 21 puntos.
        if (jugador.superaVeintiuno()) {
            return Resultado.PERDIDA;
        }

        // El crupier supera los 21 puntos.
        if (crupier.superaVeintiuno()) {
            return Resultado.GANADA;
        }

        return compararPuntuaciones(jugador, crupier);
    }

    public Resultado compararPuntuaciones(
            Mano jugador,
            Mano crupier) {

        if (jugador == null || crupier == null) {
            throw new IllegalArgumentException(
                    "Las manos no pueden ser nulas."
            );
        }

        int puntuacionJugador = calcularPuntuacion(jugador);
        int puntuacionCrupier = calcularPuntuacion(crupier);

        if (puntuacionJugador > 21) {
            return Resultado.PERDIDA;
        }

        if (puntuacionCrupier > 21) {
            return Resultado.GANADA;
        }

        if (puntuacionJugador > puntuacionCrupier) {
            return Resultado.GANADA;
        }

        if (puntuacionJugador < puntuacionCrupier) {
            return Resultado.PERDIDA;
        }

        return Resultado.EMPATE;
    }
}