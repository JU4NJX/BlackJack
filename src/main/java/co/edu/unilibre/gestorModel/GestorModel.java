package co.edu.unilibre.gestorModel;

import co.edu.unilibre.state.Estado;
import co.edu.unilibre.view.Resultado;

import java.util.List;

public class GestorModel {
    public int calcularBalance (){
        return 0;
    }
    public boolean debitarPuntos (int cantidad) {return false;}
    public boolean acreditarPuntos (int cantidad) {return false;}
    public boolean saldoSuficiente (int cantidad) {return false;}
    public List<Resultado> determinarResultado (){return null;}
    public boolean finalizada (){return false;}
}

