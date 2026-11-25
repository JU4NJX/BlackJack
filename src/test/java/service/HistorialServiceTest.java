package service;

import co.edu.unilibre.dao.PartidaDAO;
import co.edu.unilibre.dto.HistorialDTO;
import co.edu.unilibre.model.HistorialPartida;
import co.edu.unilibre.model.enums.Resultados;
import co.edu.unilibre.service.HistorialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistorialServiceTest {

    private HistorialService historialService;
    private PartidaDAOFalso partidaDAO;

    @BeforeEach
    void configurar() throws Exception {
        historialService = new HistorialService();
        partidaDAO = new PartidaDAOFalso();

        Field campoDao = HistorialService.class.getDeclaredField("partidaDAO");
        campoDao.setAccessible(true);
        campoDao.set(historialService, partidaDAO);
    }

    // =========================================
    // REGISTRAR PARTIDA
    // =========================================

    @Test
    void registrarPartida_DebeRechazarPartidaNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> historialService.registrarPartida(null)
        );
    }

    @Test
    void registrarPartida_DebeRechazarPartidaSinResultado() {
        HistorialPartida partida = new HistorialPartida(1, LocalDateTime.now(), 100.0, 1900.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> historialService.registrarPartida(partida)
        );
    }

    @Test
    void registrarPartida_DebeLanzarUnsupportedOperationCuandoTieneResultado() {
        HistorialPartida partida = new HistorialPartida(1, LocalDateTime.now(), 100.0, 2100.0);
        partida.registrarResultado(Resultados.GANADA);

        assertThrows(
                UnsupportedOperationException.class,
                () -> historialService.registrarPartida(partida)
        );
    }

    // =========================================
    // CONSULTAR HISTORIAL
    // =========================================

    @Test
    void consultarHistorial_DebeRechazarIdUsuarioInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> historialService.consultarHistorial(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> historialService.consultarHistorial(-5)
        );
    }

    @Test
    void consultarHistorial_DebeRetornarListaDeHistorialDelUsuario() {
        HistorialDTO registro = new HistorialDTO();
        registro.modificarId(10);
        registro.modificarFecha(LocalDateTime.now());
        registro.modificarResultado("GANADA");
        registro.modificarApuesta(200.0);
        registro.modificarPuntosGanados(400.0);
        registro.modificarPuntosPerdidos(0.0);
        registro.modificarSaldoFinal(2200.0);

        partidaDAO.historialSimulado.add(registro);

        List<HistorialDTO> resultado = historialService.consultarHistorial(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1, partidaDAO.ultimoIdConsultado);
        assertEquals("GANADA", resultado.get(0).obtenerResultado());
        assertEquals(200.0, resultado.get(0).obtenerApuesta(), 0.001);
    }

    @Test
    void consultarHistorial_DebeRetornarListaVaciaCuandoNoHayRegistros() {
        List<HistorialDTO> resultado = historialService.consultarHistorial(3);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(3, partidaDAO.ultimoIdConsultado);
    }

    private static class PartidaDAOFalso extends PartidaDAO {

        private final List<HistorialDTO> historialSimulado = new ArrayList<>();
        private int ultimoIdConsultado;

        @Override
        public List<HistorialDTO> consultarHistorial(int idUsuario) {
            this.ultimoIdConsultado = idUsuario;
            return new ArrayList<>(historialSimulado);
        }
    }
}
