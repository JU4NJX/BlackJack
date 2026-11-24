package co.edu.unilibre.view;

import co.edu.unilibre.dto.CartaDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelJugador extends JPanel {

    private List<CartaDTO> cartas;
    private int puntuacion;

    private JLabel etiquetaTitulo;
    private JLabel etiquetaPuntuacion;
    private JPanel panelCartas;

    public PanelJugador() {
        this.cartas = new java.util.ArrayList<>();
        this.puntuacion = 0;

        configurarPanel();
        inicializarComponentes();
    }

    private void configurarPanel() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 90, 60));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void inicializarComponentes() {

        etiquetaTitulo = new JLabel("JUGADOR", SwingConstants.CENTER);
        etiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        etiquetaTitulo.setForeground(Color.WHITE);

        etiquetaPuntuacion = new JLabel("Puntuación: 0", SwingConstants.CENTER);
        etiquetaPuntuacion.setFont(new Font("Arial", Font.BOLD, 16));
        etiquetaPuntuacion.setForeground(Color.WHITE);

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1));
        panelSuperior.setOpaque(false);
        panelSuperior.add(etiquetaTitulo);
        panelSuperior.add(etiquetaPuntuacion);

        panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelCartas.setOpaque(false);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelCartas, BorderLayout.CENTER);
    }

    public void mostrarCartas(List<CartaDTO> cartas) {

        if (cartas == null) {
            throw new IllegalArgumentException("La lista de cartas no puede ser nula.");
        }

        this.cartas = new java.util.ArrayList<>(cartas);

        panelCartas.removeAll();

        for (CartaDTO carta : this.cartas) {

            JLabel etiquetaCarta = new JLabel(obtenerTextoCarta(carta), SwingConstants.CENTER);

            etiquetaCarta.setPreferredSize(new Dimension(75, 105));
            etiquetaCarta.setOpaque(true);
            etiquetaCarta.setBackground(Color.WHITE);
            etiquetaCarta.setForeground(Color.BLACK);
            etiquetaCarta.setFont(new Font("Arial", Font.BOLD, 15));
            etiquetaCarta.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            panelCartas.add(etiquetaCarta);
        }

        panelCartas.revalidate();
        panelCartas.repaint();
    }

    private String obtenerTextoCarta(CartaDTO carta) {

        if (carta == null) {
            return "Carta";
        }

        if (!carta.obtenerVisible()) {
            return "OCULTA";
        }

        String valor = String.valueOf(carta.obtenerValor());
        String palo = String.valueOf(carta.obtenerPalo());

        return "<html><center>" + valor + "<br>" + palo + "</center></html>";
    }

    public void actualizarPuntuacion(int puntuacion) {

        this.puntuacion = puntuacion;
        etiquetaPuntuacion.setText("Puntuación: " + puntuacion);
    }

    public List<CartaDTO> obtenerCartas() {
        return new java.util.ArrayList<>(cartas);
    }

    public void modificarCartas(List<CartaDTO> cartas) {

        if (cartas == null) {
            throw new IllegalArgumentException("La lista de cartas no puede ser nula.");
        }

        mostrarCartas(cartas);
    }

    public int obtenerPuntuacion() {
        return puntuacion;
    }

    public void modificarPuntuacion(int puntuacion) {
        actualizarPuntuacion(puntuacion);
    }
}
