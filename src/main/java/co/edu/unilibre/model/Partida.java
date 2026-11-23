package co.edu.unilibre.model;

import co.edu.unilibre.state.Estado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Partida {

    private int id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Mazo mazo;
    private Mano manoCrupier;
    private List<ManoJugador> manosJugador;
    private Estado estadoActual;
    private boolean finalizada;

    public Partida(int id, Mazo mazo, Estado estadoActual) {

        if (mazo == null) {
            throw new IllegalArgumentException("El mazo no puede ser nulo.");
        }

        if (estadoActual == null) {
            throw new IllegalArgumentException("El estado inicial no puede ser nulo.");
        }

        this.id = id;
        this.mazo = mazo;
        this.estadoActual = estadoActual;
        this.fechaInicio = null;
        this.fechaFin = null;
        this.manoCrupier = new Mano();
        this.manosJugador = new ArrayList<>();
        this.finalizada = false;
    }

    // Métodos de negocio

    public void iniciar() {

        validarPartidaActiva();

        if (fechaInicio != null) {
            throw new IllegalStateException("La partida ya ha sido iniciada.");
        }

        fechaInicio = LocalDateTime.now();

        estadoActual.iniciar(this);
    }

    public void agregarManoJugador(ManoJugador mano) {

        validarPartidaActiva();

        if (mano == null) {
            throw new IllegalArgumentException("La mano del jugador no puede ser nula.");
        }

        if (!manosJugador.isEmpty()) {
            throw new IllegalStateException("La mano inicial ya fue agregada.");
        }

        manosJugador.add(mano);
    }

    public void pedirCarta(int indiceMano) {

        validarPartidaActiva();
        validarIndiceMano(indiceMano);

        estadoActual.pedirCarta(this, indiceMano);
    }

    public void pedirCartaDesdeEstado(int indiceMano) {

        validarPartidaActiva();
        validarIndiceMano(indiceMano);

        ManoJugador mano = manosJugador.get(indiceMano);

        if (!mano.puedePedirCarta()) {
            throw new IllegalStateException("Esta mano no puede recibir más cartas.");
        }

        if (mazo.estaVacio()) {
            throw new IllegalStateException("No quedan cartas en el mazo.");
        }

        Carta carta = mazo.repartirCarta();

        mano.agregarCarta(carta);

        if (mano.superaVeintiuno()
                || mano.calcularPuntuacion() == 21) {

            mano.finalizarMano();
        }
    }

    public void plantarse(int indiceMano) {

        validarPartidaActiva();
        validarIndiceMano(indiceMano);

        estadoActual.plantarse(this, indiceMano);
    }

    public void plantarseDesdeEstado(int indiceMano) {

        validarPartidaActiva();
        validarIndiceMano(indiceMano);

        ManoJugador mano = manosJugador.get(indiceMano);

        if (!mano.puedePedirCarta()) {
            throw new IllegalStateException("La mano ya no permite realizar esta acción.");
        }

        mano.plantarse();
    }

    public boolean doblarApuesta(int indiceMano) {

        validarPartidaActiva();
        validarIndiceMano(indiceMano);

        ManoJugador mano = manosJugador.get(indiceMano);

        if (!mano.puedeDoblar() || mazo.estaVacio()) {
            return false;
        }

        mano.doblar();

        Carta carta = mazo.repartirCarta();

        mano.agregarCarta(carta);
        mano.finalizarMano();

        return true;
    }

    public boolean dividirMano() {

        validarPartidaActiva();

        if (manosJugador.isEmpty()) {
            return false;
        }

        ManoJugador manoOriginal = manosJugador.get(0);

        if (!manoOriginal.puedeDividir()) {
            return false;
        }

        if (mazo.cantidadCartas() < 2) {
            return false;
        }

        Apuesta apuestaOriginal =
                manoOriginal.obtenerApuesta();

        if (apuestaOriginal == null
                || apuestaOriginal.estaLiquidada()) {

            return false;
        }

        Carta cartaSeparada =
                manoOriginal.obtenerCartas().get(1);

        boolean divisionAses =
                manoOriginal.obtenerCartas().get(0).esAs()
                        && cartaSeparada.esAs();

        Apuesta nuevaApuesta =
                new Apuesta(
                        apuestaOriginal.obtenerMontoInicial()
                );

        ManoJugador nuevaMano =
                new ManoJugador(nuevaApuesta);

        List<Carta> cartasOriginales =
                new ArrayList<>(manoOriginal.obtenerCartas());

        cartasOriginales.remove(1);

        manoOriginal.modificarCartas(cartasOriginales);

        nuevaMano.agregarCarta(cartaSeparada);

        manoOriginal.modificarDividida(true);
        nuevaMano.modificarDividida(true);

        // Cada mano recibe una carta adicional.

        Carta cartaOriginal = mazo.repartirCarta();
        Carta cartaNueva = mazo.repartirCarta();

        manoOriginal.agregarCarta(cartaOriginal);
        nuevaMano.agregarCarta(cartaNueva);

        if (divisionAses) {

            manoOriginal.modificarAsDividido(true);
            nuevaMano.modificarAsDividido(true);

            manoOriginal.finalizarMano();
            nuevaMano.finalizarMano();

        } else {

            if (manoOriginal.superaVeintiuno()
                    || manoOriginal.calcularPuntuacion() == 21) {

                manoOriginal.finalizarMano();
            }

            if (nuevaMano.superaVeintiuno()
                    || nuevaMano.calcularPuntuacion() == 21) {

                nuevaMano.finalizarMano();
            }
        }

        manosJugador.add(nuevaMano);

        return true;
    }

    public void pedirCartaCrupier() {

        validarPartidaActiva();

        if (mazo.estaVacio()) {
            throw new IllegalStateException(
                    "No quedan cartas en el mazo."
            );
        }

        Carta carta = mazo.repartirCarta();

        manoCrupier.agregarCarta(carta);
    }

    public void ejecutarTurnoCrupier() {

        validarPartidaActiva();

        estadoActual.ejecutarTurno(this);
    }


    public void ejecutarTurnoCrupierDesdeEstado() {

        validarPartidaActiva();
    }

    public void finalizar() {

        if (!finalizada) {

            finalizada = true;
            fechaFin = LocalDateTime.now();
        }
    }

    public boolean estaFinalizada() {
        return finalizada;
    }

    private void validarPartidaActiva() {

        if (finalizada) {
            throw new IllegalStateException("La partida ya está finalizada.");
        }
    }

    private void validarIndiceMano(int indiceMano) {

        if (indiceMano < 0
                || indiceMano >= manosJugador.size()) {

            throw new IndexOutOfBoundsException("El índice de la mano no es válido.");
        }
    }

    // Métodos de encapsulamiento

    public int obtenerId() {
        return id;
    }

    public void modificarId(int id) {
        this.id = id;
    }

    public LocalDateTime obtenerFechaInicio() {
        return fechaInicio;
    }

    public void modificarFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime obtenerFechaFin() {
        return fechaFin;
    }

    public void modificarFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Mazo obtenerMazo() {
        return mazo;
    }

    public void modificarMazo(Mazo mazo) {

        if (mazo == null) {
            throw new IllegalArgumentException("El mazo no puede ser nulo.");
        }

        this.mazo = mazo;
    }

    public Mano obtenerManoCrupier() {
        return manoCrupier;
    }

    public void modificarManoCrupier(Mano manoCrupier) {

        if (manoCrupier == null) {
            throw new IllegalArgumentException("La mano del crupier no puede ser nula.");
        }

        this.manoCrupier = manoCrupier;
    }

    public List<ManoJugador> obtenerManosJugador() {
        return List.copyOf(manosJugador);
    }

    public void modificarManosJugador(
            List<ManoJugador> manosJugador) {

        if (manosJugador == null) {
            throw new IllegalArgumentException("La lista de manos no puede ser nula.");
        }

        if (manosJugador.stream().anyMatch(mano -> mano == null)) {
            throw new IllegalArgumentException("La lista no puede contener manos nulas.");
        }

        this.manosJugador = new ArrayList<>(manosJugador);
    }

    public Estado obtenerEstadoActual() {
        return estadoActual;
    }

    public void modificarEstadoActual(Estado estadoActual) {

        if (estadoActual == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }

        this.estadoActual = estadoActual;
    }

    public boolean obtenerFinalizada() {
        return finalizada;
    }

    public void modificarFinalizada(boolean finalizada) {

        if (this.finalizada && !finalizada) {
            throw new IllegalStateException("No se puede reabrir una partida finalizada.");
        }

        this.finalizada = finalizada;

        if (finalizada && fechaFin == null) {
            fechaFin = LocalDateTime.now();
        }
    }
}