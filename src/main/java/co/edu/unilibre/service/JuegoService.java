package co.edu.unilibre.service;

import co.edu.unilibre.dao.MovimientoPuntosDAO;
import co.edu.unilibre.dao.PartidaDAO;
import co.edu.unilibre.dao.UsuarioDAO;
import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.factory.MazoFactory;
import co.edu.unilibre.model.Partida;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.observer.Observador;

import java.util.List;

public class JuegoService {

    private UsuarioDAO usuarioDAO;
    private PartidaDAO partidaDAO;
    private MovimientoPuntosDAO movimientoPuntosDAO;
    private PuntuacionService puntuacionService;
    private ApuestaService apuestaService;
    private MazoFactory mazoFactory;
    private List<Observador> observadores;
    private Partida partidaActual;


    public JuegoService(){
    }

    public PartidaDTO iniciarPartida(Usuario usuario, int apuesta){
        return null;
    }

    public PartidaDTO pedirCarta(int indiceMano){
        return null;
    }

    public PartidaDTO plantarse(int indiceMano){
        return null;
    }

    public PartidaDTO doblarApuesta(int indiceMano){
        return null;
    }

    public PartidaDTO dividirMano(int indiceMano){
        return null;
    }

    public void ejecutarTurnoCrupier(){
    }

    public void finalizarPartida(){
    }

    public PartidaDTO obtenerPartidaActual(){
        return null;
    }

    public void liquidarPartida(){
    }

    public void registrarMovimientos(){
    }

    public void agregarObservador(Observador observador){
        observadores.add(observador);
    }

    public void eliminarObservador(Observador observador){
        observadores.remove(observador);
    }

    public void notificarObservadores(){
    }

}
