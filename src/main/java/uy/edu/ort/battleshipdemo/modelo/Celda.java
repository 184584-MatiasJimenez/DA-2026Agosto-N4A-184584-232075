package uy.edu.ort.battleshipdemo.modelo;

import lombok.Getter;

public class Celda {

    @Getter
    private int fila;

    @Getter
    private int columna;

    private Barco barco;
    private boolean disparada;

    public Celda(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public boolean estaOcupada() {
        return barco != null;
    }

    public boolean fueDisparada() {
        return disparada;
    }

    public void ubicarBarco(Barco barco) {
    this.barco = barco;
    }

    public boolean contieneBarco(Barco barco) {
    return this.barco == barco;
    }
}