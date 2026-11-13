package uy.edu.ort.battleship.presentadores;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import uy.edu.ort.battleship.dtos.ResumenBatallaDto;
import uy.edu.ort.battleship.modelo.Batalla;
import uy.edu.ort.battleship.modelo.Fachada;

@RestController
@Scope("session")
public class SalaOperacionesPresentador {

    private final Fachada fachada;

    public SalaOperacionesPresentador(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping("/sala/vistaConectada")
    public Commands vistaConectada() {
        List<Batalla> batallas = fachada.getBatallas();
        List<ResumenBatallaDto> resumenes = new ArrayList<>();
        int ganadas = 0;
        int perdidas = 0;

        for (Batalla batalla : batallas) {
            resumenes.add(new ResumenBatallaDto(batalla));

            if (batalla.getNombreEstado().equals("Ganada")) {
                ganadas++;
            } else if (batalla.getNombreEstado().equals("Perdida")) {
                perdidas++;
            }
        }

        int exito = batallas.isEmpty() ? 0 : ganadas * 100 / batallas.size();

        return Commands.create(
                new Command("mostrarBatallas", resumenes),
                new Command("mostrarGanadas", ganadas),
                new Command("mostrarPerdidas", perdidas),
                new Command("mostrarExito", exito));
    }
}