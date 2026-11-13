package uy.edu.ort.battleship.modelo;

public interface Tactica {

    String getNombre();

    Celda elegirDisparo(Oceano oceanoObjetivo) throws BatallaException;
}