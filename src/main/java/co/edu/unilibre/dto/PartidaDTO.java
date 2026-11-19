package co.edu.unilibre.dto;

import co.edu.unilibre.model.enums.EstadoPartida;
import co.edu.unilibre.model.enums.Resultado;

import java.time.LocalDateTime;
import java.util.List;

public class PartidaDTO {

    private Long id;
    private Long idUsuario;
    private List<List<CartaDTO>> manosJuagdor;
    private List<Integer> puntuacionesManos;
    private List<String> estadosManos;
    private List<ApuestaDTO> apuestasManos;
    private List<CartaDTO> manoCrupier;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private EstadoPartida estado;
    private Resultado resultado;
    private int saldoFinal;

    //ENCAPSULAMIENTO

    public Long obtenerId() {
        return id;
    }

    public void modificarId(Long id) {
        this.id = id;
    }

    public Long obtenerIdUsuario() {
        return idUsuario;
    }

    public void modificarIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<List<CartaDTO>> obtenerManosJuagdor() {
        return manosJuagdor;
    }

    public void modificarManosJuagdor(List<List<CartaDTO>> manosJuagdor) {
        this.manosJuagdor = manosJuagdor;
    }

    public List<Integer> obtenerPuntuacionesManos() {
        return puntuacionesManos;
    }

    public void modificarPuntuacionesManos(List<Integer> puntuacionesManos) {
        this.puntuacionesManos = puntuacionesManos;
    }

    public List<String> obtenerEstadosManos() {
        return estadosManos;
    }

    public void modificarEstadosManos(List<String> estadosManos) {
        this.estadosManos = estadosManos;
    }

    public List<ApuestaDTO> obtenerApuestasManos() {
        return apuestasManos;
    }

    public void modificarApuestasManos(List<ApuestaDTO> apuestasManos) {
        this.apuestasManos = apuestasManos;
    }

    public List<CartaDTO> obtenerManoCrupier() {
        return manoCrupier;
    }

    public void modificarManoCrupier(List<CartaDTO> manoCrupier) {
        this.manoCrupier = manoCrupier;
    }

    public LocalDateTime obtenerFechaInicio() {
        return fechaInicio;
    }

    public void modificarFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime obtenerFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void modificarFechaFinalizacion(LocalDateTime fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public EstadoPartida obtenerEstado() {
        return estado;
    }

    public void modificarEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    public Resultado obtenerResultado() {
        return resultado;
    }

    public void modificarResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public int obtenerSaldoFinal() {
        return saldoFinal;
    }

    public void modificarSaldoFinal(int saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
}
