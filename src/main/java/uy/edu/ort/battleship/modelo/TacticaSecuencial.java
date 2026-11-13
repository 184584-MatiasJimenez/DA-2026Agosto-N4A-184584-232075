package uy.edu.ort.battleship.modelo;

public class TacticaSecuencial implements Tactica {

    @Override
    public String getNombre() {
        return "Secuencial";
    }

    @Override
    public Celda elegirDisparo(Oceano oceanoObjetivo) throws BatallaException {
        for (int fila = 0; fila < oceanoObjetivo.getFilas(); fila++) {
            for (int columna = 0; columna < oceanoObjetivo.getColumnas(); columna++) {
                Celda celda = oceanoObjetivo.getCelda(fila, columna);

                if (!celda.fueDisparada()) {
                    return celda;
                }
            }
        }

        throw new BatallaException("No quedan celdas disponibles para disparar");
    }
}