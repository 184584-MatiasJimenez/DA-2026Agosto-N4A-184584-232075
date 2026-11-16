package uy.edu.ort.battleship.modelo;

public class TacticaEspiral implements Tactica {

    @Override
    public String getNombre() {
        return "Espiral";
    }

    @Override
    public Celda elegirDisparo(Oceano oceano) throws BatallaException {
        int fila = oceano.getFilas() / 2;
        int columna = (oceano.getColumnas() - 1) / 2;

        if (!oceano.getCelda(fila, columna).fueDisparada()) {
            return oceano.getCelda(fila, columna);
        }

        int[] cambioFila = {0, -1, 0, 1};
        int[] cambioColumna = {1, 0, -1, 0};
        int pasosPorTramo = 1;
        int limite = 2 * Math.max(oceano.getFilas(), oceano.getColumnas());

        while (pasosPorTramo <= limite) {
            for (int direccion = 0; direccion < 4; direccion++) {
                for (int paso = 0; paso < pasosPorTramo; paso++) {
                    fila += cambioFila[direccion];
                    columna += cambioColumna[direccion];

                    if (fila >= 0 && fila < oceano.getFilas() && columna >= 0 && columna < oceano.getColumnas()) {
                        Celda celda = oceano.getCelda(fila, columna);
                        if (!celda.fueDisparada()) {
                            return celda;
                        }
                    }
                }

                if (direccion % 2 == 1) {
                    pasosPorTramo++;
                }
            }
        }

        throw new BatallaException("No quedan celdas disponibles para disparar");
    }
}