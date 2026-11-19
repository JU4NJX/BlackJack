package co.edu.unilibre.service;

import co.edu.unilibre.dao.MovimientoPuntosDAO;
import co.edu.unilibre.dao.PartidaDAO;
import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.model.enums.TipoMovimiento;

import java.util.List;

public class HistorialService {

    private PartidaDAO partidaDAO;
    private MovimientoPuntosDAO movimientoPuntosDAO;

    public HistorialService(){
    }

    public void registrarPartida(PartidaDAO partidaDAO){
    }

    public List<HistorialDTO> consultarHistorial(Long idUsuario){
        return null;
    }

    public List<TipoMovimiento> consultarMovimientos(Long idUsuario){
        return null;
    }
}
