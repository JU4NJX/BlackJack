package co.edu.unilibre.dto;

import co.edu.unilibre.model.enums.Resultado;

import java.time.LocalDateTime;

public class HistorialDTO {

    private Long id;
    private LocalDateTime fecha;
    private Resultado resultado;
    private int apuestaTotal;
    private int puntosGastadosPerdidos;
    private int saldoFinal;

    public HistorialDTO() {

    }

    //ENCAPSULAMIENTO

    public Long obtenerId() {
        return id;
    }

    public void modificarId(Long id) {
        this.id = id;
    }

    public LocalDateTime obtenerFecha() {
        return fecha;
    }

    public void modificarFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Resultado obtenerResultado() {
        return resultado;
    }

    public void modificarResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public int obtenerApuestaTotal() {
        return apuestaTotal;
    }

    public void modificarApuestaTotal(int apuestaTotal) {
        this.apuestaTotal = apuestaTotal;
    }

    public int obtenerPuntosGastadosPerdidos() {
        return puntosGastadosPerdidos;
    }

    public void modificarPuntosGastadosPerdidos(int puntosGastadosPerdidos) {
        this.puntosGastadosPerdidos = puntosGastadosPerdidos;
    }

    public int obtenerSaldoFinal() {
        return saldoFinal;
    }

    public void modificarSaldoFinal(int saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
}
