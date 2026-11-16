package uy.edu.ort.battleship.presentadores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import uy.edu.ort.battleship.dtos.NuevaBatallaDto;
import uy.edu.ort.battleship.modelo.Batalla;
import uy.edu.ort.battleship.modelo.BatallaException;
import uy.edu.ort.battleship.modelo.Fachada;
import uy.edu.ort.battleship.modelo.Tactica;

@RestController
@Scope("session")
public class NuevaBatallaPresentador {

    private final Fachada fachada;

    public NuevaBatallaPresentador(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping("/nueva-batalla/vistaConectada")
    public Commands vistaConectada() {
        List<String> nombresTacticas = new ArrayList<>();

        for (Tactica tactica : fachada.getTacticas()) {
            nombresTacticas.add(tactica.getNombre());
        }

        return Commands.create(new Command("mostrarTacticas", nombresTacticas), new Command("mostrarTiposBarco", fachada.getTiposBarco()));
    }

    @PostMapping("/nueva-batalla/continuar")
    public Commands continuar(@ModelAttribute NuevaBatallaDto datos) throws BatallaException {
        Map<String, Integer> cantidades = new HashMap<>();
        cantidades.put("Portaaviones", datos.getPortaaviones());
        cantidades.put("Acorazado", datos.getAcorazados());
        cantidades.put("Crucero", datos.getCruceros());
        cantidades.put("Submarino", datos.getSubmarinos());
        cantidades.put("Destructor", datos.getDestructores());

        Batalla batalla = fachada.crearBatalla(
            datos.getLugar(),
            datos.getFilas(),
            datos.getColumnas(),
            datos.getTactica(),
            cantidades
        );

        return Commands.create(new Command("irADespliegue", batalla.getLugar()));
    }
}