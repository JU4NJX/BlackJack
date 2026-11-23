package co.edu.unilibre.service;

import co.edu.unilibre.dto.PartidaDTO;
import co.edu.unilibre.dto.CartaDTO;

import co.edu.unilibre.model.Partida;
import co.edu.unilibre.model.Usuario;
import co.edu.unilibre.model.Carta;
import co.edu.unilibre.model.Mano;
import co.edu.unilibre.model.ManoJugador;
import co.edu.unilibre.model.Apuesta;

import co.edu.unilibre.model.enums.EstadoPartida;
import co.edu.unilibre.model.enums.Resultado;

import co.edu.unilibre.factory.MazoFactory;

import co.edu.unilibre.strategy.EstrategiaCrupier;

import co.edu.unilibre.state.EstadoInicial;
import co.edu.unilibre.state.EstadoTurnoCrupier;
import co.edu.unilibre.state.EstadoFinalizado;

import co.edu.unilibre.observer.Observador;
import co.edu.unilibre.observer.Sujeto;

import java.util.ArrayList;
import java.util.List;

public class JuegoService implements Sujeto {

    private List<Observador> observadores;
    private EstrategiaCrupier estrategiaCrupier;
    private MazoFactory mazoFactory;

    private Partida partidaActual;
    private Usuario usuarioActual;

    private PuntuacionService puntuacionService;
    private ApuestaService apuestaService;

    public JuegoService(
            EstrategiaCrupier estrategiaCrupier,
            MazoFactory mazoFactory) {

        this(
                estrategiaCrupier,
                mazoFactory,
                new ApuestaService()
        );
    }

    public JuegoService(
            EstrategiaCrupier estrategiaCrupier,
            MazoFactory mazoFactory,
            ApuestaService apuestaService) {

        if (estrategiaCrupier == null
                || mazoFactory == null
                || apuestaService == null) {

            throw new IllegalArgumentException(
                    "Las dependencias no pueden ser nulas."
            );
        }

        this.observadores = new ArrayList<>();
        this.estrategiaCrupier = estrategiaCrupier;
        this.mazoFactory = mazoFactory;
        this.puntuacionService = new PuntuacionService();
        this.apuestaService = apuestaService;
    }

    // Iniciar partida

    public PartidaDTO iniciarPartida(Usuario usuario, double apuesta) {

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        if (partidaActual != null && !partidaActual.estaFinalizada()) {
            throw new IllegalStateException("El usuario ya tiene una partida en curso.");
        }

        if (!apuestaService.validarApuesta(usuario.obtenerSaldo(), apuesta)) {
            throw new IllegalArgumentException("La apuesta no es válida o el saldo es insuficiente.");
        }

        Apuesta nuevaApuesta = new Apuesta(apuesta);

        // Crear la partida antes de registrar la apuesta.
        Partida nuevaPartida = new Partida(0, mazoFactory.crearMazo(), new EstadoInicial());

        // Crear y agregar la mano inicial.
        ManoJugador manoJugador = new ManoJugador(nuevaApuesta);

        nuevaPartida.agregarManoJugador(manoJugador);

        // Iniciar la partida para establecer la fecha de inicio.
        nuevaPartida.iniciar();

        // Descontar la apuesta y guardar el registro pendiente de liquidación en la base de datos.
        apuestaService.registrarApuesta(nuevaApuesta, usuario, nuevaPartida);

        this.usuarioActual = usuario;
        this.partidaActual = nuevaPartida;

        notificarObservadores();

        return construirPartidaDTO();
    }

    // Pedir carta

    public PartidaDTO pedirCarta(int indiceMano) {

        validarPartidaActiva();
        validarTurnoJugador();

        partidaActual.pedirCarta(indiceMano);

        notificarObservadores();

        return construirPartidaDTO();
    }

    // Plantarse

    public PartidaDTO plantarse(int indiceMano) {

        validarPartidaActiva();
        validarTurnoJugador();

        partidaActual.plantarse(indiceMano);

        notificarObservadores();

        return construirPartidaDTO();
    }

    // Doblar apuesta

    public PartidaDTO doblarApuesta(int indiceMano) {

        validarPartidaActiva();
        validarTurnoJugador();

        List<ManoJugador> manos = partidaActual.obtenerManosJugador();

        validarIndiceMano(indiceMano, manos);

        ManoJugador mano = manos.get(indiceMano);

        if (!mano.puedeDoblar()) {
            throw new IllegalStateException("Esta mano no permite doblar.");
        }

        // Descontar la apuesta adicional.
        if (!apuestaService.procesarDobleApuesta(mano.obtenerApuesta(), usuarioActual)) {
            throw new IllegalStateException("No se pudo procesar la doble apuesta.");
        }

        // Aumentar la apuesta en el modelo.
        mano.doblar();

        // Recibir una única carta adicional.
        partidaActual.pedirCarta(indiceMano);

        // Después de doblar, la mano queda plantada.
        mano.plantarse();

        notificarObservadores();

        return construirPartidaDTO();
    }

    // Dividir mano

    public PartidaDTO dividirMano() {

        validarPartidaActiva();
        validarTurnoJugador();

        List<ManoJugador> manos = partidaActual.obtenerManosJugador();

        if (manos.isEmpty()) {throw new IllegalStateException("No existe una mano para dividir.");}

        ManoJugador mano = manos.get(0);

        if (!mano.puedeDividir()) {throw new IllegalStateException("La mano no permite dividir.");}

        // Descontar la apuesta adicional.
        if (!apuestaService.procesarSplit(mano.obtenerApuesta(), usuarioActual)) {
            throw new IllegalStateException("No se pudo procesar la división.");
        }

        if (!partidaActual.dividirMano()) {
            throw new IllegalStateException("No se pudo dividir la mano.");
        }

        notificarObservadores();

        return construirPartidaDTO();
    }

    // Ejecutar turno del crupier

    public PartidaDTO ejecutarTurnoCrupier() {

        validarPartidaActiva();

        List<ManoJugador> manos = partidaActual.obtenerManosJugador();

        if (manos.isEmpty()) {
            throw new IllegalStateException("La partida no tiene manos de jugador.");
        }

        // Todas las manos deben estar finalizadas.
        for (ManoJugador mano : manos) {

            if (!mano.estaFinalizada()) {
                throw new IllegalStateException("Todas las manos del jugador deben finalizar antes del turno del crupier.");}
        }

        // Ejecutar la estrategia del crupier.
        Mano manoCrupier = partidaActual.obtenerManoCrupier();

        while (estrategiaCrupier.debePedirCarta(manoCrupier)) {

            if (partidaActual.obtenerMazo().estaVacio()) {
                break;
            }

            partidaActual.pedirCartaCrupier();
        }

        // Cambiar al estado del crupier.
        partidaActual.modificarEstadoActual(
                new EstadoTurnoCrupier()
        );

        partidaActual.ejecutarTurnoCrupierDesdeEstado();

        // Asignar resultados y liquidar la partida.
        finalizarPartida();

        return construirPartidaDTO();
    }

    // Finalizar y liquidar partida

    public PartidaDTO finalizarPartida() {

        validarPartidaActiva();

        List<ManoJugador> manos = partidaActual.obtenerManosJugador();

        Mano manoCrupier = partidaActual.obtenerManoCrupier();

        // Determinar el resultado de cada mano.
        for (ManoJugador mano : manos) {
            Resultado resultado = determinarResultadoMano(mano, manoCrupier);
            mano.modificarResultado(resultado);
        }

        // Marcar la partida como finalizada.
        partidaActual.finalizar();

        partidaActual.modificarEstadoActual(
                new EstadoFinalizado()
        );

        try {

            // Liquidar todas las apuestas y actualizar
            // saldo, movimientos e historial en la base de datos.
            apuestaService.liquidarPartida(partidaActual, usuarioActual);

        } catch (RuntimeException e) {

            // Si la liquidación falla, permitir reintentarla.
            partidaActual.modificarFinalizada(false);
            partidaActual.modificarFechaFin(null);
            partidaActual.modificarEstadoActual(new EstadoTurnoCrupier());
            throw e;
        }

        notificarObservadores();

        return construirPartidaDTO();
    }

    // Determinar resultado individual de una mano

    private Resultado determinarResultadoMano(ManoJugador mano, Mano manoCrupier) {

        // Una mano dividida no puede obtener premio de blackjack.
        if (mano.obtenerDividida()) {

            // Un blackjack natural del crupier vence a un 21
            // conseguido mediante división.
            if (puntuacionService.tieneBlackjack(manoCrupier)) {
                return Resultado.PERDIDA;
            }

            // Comparar puntuaciones sin aplicar premio de blackjack.
            return puntuacionService.compararPuntuaciones(mano, manoCrupier);
        }

        return puntuacionService.determinarResultado(mano, manoCrupier);
    }

    // Agregar observador

    @Override
    public void agregarObservador(Observador observador) {

        if (observador == null) {
            throw new IllegalArgumentException("El observador no puede ser nulo.");
        }

        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    // Eliminar observador

    @Override
    public void eliminarObservador(Observador observador) {
        observadores.remove(observador);
    }

    // Notificar observadores

    @Override
    public void notificarObservadores() {

        if (partidaActual == null || usuarioActual == null) {
            return;
        }

        PartidaDTO dto = construirPartidaDTO();

        for (Observador observador : new ArrayList<>(observadores)) {
            observador.actualizar(dto);
        }
    }

    // Validar partida activa

    private void validarPartidaActiva() {

        if (partidaActual == null || partidaActual.estaFinalizada()) {
            throw new IllegalStateException("No existe una partida activa.");}
    }

    // Validar turno del jugador

    private void validarTurnoJugador() {

        if (partidaActual.obtenerEstadoActual() instanceof EstadoTurnoCrupier) {

            throw new IllegalStateException("No es el turno del jugador.");
        }

        if (partidaActual.obtenerEstadoActual()
                instanceof EstadoFinalizado) {

            throw new IllegalStateException("La partida ya ha finalizado.");
        }
    }

    // Validar índice de mano

    private void validarIndiceMano(
            int indice,
            List<ManoJugador> manos) {

        if (indice < 0 || indice >= manos.size()) {
            throw new IllegalArgumentException("El índice de la mano no es válido.");}
    }

    // Construir DTO

    private PartidaDTO construirPartidaDTO() {

        PartidaDTO dto = new PartidaDTO();

        dto.modificarId(partidaActual.obtenerId());

        List<ManoJugador> manos = partidaActual.obtenerManosJugador();

        List<List<CartaDTO>> manosDTO = new ArrayList<>();

        List<CartaDTO> cartasJugador = new ArrayList<>();

        int puntuacionJugador = 0;
        double apuestaTotal = 0;

        for (ManoJugador mano : manos) {

            List<CartaDTO> cartasMano = new ArrayList<>();

            for (Carta carta : mano.obtenerCartas()) {

                cartasMano.add(convertirCartaDTO(carta, true));
            }

            manosDTO.add(cartasMano);
            cartasJugador.addAll(cartasMano);

            puntuacionJugador += mano.calcularPuntuacion();

            apuestaTotal += mano.obtenerApuesta().obtenerMontoTotal();
        }

        List<CartaDTO> cartasCrupier = new ArrayList<>();

        Mano manoCrupier = partidaActual.obtenerManoCrupier();

        for (int i = 0;
             i < manoCrupier.obtenerCartas().size(); i++) {
            Carta carta = manoCrupier.obtenerCartas().get(i);
            boolean visible = i != 1 || partidaActual.estaFinalizada();
            cartasCrupier.add(convertirCartaDTO(carta, visible));
        }

        dto.modificarCartasJugador(cartasJugador);
        dto.modificarCartasCrupier(cartasCrupier);
        dto.modificarManosJugador(manosDTO);

        dto.modificarPuntuacionJugador(puntuacionJugador);

        dto.modificarPuntuacionCrupier(partidaActual.estaFinalizada() ? puntuacionService.calcularPuntuacion(manoCrupier) : 0);

        dto.modificarSaldoActual(usuarioActual.obtenerSaldo());
        dto.modificarApuestaTotal(apuestaTotal);

        dto.modificarEstado(partidaActual.estaFinalizada() ? EstadoPartida.FINALIZADA : EstadoPartida.TURNO_JUGADOR
        );

        return dto;
    }

    // Convertir Carta a CartaDTO

    private CartaDTO convertirCartaDTO(Carta carta, boolean visible) {

        CartaDTO dto = new CartaDTO();

        dto.modificarPalo(carta.obtenerPalo().name());

        dto.modificarValor(carta.obtenerValorCarta().name());

        dto.modificarVisible(visible);

        if (visible) {

            dto.modificarImagen(carta.obtenerPalo().name().toLowerCase() + "_" + carta.obtenerValorCarta().name().toLowerCase() + ".png");
        }
        return dto;
    }
}