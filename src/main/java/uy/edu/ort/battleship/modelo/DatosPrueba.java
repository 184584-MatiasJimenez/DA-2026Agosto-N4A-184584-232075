package uy.edu.ort.battleship.modelo;

import java.util.Map;

public class DatosPrueba {

    public static void cargarDatos() throws BatallaException {
        Fachada fachada = Fachada.getInstancia();

        cargarBatallaEnPreparacion(fachada);
        cargarBatallaEnCombate(fachada);
        cargarBatallaGanada(fachada);
        cargarBatallaPerdida(fachada);
    }

    private static void cargarBatallaEnPreparacion(Fachada fachada)
            throws BatallaException {
        fachada.crearBatalla(
                "Atlantico", 8, 8, "Secuencial",
                Map.of("Crucero", 1, "Submarino", 1));
    }

    private static void cargarBatallaEnCombate(Fachada fachada)
            throws BatallaException {
        Batalla pacifico = fachada.crearBatalla(
                "Pacifico", 8, 8, "Espiral",
                Map.of("Crucero", 1, "Submarino", 1));

        Barco crucero = pacifico.getFlotaPropia().getBarcos().get(0);
        Barco submarino = pacifico.getFlotaPropia().getBarcos().get(1);

        pacifico.ubicarBarcoPropio(
                crucero, 0, 0, Orientacion.HORIZONTAL);
        pacifico.ubicarBarcoPropio(
                submarino, 3, 0, Orientacion.HORIZONTAL);

        pacifico.iniciarCombate();
    }

    private static void cargarBatallaGanada(Fachada fachada)
            throws BatallaException {
        Batalla indico = fachada.crearBatalla(
                "Indico", 8, 8, "Secuencial",
                Map.of("Crucero", 1, "Submarino", 1));

        Barco crucero = indico.getFlotaPropia().getBarcos().get(0);
        Barco submarino = indico.getFlotaPropia().getBarcos().get(1);

        indico.ubicarBarcoPropio(
                crucero, 7, 0, Orientacion.HORIZONTAL);
        indico.ubicarBarcoPropio(
                submarino, 7, 5, Orientacion.HORIZONTAL);

        indico.iniciarCombate();

        for (Celda celda : indico.getOceanoEnemigo().getCeldasConBarco()) {
            indico.realizarRonda(celda.getFila(), celda.getColumna());
        }
    }

    private static void cargarBatallaPerdida(Fachada fachada)
            throws BatallaException {
        Batalla artico = fachada.crearBatalla(
                "Artico", 8, 8, "Secuencial",
                Map.of("Crucero", 1, "Submarino", 1));

        Barco crucero = artico.getFlotaPropia().getBarcos().get(0);
        Barco submarino = artico.getFlotaPropia().getBarcos().get(1);

        artico.ubicarBarcoPropio(
                crucero, 0, 0, Orientacion.HORIZONTAL);
        artico.ubicarBarcoPropio(
                submarino, 0, 5, Orientacion.HORIZONTAL);

        artico.iniciarCombate();

        for (int fila = 0;
                fila < 8 && !artico.getFlotaPropia().estaHundida();
                fila++) {
            for (int columna = 0;
                    columna < 8 && !artico.getFlotaPropia().estaHundida();
                    columna++) {

                Celda objetivo = artico.getOceanoEnemigo()
                        .getCelda(fila, columna);

                if (!objetivo.estaOcupada()) {
                    artico.realizarRonda(fila, columna);
                }
            }
        }
    }
}