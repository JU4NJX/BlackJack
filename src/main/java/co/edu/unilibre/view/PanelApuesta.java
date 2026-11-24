package co.edu.unilibre.view;

import co.edu.unilibre.controller.ApuestaController;
import co.edu.unilibre.controller.JuegoController;
import co.edu.unilibre.controller.UsuarioController;
import co.edu.unilibre.dto.UsuarioDTO;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class PanelApuesta extends JPanel {

    private static final double APUESTA_MINIMA = 100.0;
    private static final double[] FICHAS = {100.0, 200.0, 500.0, 1000.0};
    private static final Color COLOR_FONDO = new Color(20, 75, 55);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_BOTONES = new Color(35, 105, 75);
    private ApuestaController apuestaController;
    private UsuarioController usuarioController;
    private JuegoController juegoController;
    private VentanaPrincipal ventana;
    private JLabel etiquetaSaldo;
    private JLabel etiquetaApuesta;
    private JButton botonApostar;
    private JButton botonLimpiar;
    private JButton botonVolver;
    private double saldoDisponible;
    private double apuestaSeleccionada;

    public PanelApuesta(ApuestaController apuestaController, UsuarioController usuarioController, JuegoController juegoController, VentanaPrincipal ventana) {
        if (apuestaController == null || usuarioController == null || juegoController == null || ventana == null) {
            throw new IllegalArgumentException("Las dependencias no pueden ser nulas.");
        }

        this.apuestaController = apuestaController;
        this.usuarioController = usuarioController;
        this.juegoController = juegoController;
        this.ventana = ventana;
        this.saldoDisponible = 0;
        this.apuestaSeleccionada = 0;

        inicializarComponentes();
        cargarSaldo();
    }

    private void inicializarComponentes() {

        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        JLabel titulo = new JLabel("Selecciona tu apuesta", SwingConstants.CENTER);

        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(COLOR_TEXTO);

        etiquetaSaldo = new JLabel("Saldo disponible: $0", SwingConstants.CENTER);
        etiquetaSaldo.setFont(new Font("Arial", Font.PLAIN, 20));
        etiquetaSaldo.setForeground(COLOR_TEXTO);
        etiquetaApuesta = new JLabel("Apuesta seleccionada: $0", SwingConstants.CENTER);
        etiquetaApuesta.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaApuesta.setForeground(COLOR_TEXTO);

        JPanel panelSuperior = new JPanel(new GridLayout(3, 1, 10, 10));

        panelSuperior.setBackground(COLOR_FONDO);

        panelSuperior.add(titulo);
        panelSuperior.add(etiquetaSaldo);
        panelSuperior.add(etiquetaApuesta);

        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelBotonesApuesta = new JPanel(new GridLayout(1, 4, 20, 20));

        panelBotonesApuesta.setBackground(COLOR_FONDO);

        Color[] colores = {new Color(23, 105, 170), new Color(22, 131, 74), new Color(198, 40, 40), new Color(23, 23, 23)};

        for (int i = 0; i < FICHAS.length; i++) {
            double ficha = FICHAS[i];
            JButton boton = new JButton("$" + formato(ficha));
            estilizarBotonCircular(boton, colores[i], i == 3);
            boton.addActionListener(e -> agregarFicha(ficha));
            panelBotonesApuesta.add(boton);
        }

        add(panelBotonesApuesta, BorderLayout.CENTER);

        botonLimpiar = new JButton("Limpiar apuesta");
        botonApostar = new JButton("Iniciar partida");
        botonVolver = new JButton("Volver");

        estilizarBotonInferior(botonLimpiar);
        estilizarBotonInferior(botonApostar);
        estilizarBotonInferior(botonVolver);

        botonLimpiar.addActionListener(e -> limpiarApuesta());
        botonApostar.addActionListener(e -> iniciarPartida());
        botonVolver.addActionListener(e -> ventana.volverAlMenu());

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        panelInferior.setBackground(COLOR_FONDO);

        panelInferior.add(botonLimpiar);
        panelInferior.add(botonApostar);
        panelInferior.add(botonVolver);

        add(panelInferior, BorderLayout.SOUTH);

        actualizarEstadoBotones();
    }

    private void estilizarBotonCircular(JButton boton, Color color, boolean dorado) {

        boton.setFont(new Font("Arial", Font.BOLD, 18));
        boton.setForeground(dorado ? new Color(212, 175, 55) : Color.WHITE);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setOpaque(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(130, 130));
        boton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {

                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(b.getWidth(), b.getHeight());
                int x = (b.getWidth() - size) / 2;
                int y = (b.getHeight() - size) / 2;

                g2.setColor(color);
                g2.fillOval(x, y, size, size);

                g2.setStroke(new BasicStroke(4));
                g2.setColor(dorado ? new Color(212, 175, 55) : Color.WHITE);
                g2.drawOval(x + 3, y + 3, size - 6, size - 6);
                g2.dispose();

                super.paint(g, c);
            }
        });
    }

    private void estilizarBotonInferior(JButton boton) {

        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_BOTONES);

        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void cargarSaldo() {

        try {
            UsuarioDTO usuario = usuarioController.obtenerUsuario();
            if (usuario == null) {
                throw new IllegalStateException("No fue posible obtener el usuario.");
            }

            saldoDisponible = usuario.obtenerSaldo();

            if (!Double.isFinite(saldoDisponible) || saldoDisponible < 0) {
                throw new IllegalStateException("El saldo del usuario no es válido.");
            }

            apuestaSeleccionada = 0;

            etiquetaSaldo.setText("Saldo disponible: $" + formato(saldoDisponible));

            actualizarApuesta();

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al cargar saldo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarFicha(double valor) {

        double nuevaApuesta = apuestaSeleccionada + valor;

        if (nuevaApuesta > saldoDisponible) {
            JOptionPane.showMessageDialog(this, "No tienes saldo suficiente para agregar esta ficha.", "Saldo insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        apuestaSeleccionada = nuevaApuesta;
        actualizarApuesta();
    }

    private void limpiarApuesta() {
        apuestaSeleccionada = 0;
        actualizarApuesta();
    }

    private void actualizarApuesta() {
        etiquetaApuesta.setText("Apuesta seleccionada: $" + formato(apuestaSeleccionada));
        actualizarEstadoBotones();
    }

    private void actualizarEstadoBotones() {
        botonApostar.setEnabled(apuestaSeleccionada >= APUESTA_MINIMA && apuestaSeleccionada <= saldoDisponible);
        botonLimpiar.setEnabled(apuestaSeleccionada > 0);
    }

    private void iniciarPartida() {
        if (apuestaSeleccionada < APUESTA_MINIMA || apuestaSeleccionada > saldoDisponible) {
            JOptionPane.showMessageDialog(this, "Selecciona una apuesta válida.", "Apuesta inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (!apuestaController.validarSaldo(apuestaSeleccionada)) {
                throw new IllegalArgumentException("La apuesta no es válida o el saldo es insuficiente.");
            }

            juegoController.iniciarPartida(apuestaSeleccionada);
            apuestaSeleccionada = 0;
            ventana.abrirMesa();

        } catch (IllegalArgumentException | IllegalStateException e) {

            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al iniciar partida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formato(double valor) {

        return String.format(Locale.US, "%,.0f", valor);
    }
}