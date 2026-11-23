package co.edu.unilibre.dto;

public class CartaDTO {

    private String palo;
    private String valor;
    private String imagen;
    private boolean visible;

    public String obtenerPalo() {
        return palo;
    }

    public void modificarPalo(String palo) {
        this.palo = palo;
    }

    public String obtenerValor() {
        return valor;
    }

    public void modificarValor(String valor) {
        this.valor = valor;
    }

    public String obtenerImagen() {
        return imagen;
    }

    public void modificarImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean obtenerVisible() {
        return visible;
    }

    public void modificarVisible(boolean visible) {
        this.visible = visible;
    }
}