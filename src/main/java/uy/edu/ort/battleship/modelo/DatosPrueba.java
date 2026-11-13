package uy.edu.ort.battleship.modelo;

import java.util.Map;

public class DatosPrueba {

    public static void cargarDatos() throws BatallaException {
        Fachada fachada = Fachada.getInstancia();

        fachada.crearBatalla(
                "Atlantico", 8, 8, "Secuencial",
                Map.of("Crucero", 1, "Submarino", 1));
    }
}