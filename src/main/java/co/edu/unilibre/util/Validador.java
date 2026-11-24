package co.edu.unilibre.util;

public class Validador {
    public static boolean validarCorreo(String correo) {
        return correo != null && correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    public static boolean validarClave(String clave) {
        return clave != null && clave.length() >= 8;
    }
    public static boolean validarMonto(double monto) {
        return Double.isFinite(monto) && monto > 0;
    }
    public static boolean validarNombreUsuario(String nombre) {
        return nombre != null && nombre.trim().length() >= 3 && nombre.trim().length() <= 30;
    }
}

