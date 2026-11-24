package co.edu.unilibre.service;

public interface Sujeto {
    void agregarObservador(Observador observador);
    void eliminarObservador(Observador observador);
    void notificarObservadores();
}