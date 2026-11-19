package co.edu.unilibre.controller;

import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.service.JuegoService;
import co.edu.unilibre.view.VentanaPrincipal;

public class JuegoController {

    private JuegoService juegoService;
    private VentanaPrincipal ventanaPrincipal;

    public JuegoController(JuegoService juegoService, VentanaPrincipal ventanaPrincipal){
        this.juegoService = juegoService;
        this.ventanaPrincipal = ventanaPrincipal;
    }

    public void iniciarPartida(int apuesta){
        return ;
    }

    public void pedirCarta(int indeceMano){
        return;
    }

    public void pplantarse(int indeceMano){
        return;
    }

    public void doblarApuesta(int indeceMano){
        return;
    }

    public void dividirMano(int indeceMano){
        return;
    }

    public PartidaDTO obtenerPartidaActual(){
        return null;
    }
    public void finalizarPartida(){
        return;
    }
}
