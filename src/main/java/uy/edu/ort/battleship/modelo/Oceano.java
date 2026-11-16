package uy.edu.ort.battleship.modelo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        validarLugarParaBarco(tamano, filaProa, columnaProa, orientacion);

        // Ocupamos las celdas solo después de validar todo el barco.
        for (int i = 0; i < tamano; i++) {
            int fila = filaProa + (orientacion == Orientacion.VERTICAL ? i : 0);
            int columna = columnaProa + (orientacion == Orientacion.HORIZONTAL ? i : 0);
            celdas[fila][columna].ubicarBarco(barco);
        }
    }

    private void validarLugarParaBarco(int tamano, int filaProa, int columnaProa, Orientacion orientacion) throws BatallaException {
        String error = errorDeUbicacion(tamano, filaProa, columnaProa, orientacion);
        if (error != null) {
            throw new BatallaException(error);
        }
    }

    private String errorDeUbicacion(int tamano, int filaProa,int columnaProa, Orientacion orientacion) {
        for (int i = 0; i < tamano; i++) {
            int fila = filaProa + (orientacion == Orientacion.VERTICAL ? i : 0);
            int columna = columnaProa + (orientacion == Orientacion.HORIZONTAL ? i : 0);
        
            if (!estaDentro(fila, columna)) {
                return "El barco queda fuera del océano";
            }
            if (hayBarcoCerca(fila, columna)) {
                return "El barco se superpone o toca otro barco";
            }
        }
        return null; // No hay error: el lugar está disponible.
    }

    private boolean estaDentro(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }

    private boolean hayBarcoCerca(int fila, int columna) {
        for (int otraFila = fila - 1; otraFila <= fila + 1; otraFila++) {
            for (int otraColumna = columna - 1; otraColumna <= columna + 1; otraColumna++) {
                if (estaDentro(otraFila, otraColumna) && celdas[otraFila][otraColumna].estaOcupada()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean barcoYaUbicado(Barco barco) {
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

    public void ubicarBarcoAleatorio(Barco barco) throws BatallaException {
        if (barco == null || barcoYaUbicado(barco)) {
            throw new BatallaException("El barco no es válido o ya fue ubicado");
        }

        List<int[]> opciones = new ArrayList<>();

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                opciones.add(new int[] {fila, columna, 0}); // Horizontal
                opciones.add(new int[] {fila, columna, 1}); // Vertical
            }
        }

        Collections.shuffle(opciones);

        for (int[] opcion : opciones) {
            Orientacion orientacion = opcion[2] == 0? Orientacion.HORIZONTAL : Orientacion.VERTICAL;

            if (errorDeUbicacion(barco.getTipo().getTamano(), opcion[0], opcion[1], orientacion) == null) {
                ubicarBarco(barco, opcion[0], opcion[1], orientacion);
                return;
            }
        }

        throw new BatallaException("No hay lugar para ubicar este barco");
    }

    public List<Celda> getCeldasConBarco() {
        List<Celda> ocupadas = new ArrayList<>();
    
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Celda celda = celdas[fila][columna];
                if (celda.estaOcupada()) {
                    ocupadas.add(celda);
                }
            }
        }
    
        return ocupadas;
    }
}