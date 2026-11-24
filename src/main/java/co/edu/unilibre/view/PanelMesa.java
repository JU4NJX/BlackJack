package co.edu.unilibre.view;

import co.edu.unilibre.controller.JuegoController;
import co.edu.unilibre.dto.CartaDTO;
import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.model.enums.EstadoPartida;
import co.edu.unilibre.observer.Observador;
import co.edu.unilibre.service.JuegoService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelMesa extends JPanel implements Observador {

    private PanelJugador panelJugador;
    private PanelCrupier panelCrupier;
    private PartidaDTO partidaDTO;
    private VentanaPrincipal ventana;
    private DialogoResultado dialogoResultado;
    private JLabel etiquetaEstado;
    private JButton botonPedir;
    private JButton botonPlantarse;
    private JButton botonDoblar;
    private JButton botonDividir;
    private JComboBox<String> selectorManos;
    private int indiceManoSeleccionada;
    private boolean resultadoMostrado;
    private JuegoController juegoController;

    public PanelMesa(JuegoController juegoController, JuegoService juegoService, VentanaPrincipal ventana) {
        if (juegoController == null || juegoService == null || ventana == null) {
            throw new IllegalArgumentException("Las dependencias no pueden ser nulas.");
        }

        this.juegoController = juegoController;
        this.ventana = ventana;
        this.partidaDTO = null;
        this.dialogoResultado = new DialogoResultado(ventana);
        this.indiceManoSeleccionada = 0;
        this.resultadoMostrado = false;

        configurarPanel();
        inicializarComponentes();

        juegoService.agregarObservador(this);
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 90, 60));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void inicializarComponentes() {

        etiquetaEstado = new JLabel("Esperando inicio de partida...", SwingConstants.CENTER);
        etiquetaEstado.setFont(new Font("Arial", Font.BOLD, 18));
        etiquetaEstado.setForeground(Color.WHITE);
        panelCrupier = new PanelCrupier();
        panelJugador = new PanelJugador();

        JButton botonVolver = new JButton("Volver al menú");

        botonVolver.addActionListener(e -> ventana.volverAlMenu());
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        panelSuperior.add(etiquetaEstado, BorderLayout.CENTER);
        panelSuperior.add(botonVolver, BorderLayout.EAST);
        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCentral.setOpaque(false);
        panelCentral.add(panelCrupier);
        panelCentral.add(panelJugador);
        inicializarBotones();
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(crearPanelControles(), BorderLayout.SOUTH);
    }

    private void inicializarBotones() {
        botonPedir = new JButton("Pedir carta");
        botonPlantarse = new JButton("Plantarse");
        botonDoblar = new JButton("Doblar apuesta");
        botonDividir = new JButton("Dividir");
        selectorManos = new JComboBox<>();
        botonPedir.addActionListener(e -> ejecutarAccion(() -> juegoController.pedirCarta(indiceManoSeleccionada)));
        botonPlantarse.addActionListener(e -> ejecutarAccion(() -> juegoController.plantarse(indiceManoSeleccionada)));
        botonDoblar.addActionListener(e -> ejecutarAccion(() -> juegoController.doblarApuesta(indiceManoSeleccionada)));
        botonDividir.addActionListener(e -> ejecutarAccion(() -> juegoController.dividirMano()));
        selectorManos.addActionListener(e -> {
            if (selectorManos.getSelectedIndex() >= 0) {indiceManoSeleccionada = selectorManos.getSelectedIndex();actualizarManos();}
        });
    }

    private JPanel crearPanelControles() {

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));

        panelControles.setOpaque(false);
        panelControles.add(new JLabel("Mano:"));
        panelControles.add(selectorManos);
        panelControles.add(botonPedir);
        panelControles.add(botonPlantarse);
        panelControles.add(botonDoblar);
        panelControles.add(botonDividir);

        return panelControles;
    }

    private void ejecutarAccion(Runnable accion) {

        try {
            accion.run();
        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(this, e.getMessage(), "Acción no permitida", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actualizar(PartidaDTO partida) {

        if (partida == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {

            // Guardar el estado anterior de la mano seleccionada.
            boolean manoAnteriorFinalizada = false;

            if (this.partidaDTO != null) {
                List<Boolean> estadosAnteriores = this.partidaDTO.obtenerManosFinalizadas();
                if (indiceManoSeleccionada >= 0 && indiceManoSeleccionada < estadosAnteriores.size()) {
                    manoAnteriorFinalizada = estadosAnteriores.get(indiceManoSeleccionada);
                }
            }

            this.partidaDTO = partida;
            List<Boolean> estadosActuales = partidaDTO.obtenerManosFinalizadas();
            // Si la mano seleccionada acaba de finalizar,
            // buscar automáticamente otra mano disponible.
            if (!manoAnteriorFinalizada && indiceManoSeleccionada >= 0 && indiceManoSeleccionada < estadosActuales.size() && estadosActuales.get(indiceManoSeleccionada)) {
                seleccionarSiguienteManoActiva(estadosActuales);
            }

            actualizarMesa();
            mostrarPuntuaciones();
            actualizarControles();
            procesarResultado();
        });
    }

    private void seleccionarSiguienteManoActiva(List<Boolean> estadosManos) {
        if (estadosManos == null || estadosManos.isEmpty()) {
            return;
        }
        int cantidadManos = estadosManos.size();
        for (int i = 1; i <= cantidadManos; i++) {
            int siguienteIndice = (indiceManoSeleccionada + i) % cantidadManos;
            if (!estadosManos.get(siguienteIndice)) {
                indiceManoSeleccionada = siguienteIndice;
                return;
            }
        }
    }

    private void actualizarMesa() {

        if (partidaDTO == null) {return;}

        panelCrupier.mostrarCartas(partidaDTO.obtenerCartasCrupier());
        List<List<CartaDTO>> manos = partidaDTO.obtenerManosJugador();

        if (manos != null && !manos.isEmpty()) {

            if (indiceManoSeleccionada >= manos.size()) {
                indiceManoSeleccionada = manos.size() - 1;
            }

            actualizarSelectorManos(manos.size());

            panelJugador.mostrarManos(manos, indiceManoSeleccionada
            );

        } else {
            panelJugador.mostrarCartas(partidaDTO.obtenerCartasJugador());
        }

        if (partidaDTO.obtenerEstado() != null) {

            etiquetaEstado.setText("Estado: " + partidaDTO.obtenerEstado());
        }
        revalidate();
        repaint();
    }

    private void actualizarSelectorManos(int cantidadManos) {

        if (selectorManos.getItemCount() != cantidadManos) {
            selectorManos.removeAllItems();
            for (int i = 0; i < cantidadManos; i++) {
                selectorManos.addItem("Mano " + (i + 1));
            }
        }
        selectorManos.setSelectedIndex(indiceManoSeleccionada);
    }

    private void actualizarManos() {

        if (partidaDTO == null) {return;}

        List<List<CartaDTO>> manos = partidaDTO.obtenerManosJugador();

        if (manos != null && !manos.isEmpty()) {
            panelJugador.mostrarManos(manos, indiceManoSeleccionada);
        }
    }

    private void actualizarControles() {

        if (partidaDTO == null) {
            return;
        }

        boolean partidaActiva = partidaDTO.obtenerEstado() != null && partidaDTO.obtenerEstado() != EstadoPartida.FINALIZADA;

        List<List<CartaDTO>> manos = partidaDTO.obtenerManosJugador();
        List<Boolean> estadosManos = partidaDTO.obtenerManosFinalizadas();

        boolean manoActiva = false;

        if (estadosManos != null && indiceManoSeleccionada >= 0 && indiceManoSeleccionada < estadosManos.size()) {manoActiva = !estadosManos.get(indiceManoSeleccionada);}

        // Acciones disponibles únicamente para una mano activa.
        botonPedir.setEnabled(partidaActiva && manoActiva);
        botonPlantarse.setEnabled(partidaActiva && manoActiva);
        botonDoblar.setEnabled(partidaActiva && manoActiva);
        // Dividir únicamente cuando el servicio lo permite.
        botonDividir.setEnabled(partidaActiva && partidaDTO.obtenerPuedeDividir());
        // Seleccionar entre varias manos.
        selectorManos.setEnabled(partidaActiva && manos != null && manos.size() > 1);
    }

    private void procesarResultado() {

        if (partidaDTO == null) {return;}
        if (partidaDTO.obtenerEstado() != EstadoPartida.FINALIZADA) {
            resultadoMostrado = false;
            return;
        }
        if (resultadoMostrado || partidaDTO.obtenerResultado() == null) {return;}

        resultadoMostrado = true;

        dialogoResultado.mostrarPuntos(partidaDTO.obtenerPuntosResultado());
        dialogoResultado.mostrarResultado(partidaDTO.obtenerResultado());
    }

    public void mostrarCarta(CartaDTO carta) {
        if (carta == null || partidaDTO == null) {return;}
        List<CartaDTO> cartas = panelJugador.obtenerCartas();
        cartas.add(carta);
        panelJugador.mostrarCartas(cartas);
    }

    public void mostrarPuntuaciones() {
        if (partidaDTO == null) {return;}
        panelJugador.actualizarPuntuacion(partidaDTO.obtenerPuntuacionJugador());
        panelCrupier.actualizarPuntuacion(partidaDTO.obtenerPuntuacionCrupier());
    }

    public PanelJugador obtenerPanelJugador() {
        return panelJugador;
    }
    public PanelCrupier obtenerPanelCrupier() {
        return panelCrupier;
    }
    public PartidaDTO obtenerPartidaDTO() {
        return partidaDTO;
    }
    public void modificarPartidaDTO(PartidaDTO partidaDTO) {
        this.partidaDTO = partidaDTO;
        actualizarMesa();
        mostrarPuntuaciones();
        actualizarControles();
    }
}