package co.edu.unilibre.observer;

import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.view.PanelMesa;

public class ObservadorJuego {
    private PanelMesa panelMesa;
    public ObservadorJuego(PanelMesa panelMesa) {
        this.panelMesa = panelMesa;
    }
    public void actualizar(PartidaDTO partida) {
    }
}
