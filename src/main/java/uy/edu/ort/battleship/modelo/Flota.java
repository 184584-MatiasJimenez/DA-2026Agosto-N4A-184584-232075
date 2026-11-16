package uy.edu.ort.battleship.modelo;

import java.util.ArrayList;
import java.util.List;

public class Flota {

    private List<Barco> barcos = new ArrayList<>();

    private void agregarBarco(Barco barco) {
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

    public void validarCantidad(TipoBarco tipo, int cantidad) throws BatallaException {
        if (tipo == null) {
            throw new BatallaException("Debe indicar un tipo de barco");
        }
        if (cantidad < 0 || cantidad > 6) {
            throw new BatallaException("La cantidad de " + tipo.getNombre() + " debe estar entre 0 y 6");
        }

        int existentes = 0;
        for (Barco barco : barcos) {
            if (barco.getTipo().getNombre().equals(tipo.getNombre())) {
                existentes++;
            }
        }

        if (existentes + cantidad > 6) {
            throw new BatallaException("No puede haber más de 6 barcos de un mismo tipo");
        }
    }

    public void agregarBarcos(TipoBarco tipo, int cantidad) throws BatallaException {
        validarCantidad(tipo, cantidad);

        for (int i = 0; i < cantidad; i++) {
            agregarBarco(new Barco(tipo));
        }
    }
}