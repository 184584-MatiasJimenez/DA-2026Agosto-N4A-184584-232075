package uy.edu.ort.battleship.modelo;

import lombok.Getter;

public class Celda {

    @Getter
    private int fila;

    @Getter
    private int columna;

    private Barco barco;
    private boolean disparada;
    private ResultadoDisparo resultado;

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

        disparada = true;

        if (barco == null) {
            resultado = ResultadoDisparo.AGUA;
        } else if (barco.recibirImpacto()) {
            resultado = ResultadoDisparo.HUNDIDO;
        } else {
            resultado = ResultadoDisparo.IMPACTO;
        }

        return resultado;
    }

    public ResultadoDisparo getResultado() {
        return resultado;
    }

    public boolean tieneImpactoPendiente() {
        return disparada && barco != null && !barco.estaHundido();
    }

    public int getTamanoBarcoHundido() {
        if (resultado != ResultadoDisparo.HUNDIDO) {
            return 0;
        }
        return barco.getTipo().getTamano();
    }
}