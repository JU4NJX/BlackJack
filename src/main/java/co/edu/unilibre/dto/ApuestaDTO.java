package co.edu.unilibre.dto;

public class ApuestaDTO {

    private int cantidadInicial;
    private int cantidadTotal;
    private String tipo;

    public ApuestaDTO(int cantidadInicial, int cantidadTotal, String tipo) {
        this.cantidadInicial = cantidadInicial;
        this.cantidadTotal = cantidadTotal;
        this.tipo = tipo;
    }

    //ENCAPSULAMIENTO
    public int obtenerCantidadInicial() {
        return cantidadInicial;
    }

    public void modificarCantidadInicial(int cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public int obtenerCantidadTotal() {
        return cantidadTotal;
    }

    public void modificarCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public String obtenerTipo() {
        return tipo;
    }

    public void modificarTipo(String tipo) {
        this.tipo = tipo;
    }

}
