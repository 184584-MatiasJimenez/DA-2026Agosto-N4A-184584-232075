package uy.edu.ort.battleship.modelo;

public class EstadoGanada extends EstadoBatalla {

    @Override
    public String getNombre() {
        return "Ganada";
    }

    @Override
    public boolean estaFinalizada() {
        return true;
    }
}