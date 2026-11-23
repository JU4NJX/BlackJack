package co.edu.unilibre.dto;

public class ApuestaDTO {

    private double montoInicial;
    private double montoTotal;
    private double montoAdicional;
    private double saldoDisponible;

    public double obtenerMontoInicial() {
        return montoInicial;
    }

    public void modificarMontoInicial(double montoInicial) {
        this.montoInicial = montoInicial;
    }

    public double obtenerMontoTotal() {
        return montoTotal;
    }

    public void modificarMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double obtenerMontoAdicional() {
        return montoAdicional;
    }

    public void modificarMontoAdicional(double montoAdicional) {
        this.montoAdicional = montoAdicional;
    }

    public double obtenerSaldoDisponible() {
        return saldoDisponible;
    }

    public void modificarSaldoDisponible(double saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }
}