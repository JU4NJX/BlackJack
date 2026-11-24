package co.edu.unilibre.view;

import co.edu.unilibre.controller.ApuestaController;
import co.edu.unilibre.controller.HistorialController;
import co.edu.unilibre.controller.JuegoController;
import co.edu.unilibre.controller.UsuarioController;
import co.edu.unilibre.service.JuegoService;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private JPanel panelActual;

    private JuegoController juegoController;
    private ApuestaController apuestaController;
    private HistorialController historialController;
    private UsuarioController usuarioController;
    private JuegoService juegoService;

    private PanelMenu panelMenu;
    private PanelMesa panelMesa;
    private PanelApuesta panelApuesta;
    private PanelHistorial panelHistorial;
    private PanelReglas panelReglas;

    public VentanaPrincipal(JuegoController juegoController, ApuestaController apuestaController, HistorialController historialController, UsuarioController usuarioController, JuegoService juegoService) {
        if (juegoController == null || apuestaController == null || historialController == null || usuarioController == null || juegoService == null) {
            throw new IllegalArgumentException("Los controladores y servicios no pueden ser nulos.");
        }

        this.juegoController = juegoController;
        this.apuestaController = apuestaController;
        this.historialController = historialController;
        this.usuarioController = usuarioController;
        this.juegoService = juegoService;

        configurarVentana();
        inicializarPaneles();
    }

    private void configurarVentana() {
        setTitle("Blackjack");
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarPaneles() {
        panelMesa = new PanelMesa(juegoController, juegoService, this);
        panelApuesta = new PanelApuesta(apuestaController, usuarioController, juegoController, this);
        panelHistorial = new PanelHistorial(historialController, usuarioController, this);
        panelReglas = new PanelReglas(this);
        panelMenu = new PanelMenu(this);

        mostrarPanel(panelMenu);
    }

    public void mostrarPanel(JPanel panel) {

        if (panel == null) {
            throw new IllegalArgumentException("El panel no puede ser nulo.");
        }

        if (panelActual != null) {remove(panelActual);}

        panelActual = panel;
        add(panelActual, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    public void iniciarAplicacion() {setVisible(true);}

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Blackjack", JOptionPane.INFORMATION_MESSAGE);
    }

    public void abrirJuego() {
        panelApuesta.cargarSaldo();
        mostrarPanel(panelApuesta);
    }

    public void abrirHistorial() {
        panelHistorial.actualizarHistorial();
        mostrarPanel(panelHistorial);
    }

    public void abrirReglas() {
        panelReglas.mostrarReglas();
        mostrarPanel(panelReglas);
    }

    public void abrirMesa() {
        mostrarPanel(panelMesa);
    }

    public void volverAlMenu() {mostrarPanel(panelMenu);}
    public JPanel obtenerPanelActual() {return panelActual;}

    public void modificarPanelActual(JPanel panelActual) {
        mostrarPanel(panelActual);
    }

    public PanelMesa obtenerPanelMesa() {return panelMesa;}
    public PanelApuesta obtenerPanelApuestas() {
        return panelApuesta;
    }
    public PanelHistorial obtenerPanelHistorial() {return panelHistorial;}
    public PanelReglas obtenerPanelReglas() {return panelReglas;}
    public PanelMenu obtenerPanelMenu() {return panelMenu;}
}
