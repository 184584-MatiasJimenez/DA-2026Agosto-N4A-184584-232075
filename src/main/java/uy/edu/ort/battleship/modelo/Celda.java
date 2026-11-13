package uy.edu.ort.battleship.modelo;

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

    public ResultadoDisparo disparar() throws BatallaException {
    if (disparada) {
        throw new BatallaException("No se puede disparar dos veces a la misma celda");
    }

    if (barco == null) {
        disparada = true;
        return ResultadoDisparo.AGUA;
    }

    boolean hundido = barco.recibirImpacto();
    disparada = true;

    if (hundido) {
        return ResultadoDisparo.HUNDIDO;
    }
    return ResultadoDisparo.IMPACTO;
    }
}