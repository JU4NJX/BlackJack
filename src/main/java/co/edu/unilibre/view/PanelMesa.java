package co.edu.unilibre.view;

import co.edu.unilibre.controller.JuegoController;
import co.edu.unilibre.dto.CartaDTO;
import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.model.enums.EstadoPartida;
import co.edu.unilibre.observer.Observador;
import co.edu.unilibre.service.JuegoService;

import javax.swing.*;
import java.awt.*;

public class PanelMesa extends JPanel implements Observador {

    private PanelJugador panelJugador;
    private PanelCrupier panelCrupier;
    private PartidaDTO partidaDTO;
    private VentanaPrincipal ventana;
    private DialogoResultado dialogoResultado;
    private boolean resultadoMostrado;

    private JLabel etiquetaEstado;

    public PanelMesa(JuegoController juegoController, JuegoService juegoService, VentanaPrincipal ventana) {
        if (juegoController == null || juegoService == null || ventana == null) {
            throw new IllegalArgumentException("Las dependencias no pueden ser nulas.");
        }

        this.ventana = ventana;
        this.partidaDTO = null;
        this.dialogoResultado = new DialogoResultado(ventana);
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

        botonVolver.addActionListener(
                e -> ventana.volverAlMenu()
        );

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        panelSuperior.add(etiquetaEstado, BorderLayout.CENTER);
        panelSuperior.add(botonVolver, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelCrupier, BorderLayout.CENTER);
        add(panelJugador, BorderLayout.SOUTH);
    }

    @Override
    public void actualizar(PartidaDTO partida) {

        if (partida == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {

            this.partidaDTO = partida;

            actualizarMesa();
            mostrarPuntuaciones();
            procesarResultado();
        });
    }

    private void procesarResultado() {

        if (partidaDTO == null) {
            return;
        }

        if (partidaDTO.obtenerEstado() != EstadoPartida.FINALIZADA) {

            resultadoMostrado = false;
            return;
        }

        if (resultadoMostrado || partidaDTO.obtenerResultado() == null) {
            return;
        }

        resultadoMostrado = true;

        dialogoResultado.mostrarPuntos(
                partidaDTO.obtenerPuntosResultado()
        );

        dialogoResultado.mostrarResultado(
                partidaDTO.obtenerResultado()
        );
    }

    public void actualizarMesa() {

        if (partidaDTO == null) {
            return;
        }

        panelJugador.mostrarCartas(partidaDTO.obtenerCartasJugador());

        panelCrupier.mostrarCartas(partidaDTO.obtenerCartasCrupier());

        if (partidaDTO.obtenerEstado() != null) {
            etiquetaEstado.setText("Estado: " + partidaDTO.obtenerEstado());
        }
    }

    public void mostrarCarta(CartaDTO carta) {

        if (carta == null || partidaDTO == null) {
            return;
        }

        java.util.List<CartaDTO> cartas = panelJugador.obtenerCartas();

        cartas.add(carta);

        panelJugador.mostrarCartas(cartas);
    }

    public void mostrarPuntuaciones() {

        if (partidaDTO == null) {
            return;
        }

        panelJugador.actualizarPuntuacion(partidaDTO.obtenerPuntuacionJugador()
        );

        panelCrupier.actualizarPuntuacion(partidaDTO.obtenerPuntuacionCrupier()
        );
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
    }
}