package uy.edu.ort.battleship.modelo;

public class EstadoEnCombate extends EstadoBatalla {

    @Override
    public String getNombre() {
        return "En combate";
    }

    @Override
public Ronda realizarRonda(Batalla batalla, int fila, int columna)throws BatallaException {
    return batalla.realizarRondaEnCombate(fila, columna);
}
}