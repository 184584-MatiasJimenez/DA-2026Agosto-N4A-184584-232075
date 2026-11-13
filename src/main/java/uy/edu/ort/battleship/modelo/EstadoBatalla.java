package uy.edu.ort.battleship.modelo;

public abstract class EstadoBatalla {

    public abstract String getNombre();

    public void ubicarBarcoPropio(Batalla batalla, Barco barco,int fila, int columna,Orientacion orientacion) throws BatallaException {
        throw new BatallaException("No se pueden ubicar barcos en este estado");
    }

    public void iniciarCombate(Batalla batalla) throws BatallaException {
    throw new BatallaException("No se puede iniciar el combate en este estado");
}

public void agregarTipoBarco(Batalla batalla, TipoBarco tipo, int cantidad)throws BatallaException {
    throw new BatallaException("No se puede modificar la flota en este estado");
}

public Ronda realizarRonda(Batalla batalla, int fila, int columna)throws BatallaException {
    throw new BatallaException("No se puede disparar en este estado");
}
}