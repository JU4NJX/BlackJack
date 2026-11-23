package co.edu.unilibre.dto;

public class UsuarioDTO {

    private int id;
    private String nombreUsuario;
    private String correo;
    private double saldo;

    public int obtenerId() {
        return id;
    }

    public void modificarId(int id) {
        this.id = id;
    }

    public String obtenerNombreUsuario() {
        return nombreUsuario;
    }

    public void modificarNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String obtenerCorreo() {
        return correo;
    }

    public void modificarCorreo(String correo) {
        this.correo = correo;
    }

    public double obtenerSaldo() {
        return saldo;
    }

    public void modificarSaldo(double saldo) {
        this.saldo = saldo;
    }
}