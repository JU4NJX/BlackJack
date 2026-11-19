package co.edu.unilibre.controller;

import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.service.HistorialService;

import java.util.ArrayList;
import java.util.List;

public class HistorialController {

    private HistorialService historialService;

    public HistorialController(HistorialService historialService){
        this.historialService = historialService;
    }

    public List<HistorialDTO> consultarHistorial(Long idUsuario){
        List<HistorialDTO> historial = new ArrayList<>();

        return historial;
    }

}
