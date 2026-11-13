package uy.edu.ort.battleship.modelo;

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

    public boolean recibirImpacto() throws BatallaException {
    if (estaHundido()) {
        throw new BatallaException("El barco ya está hundido");
    }

    partesSanas--;
    return estaHundido();
    }
}