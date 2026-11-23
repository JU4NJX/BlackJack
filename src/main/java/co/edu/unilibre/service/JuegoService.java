package co.edu.unilibre.service;

import co.edu.unilibre.dto.CartaDTO;
import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.factory.MazoFactory;
import co.edu.unilibre.model.*;
import co.edu.unilibre.strategy.EstrategiaCrupier;

import java.util.ArrayList;
import java.util.List;

public class JuegoService implements Sujeto {

    private List<Observador> observadores;
    private EstrategiaCrupier estrategiaCrupier;
    private MazoFactory mazoFactory;

    private ApuestaService apuestaService;
    private PuntuacionService puntuacionService;
    private UsuarioService usuarioService;

    private Partida partidaActual;
    private Usuario usuarioActual;

    public JuegoService(
            EstrategiaCrupier estrategiaCrupier,
            MazoFactory mazoFactory,
            ApuestaService apuestaService,
            PuntuacionService puntuacionService,
            UsuarioService usuarioService) {

        this.observadores = new ArrayList<>();
        this.estrategiaCrupier = estrategiaCrupier;
        this.mazoFactory = mazoFactory;
        this.apuestaService = apuestaService;
        this.puntuacionService = puntuacionService;
        this.usuarioService = usuarioService;
    }

    public PartidaDTO iniciarPartida(Usuario usuario, double apuesta) {

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        if (partidaActual != null && !partidaActual.estaFinalizada()) {
            throw new IllegalStateException("Ya existe una partida en curso.");
        }

        if (!apuestaService.validarApuesta(usuario.obtenerSaldoPuntos(), apuesta)) {
            throw new IllegalArgumentException("La apuesta no es válida.");
        }

        this.usuarioActual = usuario;

        Mazo mazo = mazoFactory.crearMazo();

        partidaActual = new Partida(mazo);

        apuestaService.registrarApuesta(
                new Apuesta(apuesta)
        );

        partidaActual.iniciar();

        PartidaDTO partidaDTO = construirPartidaDTO();

        notificarObservadores();

        return partidaDTO;
    }

    public PartidaDTO pedirCarta(int indiceMano) {

        validarPartidaActiva();

        partidaActual.pedirCarta(indiceMano);

        if (partidaActual.estaFinalizada()) {
            finalizarPartida();
        }

        return construirYNotificar();
    }

    public PartidaDTO plantarse(int indiceMano) {

        validarPartidaActiva();

        partidaActual.plantarse(indiceMano);

        return construirYNotificar();
    }

    public PartidaDTO doblarApuesta(int indiceMano) {

        validarPartidaActiva();

        ManoJugador mano = partidaActual.obtenerManosJugador().get(indiceMano);

        if (!mano.puedeDoblar()) {
            throw new IllegalStateException(
                    "No es posible doblar la apuesta."
            );
        }

        if (!apuestaService.procesarDobleApuesta(mano.obtenerApuesta())) {
            throw new IllegalStateException(
                    "No hay saldo suficiente para doblar."
            );
        }

        partidaActual.doblarApuesta(indiceMano);

        return construirYNotificar();
    }

    public PartidaDTO dividirMano() {

        validarPartidaActiva();

        if (!partidaActual.dividirMano()) {
            throw new IllegalStateException(
                    "No es posible dividir la mano."
            );
        }

        ManoJugador primeraMano =
                partidaActual.getManosJugador().get(0);

        apuestaService.procesarSplit(primeraMano.getApuesta());

        return construirYNotificar();
    }

    public PartidaDTO ejecutarTurnoCrupier() {

        validarPartidaActiva();

        Mano manoCrupier = partidaActual.getManoCrupier();

        while (estrategiaCrupier.debePedirCarta(manoCrupier)) {
            partidaActual.getMazo().repartirCarta();
            manoCrupier.agregarCarta(
                    partidaActual.getMazo().repartirCarta()
            );
        }

        partidaActual.ejecutarTurnoCrupier();

        return construirYNotificar();
    }

    public PartidaDTO finalizarPartida() {

        validarPartidaActiva();

        partidaActual.finalizar();

        apuestaService.liquidarPartida(
                partidaActual,
                usuarioActual
        );

        PartidaDTO partidaDTO = construirPartidaDTO();

        notificarObservadores();

        return partidaDTO;
    }

    @Override
    public void agregarObservador(Observador observador) {

        if (observador == null) {
            throw new IllegalArgumentException(
                    "El observador no puede ser nulo."
            );
        }

        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void eliminarObservador(Observador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores() {

        PartidaDTO partidaDTO = construirPartidaDTO();

        for (Observador observador : observadores) {
            observador.actualizar(partidaDTO);
        }
    }

    private void validarPartidaActiva() {

        if (partidaActual == null || partidaActual.estaFinalizada()) {
            throw new IllegalStateException(
                    "No existe una partida activa."
            );
        }
    }

    private PartidaDTO construirYNotificar() {

        PartidaDTO partidaDTO = construirPartidaDTO();

        notificarObservadores();

        return partidaDTO;
    }

    private PartidaDTO construirPartidaDTO() {

        PartidaDTO dto = new PartidaDTO();

        if (partidaActual == null) {
            return dto;
        }

        dto.id = partidaActual.getId();

        dto.cartasCrupier = convertirCartas(
                partidaActual.getManoCrupier().obtenerCartas()
        );

        dto.puntuacionCrupier =
                puntuacionService.calcularPuntuacion(
                        partidaActual.getManoCrupier()
                );

        dto.cartasJugador = new ArrayList<>();
        dto.manosJugador = new ArrayList<>();

        for (ManoJugador mano :
                partidaActual.getManosJugador()) {

            List<CartaDTO> cartas =
                    convertirCartas(mano.obtenerCartas());

            dto.manosJugador.add(cartas);

            dto.cartasJugador.addAll(cartas);

            dto.puntuacionJugador +=
                    puntuacionService.calcularPuntuacion(mano);
        }

        dto.estado = partidaActual.getEstadoActual();

        dto.saldoActual = usuarioActual.getSaldo();

        return dto;
    }

    private List<CartaDTO> convertirCartas(List<Carta> cartas) {

        List<CartaDTO> cartasDTO = new ArrayList<>();

        for (Carta carta : cartas) {

            CartaDTO dto = new CartaDTO();

            dto.palo = carta.getPalo().name();
            dto.valor = carta.getValor().name();

            cartasDTO.add(dto);
        }

        return cartasDTO;
    }
}
