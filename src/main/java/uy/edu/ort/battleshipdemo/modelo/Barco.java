package uy.edu.ort.battleshipdemo.modelo;

import lombok.Getter;

public class Barco {

    @Getter
    private TipoBarco tipo;

    @Getter
    private int partesSanas;

    public Barco(TipoBarco tipo) {
        this.tipo = tipo;
        this.partesSanas = tipo.getTamano();
    }

    public boolean estaHundido() {
        return partesSanas == 0;
    }
}