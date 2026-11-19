package co.edu.unilibre.controller;

import co.edu.unilibre.service.ApuestaService;
import co.edu.unilibre.view.Resultado;

public class ApuestaController {

    private ApuestaService apuestaService;

    public ApuestaController(ApuestaService apuestaService){
        this.apuestaService = apuestaService;

    }

    public boolean validarApuesta(int cantidad){
        return false;
    }

    public int calcularPago(Resultado resultado){
        return 0;
    }

    public int obtenerSaldoDisponible(Long idUsuario){
        return 0;
    }

}
