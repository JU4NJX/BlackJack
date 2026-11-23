package co.edu.unilibre.service;

import co.edu.unilibre.dao.PartidaDAO;
import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.model.HistorialPartida;

import java.util.List;

public class HistorialService {

    private PartidaDAO partidaDAO;

    public HistorialService() {
        this.partidaDAO = new PartidaDAO();
    }

    public void registrarPartida(HistorialPartida partida) {

        if (partida == null) {
            throw new IllegalArgumentException("La partida del historial no puede ser nula.");
        }

        if (partida.obtenerResultado() == null) {
            throw new IllegalArgumentException("No se puede registrar una partida sin resultado.");
        }

        // El registro requiere idUsuario y Connection según el DAO.
        throw new UnsupportedOperationException("Pendiente integrar el registro con la conexión y el usuario.");
    }

    public List<HistorialDTO> consultarHistorial(int idUsuario) {

        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El identificador del usuario no es válido.");
        }
        List<HistorialDTO> historial = partidaDAO.consultarHistorial(idUsuario);
        return historial;
    }
}

