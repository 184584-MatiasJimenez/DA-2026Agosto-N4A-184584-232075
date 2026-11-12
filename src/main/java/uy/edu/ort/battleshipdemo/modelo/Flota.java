package uy.edu.ort.battleshipdemo.modelo;

import java.util.ArrayList;
import java.util.List;

public class Flota {

    private List<Barco> barcos = new ArrayList<>();

    public void agregarBarco(Barco barco) {
        barcos.add(barco);
    }

    public List<Barco> getBarcos() {
    return new ArrayList<>(barcos);
    }

    public int getPartesSanas() {
        int total = 0;
        for (Barco barco : barcos) {
            total += barco.getPartesSanas();
        }
        return total;
    }

    public int getTamanoTotal() {
        int total = 0;
        for (Barco barco : barcos) {
            total += barco.getTipo().getTamano();
        }
        return total;
    }

    public boolean estaHundida() {
    return !barcos.isEmpty() && getPartesSanas() == 0;
    }
}