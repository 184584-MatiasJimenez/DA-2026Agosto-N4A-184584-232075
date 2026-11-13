package uy.edu.ort.battleship.modelo;

import java.util.List;

public class FabricaTacticas {

    public Tactica crear(String nombre) throws BatallaException {
        if ("Secuencial".equals(nombre)) {
            return new TacticaSecuencial();
        }
        if ("Espiral".equals(nombre)) {
            return new TacticaEspiral();
        }
        if ("Caza y ataque".equals(nombre)) {
            return new TacticaCazaYAtaque();
        }

        throw new BatallaException("La táctica indicada no existe");
    }

    public List<Tactica> getTacticasDisponibles() {
    return List.of(
            new TacticaSecuencial(),
            new TacticaEspiral(),
            new TacticaCazaYAtaque());
}
}