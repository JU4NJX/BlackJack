package co.edu.unilibre.model;

import java.time.LocalDateTime;

public class Usuario {

    private int id;
    private String nombreUsuario;
    private String correo;
    private String claveHash;
    private double saldo;
    private LocalDateTime fechaRegistro;

    public Usuario(int id, String nombreUsuario, String correo,
                   String claveHash, double saldo, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.claveHash = claveHash;
        this.saldo = saldo;
        this.fechaRegistro = fechaRegistro;
    }

    // Métodos de negocio

    public void depositarPuntos(double cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        saldo += cantidad;
    }

    public boolean descontarPuntos(double cantidad) {
        if (cantidad <= 0) {
            return false;
        }

        if (!tieneSaldo(cantidad)) {
            return false;
        }

        saldo -= cantidad;
        return true;
    }

    public void recuperarPuntos() {
        saldo = 0;
    }

    public boolean tieneSaldo(double cantidad) {
        return cantidad >= 0 && saldo >= cantidad;
    }

    // Métodos de encapsulamiento

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

    public String obtenerClaveHash() {
        return claveHash;
    }

    public void modificarClaveHash(String claveHash) {
        this.claveHash = claveHash;
    }

    public double obtenerSaldo() {
        return saldo;
    }

    public void modificarSaldo(double saldo) {
        if (saldo < 0) {
            throw new IllegalArgumentException("El saldo no puede ser negativo.");
        }
        this.saldo = saldo;
    }

    public LocalDateTime obtenerFechaRegistro() {
        return fechaRegistro;
    }

    public void modificarFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}