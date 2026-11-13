package uy.edu.ort.battleship.modelo;

import lombok.Getter;

public class Ronda {

    @Getter
    private int numero;

    @Getter
    private Disparo disparoHumano;

    @Getter
    private Disparo disparoMaquina;

    public Ronda(int numero, Disparo disparoHumano, Disparo disparoMaquina) {
        this.numero = numero;
        this.disparoHumano = disparoHumano;
        this.disparoMaquina = disparoMaquina;
    }
}