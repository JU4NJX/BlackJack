package co.edu.unilibre.view;

import co.edu.unilibre.dto.CartaDTO;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PanelJugador extends JPanel {

    private List<CartaDTO> cartas;
    private int puntuacion;

    private JLabel etiquetaTitulo;
    private JLabel etiquetaPuntuacion;
    private JPanel panelCartas;
    private JPanel panelManos;

    public PanelJugador() {
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

        panelManos = new JPanel(new GridLayout(1, 2, 20, 10));
        panelManos.setOpaque(false);
        panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelCartas.setOpaque(false);
        add(panelSuperior, BorderLayout.NORTH);
        add(panelManos, BorderLayout.CENTER);
    }

    // Mostrar una sola mano

    public void mostrarCartas(List<CartaDTO> cartas) {

        if (cartas == null) {
            throw new IllegalArgumentException("La lista de cartas no puede ser nula.");
        }

        this.cartas = new ArrayList<>(cartas);

        panelManos.removeAll();
        panelCartas = crearPanelCartas(cartas);
        panelManos.add(panelCartas);
        panelManos.revalidate();
        panelManos.repaint();
    }

    // Mostrar varias manos después de dividir

    public void mostrarManos(List<List<CartaDTO>> manos, int indiceSeleccionado) {
        if (manos == null) {
            throw new IllegalArgumentException("La lista de manos no puede ser nula.");
        }

        panelManos.removeAll();

        for (int i = 0; i < manos.size(); i++) {
            List<CartaDTO> mano = manos.get(i);
            JPanel contenedor = new JPanel(new BorderLayout(5, 5));
            contenedor.setOpaque(false);
            JLabel titulo = new JLabel("MANO " + (i + 1), SwingConstants.CENTER);
            titulo.setFont(new Font("Arial", Font.BOLD, 16));
            titulo.setForeground(Color.WHITE);

            if (i == indiceSeleccionado) {
                titulo.setText("▶ MANO " + (i + 1));
                titulo.setForeground(Color.YELLOW);
            }

            JPanel cartasMano = crearPanelCartas(mano);

            JLabel puntuacionMano = new JLabel("Puntuación: " + calcularPuntuacion(mano), SwingConstants.CENTER);

            puntuacionMano.setForeground(Color.WHITE);
            puntuacionMano.setFont(new Font("Arial", Font.BOLD, 14));

            contenedor.add(titulo, BorderLayout.NORTH);
            contenedor.add(cartasMano, BorderLayout.CENTER);
            contenedor.add(puntuacionMano, BorderLayout.SOUTH);

            panelManos.add(contenedor);
        }

        if (!manos.isEmpty()) {
            int indice = Math.max(0, Math.min(indiceSeleccionado, manos.size() - 1));
            this.cartas = new ArrayList<>(manos.get(indice));
        }

        panelManos.revalidate();
        panelManos.repaint();
    }

    private JPanel crearPanelCartas(List<CartaDTO> cartas) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));

        panel.setOpaque(false);

        for (CartaDTO carta : cartas) {

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
            panel.add(etiquetaCarta);
        }

        return panel;
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

    private int calcularPuntuacion(List<CartaDTO> cartas) {

        int total = 0;
        int ases = 0;

        for (CartaDTO carta : cartas) {

            if (carta == null || !carta.obtenerVisible()) {continue;}
            String valor = carta.obtenerValor();

            if (valor == null) {continue;}

            switch (valor.toUpperCase()) {
                case "AS":
                    total += 11;
                    ases++;
                    break;

                case "DOS":
                    total += 2;
                    break;

                case "TRES":
                    total += 3;
                    break;

                case "CUATRO":
                    total += 4;
                    break;

                case "CINCO":
                    total += 5;
                    break;

                case "SEIS":
                    total += 6;
                    break;

                case "SIETE":
                    total += 7;
                    break;

                case "OCHO":
                    total += 8;
                    break;

                case "NUEVE":
                    total += 9;
                    break;

                case "DIEZ":
                case "J":
                case "Q":
                case "K":
                    total += 10;
                    break;

                default:
                    break;
            }
        }

        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }

        return total;
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