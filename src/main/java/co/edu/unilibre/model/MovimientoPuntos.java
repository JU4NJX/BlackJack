
package co.edu.unilibre.model;

import co.edu.unilibre.model.enums.TipoMovimiento;
import java.time.LocalDateTime;

public class MovimientoPuntos {

    private int id;
    private int idUsuario;
    private TipoMovimiento tipo;
    private double cantidad;
    private LocalDateTime fecha;

    public MovimientoPuntos(int id, int idUsuario,
                            TipoMovimiento tipo, double cantidad,
                            LocalDateTime fecha) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    // Métodos de encapsulamiento

    public int obtenerId() {
        return id;
    }

    public void modificarId(int id) {
        this.id = id;
    }

    public int obtenerIdUsuario() {
        return idUsuario;
    }

    public void modificarIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public TipoMovimiento obtenerTipo() {
        return tipo;
    }

    public void modificarTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public double obtenerCantidad() {
        return cantidad;
    }

    public void modificarCantidad(double cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La cantidad no puede ser negativa."
            );
        }
        this.cantidad = cantidad;
    }

    public LocalDateTime obtenerFecha() {
        return fecha;
    }

    public void modificarFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
