package co.edu.unilibre.service;

import co.edu.unilibre.dao.MovimientoPuntosDAO;
import co.edu.unilibre.dao.UsuarioDAO;
import co.edu.unilibre.model.Apuesta;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.view.Resultado;

public class ApuestaService {

    private UsuarioDAO usuarioDAO;
    private MovimientoPuntosDAO movimientoPuntosDAO;

    public ApuestaService(){
    }

    public boolean validadApuesta(Usuario usuario, int cantidad){
        return true;
    }

    public void descontarApuesta(Usuario usuario, int cantidad){
    }

    public void procesarDobleApuesta(Usuario usuario, Apuesta apuesta){
    }

    public void procesarDivision(Usuario usuario, Apuesta apuesta){
    }

    public int calcularPago(Apuesta apuesta, Resultado resultado){
        return 0;
    }

    public void devolverApuesta(Usuario usuario, int cantidad){
    }

    public void aplicarRecuperacion(Usuario usuario){
    }


}
