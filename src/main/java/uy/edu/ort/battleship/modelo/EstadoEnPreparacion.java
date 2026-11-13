package uy.edu.ort.battleship.modelo;

public class EstadoEnPreparacion extends EstadoBatalla {

    @Override
    public String getNombre() {
        return "En preparación";
    }

    @Override
public void ubicarBarcoPropio(Batalla batalla, Barco barco,int fila, int columna,Orientacion orientacion) throws BatallaException {
    batalla.ubicarBarcoPropioEnPreparacion(barco, fila, columna, orientacion);
}

@Override
public void iniciarCombate(Batalla batalla) throws BatallaException {
    batalla.iniciarCombateEnPreparacion();
}

@Override
public void agregarTipoBarco(Batalla batalla, TipoBarco tipo, int cantidad)throws BatallaException {
    batalla.agregarTipoBarcoEnPreparacion(tipo, cantidad);
}
}