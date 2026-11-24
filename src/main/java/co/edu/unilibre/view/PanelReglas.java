
package co.edu.unilibre.view;

import javax.swing.*;
import java.awt.*;

public class PanelReglas extends JPanel {

    private JTextPane areaReglas;
    private JScrollPane desplazamiento;
    private VentanaPrincipal ventana;

    public PanelReglas(VentanaPrincipal ventana) {

        if (ventana == null) {
            throw new IllegalArgumentException(
                    "La ventana principal no puede ser nula."
            );
        }

        this.ventana = ventana;

        configurarPanel();
        inicializarComponentes();
        mostrarReglas();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 90, 60));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
    }

    private void inicializarComponentes() {

        JLabel titulo = new JLabel(
                "REGLAS DE BLACKJACK ♠",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 27));
        titulo.setForeground(Color.WHITE);

        areaReglas = new JTextPane();
        areaReglas.setContentType("text/html");
        areaReglas.setEditable(false);
        areaReglas.setBackground(new Color(245, 245, 235));
        areaReglas.setMargin(new Insets(20, 25, 20, 25));

        desplazamiento = new JScrollPane(areaReglas);
        desplazamiento.setBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1)
        );

        // Botón para regresar al menú principal
        JButton botonVolver = new JButton("Volver al menú principal");

        botonVolver.setFont(new Font("Arial", Font.BOLD, 14));
        botonVolver.setFocusPainted(false);
        botonVolver.addActionListener(e -> ventana.volverAlMenu());

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setOpaque(false);
        panelInferior.add(botonVolver);

        add(titulo, BorderLayout.NORTH);
        add(desplazamiento, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    public void mostrarReglas() {
        String contenido = """
                <html>
                <body style='font-family: Arial; font-size: 13px; color: #222222;'>

                <h1 style='color: #145A38; text-align: center;'>
                GUÍA DEL JUEGO
                </h1>

                <h2 style='color: #145A38;'>1. Objetivo</h2>
                <p>
                Conseguir una puntuación superior a la del crupier
                sin superar los 21 puntos.
                </p>

                <hr>

                <h2 style='color: #145A38;'>2. Valor de las cartas</h2>

                <table border='1' cellpadding='8' cellspacing='0'
                       width='100%' style='border-collapse: collapse;'>
                    <tr bgcolor='#D5E8D4'>
                        <th>Carta</th>
                        <th>Valor</th>
                    </tr>
                    <tr>
                        <td>2 al 10</td>
                        <td>Valor numérico</td>
                    </tr>
                    <tr>
                        <td>J, Q, K</td>
                        <td>10 puntos</td>
                    </tr>
                    <tr>
                        <td>As</td>
                        <td>1 u 11 puntos</td>
                    </tr>
                </table>

                <hr>

                <h2 style='color: #145A38;'>3. Desarrollo de la partida</h2>

                <ul>
                    <li>Cada participante recibe dos cartas.</li>
                    <li>El jugador puede pedir cartas o plantarse.</li>
                    <li>Si supera 21, pierde automáticamente.</li>
                    <li>El crupier pide con menos de 17 y se planta con 17 o más.</li>
                </ul>

                <hr>

                <h2 style='color: #145A38;'>4. Apuestas y pagos</h2>

                <table border='1' cellpadding='8' cellspacing='0'
                       width='100%' style='border-collapse: collapse;'>
                    <tr bgcolor='#D5E8D4'>
                        <th>Resultado</th>
                        <th>Pago total</th>
                    </tr>
                    <tr>
                        <td>Victoria normal</td>
                        <td>2:1</td>
                    </tr>
                    <tr>
                        <td>Blackjack natural</td>
                        <td>3:2 de ganancia + apuesta</td>
                    </tr>
                    <tr>
                        <td>Empate</td>
                        <td>Se devuelve la apuesta</td>
                    </tr>
                    <tr>
                        <td>Derrota</td>
                        <td>Se pierde la apuesta</td>
                    </tr>
                </table>

                <hr>

                <h2 style='color: #145A38;'>5. Acciones especiales</h2>

                <ul>
                    <li><b>Double Down:</b> duplicar la apuesta y recibir una sola carta.</li>
                    <li><b>Split:</b> dividir dos cartas iniciales del mismo valor en dos manos.</li>
                    <li><b>Split de Ases:</b> una carta adicional por cada mano.</li>
                    <li>Se permite doblar después de dividir.</li>
                </ul>

                <hr>

                <h2 style='color: #145A38;'>6. Saldo</h2>

                <ul>
                    <li>Saldo inicial: <b>2.000 puntos.</b></li>
                    <li>Apuestas permitidas: <b>50, 100, 500, 1.000 o todo el saldo.</b></li>
                    <li>Si el saldo llega a cero después de perder, se recuperan <b>500 puntos.</b></li>
                </ul>

                <br>

                <p style='text-align: center; color: #145A38;'>
                <b>¡Buena suerte en la mesa!</b>
                </p>

                </body>
                </html>
                """;

        areaReglas.setText(contenido);
        areaReglas.setCaretPosition(0);
    }

    public void mostrarPagos() {
        mostrarReglas();
        areaReglas.setCaretPosition(0);
    }

    public void mostrarPuntuaciones() {
        mostrarReglas();
        areaReglas.setCaretPosition(0);
    }
}
