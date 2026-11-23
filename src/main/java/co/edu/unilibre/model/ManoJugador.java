package co.edu.unilibre.model;

import co.edu.unilibre.model.enums.Resultado;

public class ManoJugador extends Mano {

    private Apuesta apuesta;
    private Resultado resultado;
    private boolean plantada;
    private boolean doblada;
    private boolean dividida;
    private boolean asDividido;

    public ManoJugador(Apuesta apuesta) {
        super();

        if (apuesta == null) {
            throw new IllegalArgumentException("La apuesta no puede ser nula.");
        }

        this.apuesta = apuesta;
        this.resultado = null;
        this.plantada = false;
        this.doblada = false;
        this.dividida = false;
        this.asDividido = false;
    }

    // Métodos de negocio

    public void plantarse() {
        plantada = true;
    }

    public void doblar() {
        if (!puedeDoblar()) {
            throw new IllegalStateException("No es posible doblar esta mano.");
        }

        if (!apuesta.aumentarApuesta(apuesta.obtenerMontoInicial())) {
            throw new IllegalStateException("No se pudo aumentar la apuesta.");
        }

        doblada = true;
    }

    public boolean puedePedirCarta() {
        return !plantada && !doblada && !superaVeintiuno() && !finalizarPorBlackjack();
    }

    public boolean puedeDoblar() {
        return !plantada && !doblada && !dividida && cartas.size() == 2 && !superaVeintiuno() && apuesta != null && !apuesta.estaLiquidada();
    }

    public boolean puedeDividir() {
        if (plantada || doblada || dividida || cartas.size() != 2) {
            return false;
        }

        if (cartas.get(0).esAs() && cartas.get(1).esAs()) {
            return !asDividido;
        }

        return cartas.get(0).obtenerValor() == cartas.get(1).obtenerValor();
    }

    public void finalizarMano() {
        plantada = true;
    }

    private boolean finalizarPorBlackjack() {
        return tieneBlackjackNatural();
    }

    // Métodos de encapsulamiento

    public Apuesta obtenerApuesta() {
        return apuesta;
    }

    public void modificarApuesta(Apuesta apuesta) {
        if (apuesta == null) {
            throw new IllegalArgumentException("La apuesta no puede ser nula.");
        }
        this.apuesta = apuesta;
    }

    public Resultado obtenerResultado() {
        return resultado;
    }

    public void modificarResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public boolean obtenerPlantada() {
        return plantada;
    }

    public void modificarPlantada(boolean plantada) {
        this.plantada = plantada;
    }

    public boolean obtenerDoblada() {
        return doblada;
    }

    public void modificarDoblada(boolean doblada) {
        this.doblada = doblada;
    }

    public boolean obtenerDividida() {
        return dividida;
    }

    public void modificarDividida(boolean dividida) {
        this.dividida = dividida;
    }

    public boolean obtenerAsDividido() {
        return asDividido;
    }

    public void modificarAsDividido(boolean asDividido) {
        this.asDividido = asDividido;
    }
}