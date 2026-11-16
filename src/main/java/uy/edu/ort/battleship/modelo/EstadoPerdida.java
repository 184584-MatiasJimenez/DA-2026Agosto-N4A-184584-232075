package uy.edu.ort.battleship.modelo;

public class EstadoPerdida extends EstadoBatalla {

    @Override
    public String getNombre() {
        return "Perdida";
    }

    @Override
    public boolean revelaFlotaEnemiga() {
        return true;
    }

    @Override
    public boolean estaFinalizada() {
        return true;
    }
}