package co.edu.unilibre.view;

import co.edu.unilibre.dto.CartaDTO;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PanelCrupier extends JPanel {

    private List<CartaDTO> cartas;
    private int puntuacion;

    private JLabel etiquetaTitulo;
    private JLabel etiquetaPuntuacion;
    private JPanel panelCartas;

    public PanelCrupier() {

        this.cartas = new ArrayList<>();
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

        etiquetaTitulo = new JLabel("CRUPIER", SwingConstants.CENTER);
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

        this.cartas = new ArrayList<>(cartas);

        panelCartas.removeAll();

        for (CartaDTO carta : this.cartas) {

            JLabel etiquetaCarta = new JLabel();
            etiquetaCarta.setPreferredSize(new Dimension(90, 130));
            etiquetaCarta.setHorizontalAlignment(SwingConstants.CENTER);

            if (carta == null) {
                etiquetaCarta.setText("Carta");
            } else {

                ImageIcon imagen = cargarImagenCarta(carta);

                if (imagen != null) {
                    etiquetaCarta.setIcon(imagen);
                } else {
                    etiquetaCarta.setText(obtenerTextoCarta(carta));
                    etiquetaCarta.setOpaque(true);
                    etiquetaCarta.setBackground(Color.WHITE);
                    etiquetaCarta.setForeground(Color.BLACK);
                    etiquetaCarta.setFont(new Font("Arial", Font.BOLD, 13));
                    etiquetaCarta.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                }
            }

            panelCartas.add(etiquetaCarta);
        }

        panelCartas.revalidate();
        panelCartas.repaint();
    }

    private ImageIcon cargarImagenCarta(CartaDTO carta) {
        if (carta == null) {return null;}
        if (!carta.obtenerVisible()) {return cargarImagen("/img/reverso.png");}
        String nombreImagen = carta.obtenerImagen();
        if (nombreImagen == null || nombreImagen.trim().isEmpty()) {return null;}
        return cargarImagen("/img/" + nombreImagen);
    }

    private ImageIcon cargarImagen(String ruta) {

        URL recurso = getClass().getResource(ruta);

        if (recurso == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            return null;
        }

        ImageIcon imagenOriginal = new ImageIcon(recurso);
        Image imagenEscalada = imagenOriginal.getImage().getScaledInstance(90, 130, Image.SCALE_SMOOTH);

        return new ImageIcon(imagenEscalada);
    }

    private String obtenerTextoCarta(CartaDTO carta) {
        if (carta == null) {return "Carta";}
        if (!carta.obtenerVisible()) {return "OCULTA";}
        return "<html><center>" + carta.obtenerValor() + "<br>" + carta.obtenerPalo() + "</center></html>";
    }

    public void actualizarPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
        etiquetaPuntuacion.setText("Puntuación: " + puntuacion);
    }

    public List<CartaDTO> obtenerCartas() {
        return new ArrayList<>(cartas);
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