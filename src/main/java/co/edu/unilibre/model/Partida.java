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
        if (finalizada) {
            throw new IllegalStateException("La partida ya está finalizada.");
        }

        if (estadoActual == null) {
            throw new IllegalStateException("No hay un estado asignado a la partida.");
        }

        estadoActual.iniciar(this);
    }

    public void pedirCarta(int indiceMano) {
        validarIndiceMano(indiceMano);

        if (estadoActual == null) {
            throw new IllegalStateException("No hay un estado asignado a la partida.");
        }

        estadoActual.pedirCarta(this, indiceMano);
    }

    public void plantarse(int indiceMano) {
        validarIndiceMano(indiceMano);

        if (estadoActual == null) {
            throw new IllegalStateException("No hay un estado asignado a la partida.");
        }

        estadoActual.plantarse(this, indiceMano);
    }

    public boolean doblarApuesta(int indiceMano) {
        validarIndiceMano(indiceMano);

        ManoJugador mano = manosJugador.get(indiceMano);

        if (!mano.puedeDoblar()) {
            return false;
        }

        mano.doblar();
        return true;
    }

    public boolean dividirMano() {
        if (manosJugador.isEmpty()) {
            return false;
        }

        ManoJugador manoOriginal = manosJugador.get(0);

        if (!manoOriginal.puedeDividir()) {
            return false;
        }

        if (mazo.estaVacio()) {
            return false;
        }

        Apuesta apuestaOriginal = manoOriginal.obtenerApuesta();

        Apuesta nuevaApuesta = new Apuesta(apuestaOriginal.obtenerMontoInicial());

        ManoJugador nuevaMano = new ManoJugador(nuevaApuesta);

        Carta cartaSeparada = manoOriginal.obtenerCartas().get(1);

        List<Carta> cartasOriginales = new ArrayList<>(manoOriginal.obtenerCartas());
        cartasOriginales.remove(1);

        manoOriginal.modificarCartas(cartasOriginales);
        nuevaMano.agregarCarta(cartaSeparada);

        manoOriginal.modificarDividida(true);
        nuevaMano.modificarDividida(true);

        if (cartaSeparada.esAs() && manoOriginal.obtenerCartas().get(0).esAs()) {
            manoOriginal.modificarAsDividido(true);
            nuevaMano.modificarAsDividido(true);
            manoOriginal.finalizarMano();
            nuevaMano.finalizarMano();
        }

        manosJugador.add(nuevaMano);
        return true;
    }

    public void ejecutarTurnoCrupier() {
        if (finalizada) {
            throw new IllegalStateException("La partida ya está finalizada.");
        }

        if (estadoActual == null) {
            throw new IllegalStateException("No hay un estado asignado a la partida.");
        }

        estadoActual.ejecutarTurno(this);
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

    private void validarIndiceMano(int indiceMano) {
        if (indiceMano < 0 || indiceMano >= manosJugador.size()) {
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

    public void modificarManosJugador(List<ManoJugador> manosJugador) {
        if (manosJugador == null) {
            throw new IllegalArgumentException("La lista de manos no puede ser nula.");
        }
        this.manosJugador = new ArrayList<>(manosJugador);
    }

    public Estado obtenerEstadoActual() {
        return estadoActual;
    }

    public void modificarEstadoActual(Estado estadoActual) {
        this.estadoActual = estadoActual;
    }

    public boolean obtenerFinalizada() {
        return finalizada;
    }

    public void modificarFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }
}