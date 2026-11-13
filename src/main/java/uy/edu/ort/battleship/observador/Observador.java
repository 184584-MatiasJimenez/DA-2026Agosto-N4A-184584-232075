package uy.edu.ort.battleship.observador;

public interface Observador {
    void actualizar(Observable origen, Object evento);
}