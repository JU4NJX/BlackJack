package co.edu.unilibre.controller;

import co.edu.unilibre.service.JuegoService;

public class JuegoController {

    private JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {

        if (juegoService == null) {
            throw new IllegalArgumentException("El servicio de juego no puede ser nulo.");
        }
        this.juegoService = juegoService;
    }

    // Iniciar partida
    public void iniciarPartida(double apuesta) {
        juegoService.iniciarPartida(apuesta);
    }
    // Pedir carta
    public void pedirCarta(int indiceMano) {
        juegoService.pedirCarta(indiceMano);
    }
    // Plantarse
    public void plantarse(int indiceMano) {
        juegoService.plantarse(indiceMano);
    }
    // Doblar apuesta
    public void doblarApuesta(int indiceMano) {
        juegoService.doblarApuesta(indiceMano);
    }
    // Dividir mano
    public void dividirMano() {
        juegoService.dividirMano();
    }
}
