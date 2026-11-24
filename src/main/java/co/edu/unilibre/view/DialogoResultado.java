package co.edu.unilibre.view;

import co.edu.unilibre.model.enums.Resultados;

import javax.swing.*;
import java.awt.*;

public class DialogoResultado extends JDialog {

    private Resultados resultado;
    private double puntos;

    private JLabel etiquetaResultado;
    private JLabel etiquetaPuntos;

    public DialogoResultado(JFrame ventanaPrincipal) {

        super(ventanaPrincipal, "Resultado de la partida", true);

        this.resultado = null;
        this.puntos = 0;

        configurarDialogo();
        inicializarComponentes();
    }

    private void configurarDialogo() {
        setSize(400, 250);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
    }

    private void inicializarComponentes() {

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(new Color(20, 90, 60));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        etiquetaResultado = new JLabel("Resultado de la partida", SwingConstants.CENTER);
        etiquetaResultado.setFont(new Font("Arial", Font.BOLD, 25));
        etiquetaResultado.setForeground(Color.WHITE);
        etiquetaResultado.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiquetaPuntos = new JLabel("Puntos: 0", SwingConstants.CENTER);
        etiquetaPuntos.setFont(new Font("Arial", Font.BOLD, 20));
        etiquetaPuntos.setForeground(Color.WHITE);
        etiquetaPuntos.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonAceptar = new JButton("Aceptar");

        botonAceptar.setFont(new Font("Arial", Font.BOLD, 15));
        botonAceptar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonAceptar.addActionListener(e -> dispose());

        panelCentral.add(etiquetaResultado);
        panelCentral.add(Box.createVerticalStrut(20));
        panelCentral.add(etiquetaPuntos);
        panelCentral.add(Box.createVerticalStrut(25));
        panelCentral.add(botonAceptar);

        add(panelCentral, BorderLayout.CENTER);
    }

    public void mostrarResultado(Resultados resultado) {

        if (resultado == null) {
            throw new IllegalArgumentException("El resultado no puede ser nulo.");
        }
        this.resultado = resultado;

        String mensaje;

        switch (resultado) {

            case GANADA:
                mensaje = "¡VICTORIA!";
                break;

            case PERDIDA:
                mensaje = "DERROTA";
                break;

            case EMPATE:
                mensaje = "¡EMPATE!";
                break;

            case BLACKJACK:
                mensaje = "¡BLACKJACK!";
                break;

            default:
                mensaje = "PARTIDA FINALIZADA";
                break;
        }

        etiquetaResultado.setText(mensaje);

        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    public void mostrarPuntos(double puntos) {

        this.puntos = puntos;
        etiquetaPuntos.setText("Puntos: " + puntos);
    }

    public Resultados obtenerResultado() {
        return resultado;
    }

    public void modificarResultado(Resultados resultado) {
        if (resultado == null) {
            throw new IllegalArgumentException("El resultado no puede ser nulo.");
        }
        this.resultado = resultado;
    }

    public double obtenerPuntos() {
        return puntos;
    }

    public void modificarPuntos(double puntos) {
        this.puntos = puntos;
        etiquetaPuntos.setText("Puntos: " + puntos);
    }
}