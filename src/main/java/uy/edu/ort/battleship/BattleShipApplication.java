package uy.edu.ort.battleship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uy.edu.ort.battleship.modelo.BatallaException;
import uy.edu.ort.battleship.modelo.DatosPrueba;

@SpringBootApplication
public class BattleShipApplication {

    public static void main(String[] args) throws BatallaException {
        DatosPrueba.cargarDatos();
        SpringApplication.run(BattleShipApplication.class, args);
    }
}