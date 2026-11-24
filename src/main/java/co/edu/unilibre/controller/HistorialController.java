package co.edu.unilibre.controller;

import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.service.HistorialService;

import java.util.List;

public class HistorialController {

    private HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        if (historialService == null) {
            throw new IllegalArgumentException("El servicio de historial no puede ser nulo.");
        }
        this.historialService = historialService;
    }

    public List<HistorialDTO> cargarHistorial(int idUsuario) {
        return historialService.consultarHistorial(idUsuario);
    }
}

