package co.edu.unilibre.view;

import co.edu.unilibre.controller.ApuestaController;
import co.edu.unilibre.controller.JuegoController;
import co.edu.unilibre.controller.UsuarioController;
import co.edu.unilibre.dto.UsuarioDTO;

import javax.swing.*;
import java.awt.*;

public class PanelApuesta extends JPanel {

    private double apuestaSeleccionada;
    private double saldoDisponible;

    private ApuestaController apuestaController;
    private UsuarioController usuarioController;
    private JuegoController juegoController;
    private VentanaPrincipal ventana;

    private JLabel etiquetaSaldo;
    private JLabel etiquetaApuesta;
    private JTextField campoApuesta;
    private JButton botonApostar;
    private JButton botonTodo;
    private JButton botonVolver;

    public PanelApuesta(ApuestaController apuestaController, UsuarioController usuarioController, JuegoController juegoController, VentanaPrincipal ventana) {

        if (apuestaController == null || usuarioController == null || juegoController == null || ventana == null) {
            throw new IllegalArgumentException("Las dependencias no pueden ser nulas.");
        }

        this.apuestaController = apuestaController;
        this.usuarioController = usuarioController;
        this.juegoController = juegoController;
        this.ventana = ventana;

        this.apuestaSeleccionada = 0;
        this.saldoDisponible = 0;

        configurarPanel();
        inicializarComponentes();
        configurarEventos();
        cargarSaldo();
    }

    private void configurarPanel() {

        setLayout(new GridBagLayout());
        setBackground(new Color(20, 90, 60));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void inicializarComponentes() {

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(new Color(20, 90, 60));

        JLabel titulo = new JLabel("REALIZAR APUESTA");
        titulo.setFont(new Font("Arial", Font.BOLD, 25));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        etiquetaSaldo = new JLabel("Saldo disponible: 0");
        etiquetaSaldo.setFont(new Font("Arial", Font.BOLD, 18));
        etiquetaSaldo.setForeground(Color.WHITE);
        etiquetaSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel etiquetaCampo = new JLabel("Ingrese el valor de su apuesta:");
        etiquetaCampo.setForeground(Color.WHITE);
        etiquetaCampo.setAlignmentX(Component.CENTER_ALIGNMENT);

        campoApuesta = new JTextField();
        campoApuesta.setMaximumSize(new Dimension(250, 35));
        campoApuesta.setHorizontalAlignment(JTextField.CENTER);
        campoApuesta.setFont(new Font("Arial", Font.PLAIN, 17));

        etiquetaApuesta = new JLabel("Apuesta seleccionada: 0");
        etiquetaApuesta.setForeground(Color.WHITE);
        etiquetaApuesta.setAlignmentX(Component.CENTER_ALIGNMENT);

        botonTodo = crearBoton("Apostar todo");
        botonApostar = crearBoton("Iniciar partida");
        botonVolver = crearBoton("Volver al menú");

        panelCentral.add(titulo);
        panelCentral.add(Box.createVerticalStrut(30));
        panelCentral.add(etiquetaSaldo);
        panelCentral.add(Box.createVerticalStrut(25));
        panelCentral.add(etiquetaCampo);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(campoApuesta);
        panelCentral.add(Box.createVerticalStrut(15));
        panelCentral.add(etiquetaApuesta);
        panelCentral.add(Box.createVerticalStrut(25));
        panelCentral.add(botonTodo);
        panelCentral.add(Box.createVerticalStrut(15));
        panelCentral.add(botonApostar);
        panelCentral.add(Box.createVerticalStrut(15));
        panelCentral.add(botonVolver);

        add(panelCentral);
    }

    private JButton crearBoton(String texto) {

        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setPreferredSize(new Dimension(220, 40));
        boton.setMaximumSize(new Dimension(220, 40));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setFocusPainted(false);

        return boton;
    }

    private void configurarEventos() {

        campoApuesta.addActionListener(e -> seleccionarApuesta());

        botonTodo.addActionListener(e -> apostarTodo());

        botonApostar.addActionListener(e -> iniciarPartida());

        botonVolver.addActionListener(e -> ventana.volverAlMenu());
    }

    public void cargarSaldo() {

        try {
            UsuarioDTO usuario = usuarioController.obtenerUsuario();
            if (usuario == null) {
                mostrarError("No se encontró un usuario.");
                return;
            }
            mostrarSaldo(
                    usuarioController.consultarSaldo(usuario.obtenerId())
            );

        } catch (Exception e) {
            mostrarError(
                    "No fue posible consultar el saldo: " + e.getMessage()
            );
        }
    }


    public void seleccionarApuesta(double monto) {

        if (monto <= 0 || monto > saldoDisponible) {
            mostrarError(
                    "La apuesta debe ser mayor que cero y no superar tu saldo."
            );
            return;
        }

        apuestaSeleccionada = monto;
        campoApuesta.setText(String.valueOf(monto));
        etiquetaApuesta.setText("Apuesta seleccionada: " + monto);
    }

    private void seleccionarApuesta() {

        try {

            double monto = Double.parseDouble(campoApuesta.getText().trim());
            seleccionarApuesta(monto);

        } catch (NumberFormatException e) {

            mostrarError("Ingrese un valor numérico válido.");
        }
    }

    public void apostarTodo() {

        if (saldoDisponible <= 0) {
            mostrarError("No tienes saldo disponible para apostar.");
            return;
        }

        seleccionarApuesta(saldoDisponible);
    }

    public void mostrarSaldo(double saldo) {

        if (saldo < 0) {
            throw new IllegalArgumentException(
                    "El saldo no puede ser negativo."
            );
        }

        saldoDisponible = saldo;
        etiquetaSaldo.setText("Saldo disponible: " + saldo);
        habilitarAcciones();
    }

    public void habilitarAcciones() {

        boolean habilitado = saldoDisponible > 0;

        campoApuesta.setEnabled(habilitado);
        botonTodo.setEnabled(habilitado);
        botonApostar.setEnabled(habilitado);
    }

    private void iniciarPartida() {

        seleccionarApuesta();

        if (apuestaSeleccionada <= 0
                || apuestaSeleccionada > saldoDisponible) {
            return;
        }

        try {

            juegoController.iniciarPartida(apuestaSeleccionada);

            ventana.abrirMesa();

        } catch (Exception e) {

            mostrarError(
                    "No fue posible iniciar la partida: " + e.getMessage()
            );
        }
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Apuestas",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public double obtenerApuestaSeleccionada() {
        return apuestaSeleccionada;
    }

    public void modificarApuestaSeleccionada(double monto) {
        seleccionarApuesta(monto);
    }

    public double obtenerSaldoDisponible() {
        return saldoDisponible;
    }

    public void modificarSaldoDisponible(double saldo) {
        mostrarSaldo(saldo);
    }
}
