package co.edu.unilibre.dao;

import co.edu.unilibre.model.enums.TipoMovimiento;

import java.sql.Connection;
import java.util.List;

public class MovimientoPuntosDAO {

    public void registrar(Connection conexion, TipoMovimiento tipo, int cantidad,Long idPartida){
        return;
    }

    public List<TipoMovimiento> listarPorUsuario(Long idUsuario){
        return null;
    }

    public List<TipoMovimiento> listarPorPartida(Long idPartida){
        return null;
    }

}
