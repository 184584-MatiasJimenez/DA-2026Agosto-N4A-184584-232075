package uy.edu.ort.battleship.modelo;

import lombok.Getter;

public class Oceano {

    @Getter
    private int filas;

    @Getter
    private int columnas;

    private Celda[][] celdas;

    public Oceano(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.celdas = new Celda[filas][columnas];

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                celdas[fila][columna] = new Celda(fila, columna);
            }
        }
    }

    public Celda getCelda(int fila, int columna) {
        return celdas[fila][columna];
    }

    public void ubicarBarco(Barco barco, int filaProa, int columnaProa,Orientacion orientacion) throws BatallaException {
    if (barco == null || orientacion == null) {
        throw new BatallaException("Debe indicar un barco y su orientación");
    }
     if (barcoYaUbicado(barco)) {
        throw new BatallaException("Este barco ya fue ubicado");
    }

    int tamano = barco.getTipo().getTamano();

    // Primero validamos el barco completo.
    for (int i = 0; i < tamano; i++) {
        int fila = filaProa + (orientacion == Orientacion.VERTICAL ? i : 0);
        int columna = columnaProa + (orientacion == Orientacion.HORIZONTAL ? i : 0);

        if (!estaDentro(fila, columna)) {
            throw new BatallaException("El barco queda fuera del océano");
        }
        if (hayBarcoCerca(fila, columna)) {
            throw new BatallaException("El barco se superpone o toca otro barco");
        }
    }

    // Solo después de validar, ocupamos las celdas.
    for (int i = 0; i < tamano; i++) {
        int fila = filaProa + (orientacion == Orientacion.VERTICAL ? i : 0);
        int columna = columnaProa + (orientacion == Orientacion.HORIZONTAL ? i : 0);
        celdas[fila][columna].ubicarBarco(barco);
    }
}

private boolean estaDentro(int fila, int columna) {
    return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
}

private boolean hayBarcoCerca(int fila, int columna) {
    for (int otraFila = fila - 1; otraFila <= fila + 1; otraFila++) {
        for (int otraColumna = columna - 1; otraColumna <= columna + 1; otraColumna++) {
            if (estaDentro(otraFila, otraColumna)
                    && celdas[otraFila][otraColumna].estaOcupada()) {
                return true;
            }
        }
    }
    return false;
}

private boolean barcoYaUbicado(Barco barco) {
    for (int fila = 0; fila < filas; fila++) {
        for (int columna = 0; columna < columnas; columna++) {
            if (celdas[fila][columna].contieneBarco(barco)) {
                return true;
            }
        }
    }
    return false;
}

public ResultadoDisparo disparar(int fila, int columna) throws BatallaException {
    if (!estaDentro(fila, columna)) {
        throw new BatallaException("El disparo queda fuera del océano");
    }

    return celdas[fila][columna].disparar();
}
}