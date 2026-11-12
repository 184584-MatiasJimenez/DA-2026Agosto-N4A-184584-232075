package uy.edu.ort.battlefield;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import uy.edu.ort.battleshipdemo.modelo.*;
import uy.edu.ort.battleshipdemo.BattleShipApplication;

public class OceanoTest {

    @Test
    void ubicaUnBarcoHorizontal() throws BatallaException {
        Oceano oceano = new Oceano(6, 6);
        Barco barco = new Barco(new TipoBarco("Crucero", 3));

        oceano.ubicarBarco(barco, 2, 1, Orientacion.HORIZONTAL);

        assertTrue(oceano.getCelda(2, 1).estaOcupada());
        assertTrue(oceano.getCelda(2, 2).estaOcupada());
        assertTrue(oceano.getCelda(2, 3).estaOcupada());
        assertFalse(oceano.getCelda(2, 4).estaOcupada());
    }

    @Test
    void rechazaUnBarcoQueSaleDelOceano() {
        Oceano oceano = new Oceano(6, 6);
        Barco barco = new Barco(new TipoBarco("Crucero", 3));

        assertThrows(BatallaException.class,
                () -> oceano.ubicarBarco(barco, 5, 4, Orientacion.HORIZONTAL));

        assertFalse(oceano.getCelda(5, 4).estaOcupada());
    }
}