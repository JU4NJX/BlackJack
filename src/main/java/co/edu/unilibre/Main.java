
package co.edu.unilibre;

import co.edu.unilibre.controller.JuegoController;
import co.edu.unilibre.controller.ApuestaController;
import co.edu.unilibre.controller.HistorialController;
import co.edu.unilibre.controller.UsuarioController;

import co.edu.unilibre.factory.CartaFactoryEstandar;
import co.edu.unilibre.factory.MazoFactory;

import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.service.JuegoService;
import co.edu.unilibre.service.ApuestaService;
import co.edu.unilibre.service.UsuarioService;
import co.edu.unilibre.service.HistorialService;

import co.edu.unilibre.strategy.EstrategiaClasica;

import co.edu.unilibre.view.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                // 1. Crear fábricas
                CartaFactoryEstandar cartaFactory = new CartaFactoryEstandar();

                MazoFactory mazoFactory = new MazoFactory(cartaFactory);

                // 2. Crear servicios
                UsuarioService usuarioService = new UsuarioService();
                ApuestaService apuestaService = new ApuestaService();
                Usuario usuarioActual = usuarioService.obtenerUsuarioModelo();
                apuestaService.aplicarRecuperacion(usuarioActual);
                HistorialService historialService = new HistorialService();

                JuegoService juegoService = new JuegoService(new EstrategiaClasica(), mazoFactory, apuestaService);

                // 3. Crear controladores
                JuegoController juegoController = new JuegoController(juegoService);

                ApuestaController apuestaController = new ApuestaController(apuestaService, usuarioService);

                HistorialController historialController = new HistorialController(historialService);

                UsuarioController usuarioController = new UsuarioController(usuarioService);

                // 4. Iniciar interfaz gráfica
                VentanaPrincipal ventana = new VentanaPrincipal(juegoController, apuestaController, historialController, usuarioController, juegoService);

                ventana.iniciarAplicacion();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al iniciar Blackjack:\n" + e.getMessage(), "Error de aplicación", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}

