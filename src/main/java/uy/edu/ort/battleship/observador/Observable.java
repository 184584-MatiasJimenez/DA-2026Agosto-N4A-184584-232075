package uy.edu.ort.battleship.observador;

import java.util.ArrayList;
import java.util.List;

public class Observable {

    private final List<Observador> observadores = new ArrayList<>();

    public synchronized void agregarObservador(Observador observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public synchronized void quitarObservador(Observador observador) {
        observadores.remove(observador);
    }

    public void avisar(Object evento) {
        List<Observador> copia;

        synchronized (this) {
            copia = new ArrayList<>(observadores);
        }

        for (Observador observador : copia) {
            observador.actualizar(this, evento);
        }
    }
}