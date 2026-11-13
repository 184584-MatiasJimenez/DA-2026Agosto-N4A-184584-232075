package uy.edu.ort.battleship.modelo;

import lombok.Getter;

public class Disparo {

    @Getter
    private int fila;

    @Getter
    private int columna;

    @Getter
    private ResultadoDisparo resultado;

    public Disparo(int fila, int columna, ResultadoDisparo resultado) {
        this.fila = fila;
        this.columna = columna;
        this.resultado = resultado;
    }
}