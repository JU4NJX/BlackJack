package co.edu.unilibre.dto;

public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String correo;
    private int saldo;

    public UsuarioDTO(Long id, String nombre, String correo, int saldo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.saldo = saldo;
    }

    //ENCAPSULAMIENTO

    public Long obtenerId() {
        return id;
    }

    public void modificarId(Long id) {
        this.id = id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public void modificarNombre(String nombre) {
        this.nombre = nombre;
    }

    public String obtenerCorreo() {
        return correo;
    }

    public void modificarCorreo(String correo) {
        this.correo = correo;
    }

    public int obtenerSaldo() {
        return saldo;
    }

    public void modificarSaldo(int saldo) {
        this.saldo = saldo;
    }
}
