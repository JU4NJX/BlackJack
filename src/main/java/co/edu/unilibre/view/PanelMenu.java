package co.edu.unilibre.view;

import javax.swing.*;
import java.awt.*;

public class PanelMenu extends JPanel {

    private VentanaPrincipal ventana;

    private JButton botonJugar;
    private JButton botonHistorial;
    private JButton botonReglas;
    private JButton botonSalir;

    public PanelMenu(VentanaPrincipal ventana) {

        if (ventana == null) {
            throw new IllegalArgumentException("La ventana principal no puede ser nula.");
        }

        this.ventana = ventana;

        configurarPanel();
        inicializarComponentes();
        configurarEventos();
    }

    private void configurarPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 75, 55));
    }

    private void inicializarComponentes() {

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(new Color(20, 75, 55));

        JLabel titulo = new JLabel("BLACKJACK");
        titulo.setFont(new Font("Arial", Font.BOLD, 42));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("¡Bienvenido a la mesa!");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setForeground(Color.WHITE);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        botonJugar = crearBoton("Jugar");
        botonHistorial = crearBoton("Historial");
        botonReglas = crearBoton("Reglas");
        botonSalir = crearBoton("Salir");

        panelCentral.add(titulo);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(subtitulo);
        panelCentral.add(Box.createVerticalStrut(40));
        panelCentral.add(botonJugar);
        panelCentral.add(Box.createVerticalStrut(15));
        panelCentral.add(botonHistorial);
        panelCentral.add(Box.createVerticalStrut(15));
        panelCentral.add(botonReglas);
        panelCentral.add(Box.createVerticalStrut(15));
        panelCentral.add(botonSalir);

        add(panelCentral);
    }

    private JButton crearBoton(String texto) {

        JButton boton = new JButton(texto);

        boton.setFont(new Font("Arial", Font.BOLD, 18));
        boton.setPreferredSize(new Dimension(220, 45));
        boton.setMaximumSize(new Dimension(220, 45));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setFocusPainted(false);

        return boton;
    }

    private void configurarEventos() {

        botonJugar.addActionListener(e -> abrirJuego());

        botonHistorial.addActionListener(e -> abrirHistorial());

        botonReglas.addActionListener(e -> abrirReglas());

        botonSalir.addActionListener(e -> salir());
    }

    public void mostrarMenu() {
        ventana.mostrarPanel(this);
    }

    public void abrirJuego() {
        // La navegación al juego se conectará desde VentanaPrincipal.
        ventana.abrirJuego();
    }

    public void abrirHistorial() {
        ventana.abrirHistorial();
    }

    public void abrirReglas() {
        ventana.abrirReglas();
    }

    private void salir() {

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas salir?",
                "Salir de Blackjack",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public VentanaPrincipal obtenerVentana() {
        return ventana;
    }

    public void modificarVentana(VentanaPrincipal ventana) {

        if (ventana == null) {
            throw new IllegalArgumentException(
                    "La ventana principal no puede ser nula."
            );
        }

        this.ventana = ventana;
    }
}
