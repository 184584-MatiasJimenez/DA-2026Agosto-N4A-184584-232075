package uy.edu.ort.battleship.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TacticaCazaYAtaque implements Tactica {

    @Override
    public String getNombre() {
        return "Caza y ataque";
    }

    @Override
    public Celda elegirDisparo(Oceano oceano) throws BatallaException {
        Celda objetivo = buscarJuntoAImpacto(oceano);

        if (objetivo != null) {
            return objetivo;
        }

        return buscarCeldaAleatoria(oceano);
    }

    private Celda buscarJuntoAImpacto(Oceano oceano) {
        Celda enLinea = buscarEnLinea(oceano);
        if (enLinea != null) {
            return enLinea;
        }
        for (int fila = 0; fila < oceano.getFilas(); fila++) {
            for (int columna = 0; columna < oceano.getColumnas(); columna++) {
                Celda celda = oceano.getCelda(fila, columna);

                if (celda.tieneImpactoPendiente()) {
                    Celda vecina = primeraVecinaDisponible(oceano, fila, columna);
                    if (vecina != null) {
                        return vecina;
                    }
                }
            }
        }

        return null;
    }

    private Celda primeraVecinaDisponible(Oceano oceano, int fila, int columna) {
        int[][] direcciones = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}
        };

        for (int[] direccion : direcciones) {
            int nuevaFila = fila + direccion[0];
            int nuevaColumna = columna + direccion[1];

            if (nuevaFila >= 0 && nuevaFila < oceano.getFilas() && nuevaColumna >= 0 && nuevaColumna < oceano.getColumnas()) {
                Celda vecina = oceano.getCelda(nuevaFila, nuevaColumna);

                if (!vecina.fueDisparada()) {
                    return vecina;
                }
            }
        }

        return null;
    }

    private Celda buscarCeldaAleatoria(Oceano oceano) throws BatallaException {
        List<Celda> disponibles = new ArrayList<>();

        for (int fila = 0; fila < oceano.getFilas(); fila++) {
            for (int columna = 0; columna < oceano.getColumnas(); columna++) {
                Celda celda = oceano.getCelda(fila, columna);
                if (!celda.fueDisparada()) {
                    disponibles.add(celda);
                }
            }
        }

        if (disponibles.isEmpty()) {
            throw new BatallaException("No quedan celdas disponibles para disparar");
        }

        Collections.shuffle(disponibles);
        return disponibles.get(0);
    }

    private Celda buscarEnLinea(Oceano oceano) {
    for (int fila = 0; fila < oceano.getFilas(); fila++) {
        for (int columna = 0; columna < oceano.getColumnas(); columna++) {
            if (!oceano.getCelda(fila, columna).tieneImpactoPendiente()) {
                continue;
            }

            if (columna + 1 < oceano.getColumnas()
                    && oceano.getCelda(fila, columna + 1).tieneImpactoPendiente()) {
                Celda siguiente = celdaDisponible(oceano, fila, columna + 2);
                if (siguiente != null) {
                    return siguiente;
                }

                Celda anterior = celdaDisponible(oceano, fila, columna - 1);
                if (anterior != null) {
                    return anterior;
                }
            }

            if (fila + 1 < oceano.getFilas()
                    && oceano.getCelda(fila + 1, columna).tieneImpactoPendiente()) {
                Celda siguiente = celdaDisponible(oceano, fila + 2, columna);
                if (siguiente != null) {
                    return siguiente;
                }

                Celda anterior = celdaDisponible(oceano, fila - 1, columna);
                if (anterior != null) {
                    return anterior;
                }
            }
        }
    }

    return null;
}

private Celda celdaDisponible(Oceano oceano, int fila, int columna) {
    if (fila < 0 || fila >= oceano.getFilas()
            || columna < 0 || columna >= oceano.getColumnas()) {
        return null;
    }

    Celda celda = oceano.getCelda(fila, columna);
    return celda.fueDisparada() ? null : celda;
}
}