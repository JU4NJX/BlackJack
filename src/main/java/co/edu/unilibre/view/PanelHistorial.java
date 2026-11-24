package co.edu.unilibre.view;

import co.edu.unilibre.controller.HistorialController;
import co.edu.unilibre.controller.UsuarioController;
import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.dto.UsuarioDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelHistorial extends JPanel {

    private List<HistorialDTO> registros;

    private HistorialController historialController;
    private UsuarioController usuarioController;
    private VentanaPrincipal ventana;

    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    public PanelHistorial(HistorialController historialController, UsuarioController usuarioController, VentanaPrincipal ventana) {

        if (historialController == null || usuarioController == null || ventana == null) {
            throw new IllegalArgumentException("Las dependencias no pueden ser nulas.");
        }

        this.historialController = historialController;
        this.usuarioController = usuarioController;
        this.ventana = ventana;
        this.registros = new ArrayList<>();

        configurarPanel();
        inicializarComponentes();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(20, 90, 60));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void inicializarComponentes() {

        JLabel titulo = new JLabel("HISTORIAL DE PARTIDAS", SwingConstants.CENTER);

        titulo.setFont(new Font("Arial", Font.BOLD, 25));
        titulo.setForeground(Color.WHITE);

        String[] columnas = {
                "ID",
                "Fecha",
                "Resultado",
                "Apuesta",
                "Puntos ganados",
                "Puntos perdidos",
                "Saldo final"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(28);
        tablaHistorial.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaHistorial.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane desplazamiento = new JScrollPane(tablaHistorial);

        JButton botonActualizar = new JButton("Actualizar historial");
        botonActualizar.addActionListener(e -> actualizarHistorial());

        JButton botonVolver = new JButton("Volver al menú principal");
        botonVolver.addActionListener(e -> ventana.volverAlMenu());

        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 5)
        );

        panelBotones.setOpaque(false);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonVolver);

        add(titulo, BorderLayout.NORTH);
        add(desplazamiento, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void mostrarHistorial(List<HistorialDTO> registros) {

        if (registros == null) {
            throw new IllegalArgumentException("La lista del historial no puede ser nula.");
        }

        this.registros = new ArrayList<>(registros);

        modeloTabla.setRowCount(0);

        for (HistorialDTO registro : this.registros) {
            if (registro == null) {
                continue;
            }

            Object[] fila = {
                    registro.obtenerId(),
                    registro.obtenerFecha(),
                    registro.obtenerResultado(),
                    registro.obtenerApuesta(),
                    registro.obtenerPuntosGanados(),
                    registro.obtenerPuntosPerdidos(),
                    registro.obtenerSaldoFinal()
            };

            modeloTabla.addRow(fila);
        }
    }

    public void actualizarHistorial() {

        try {

            UsuarioDTO usuario = usuarioController.obtenerUsuario();

            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "No se encontró un usuario.", "Historial", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<HistorialDTO> historial = historialController.cargarHistorial(usuario.obtenerId());

            mostrarHistorial(historial);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No fue posible cargar el historial: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<HistorialDTO> obtenerRegistros() {
        return new ArrayList<>(registros);
    }

    public void modificarRegistros(List<HistorialDTO> registros) {
        mostrarHistorial(registros);
    }
}
