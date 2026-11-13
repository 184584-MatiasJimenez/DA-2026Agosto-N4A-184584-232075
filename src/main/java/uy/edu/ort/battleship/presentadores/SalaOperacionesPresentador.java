package uy.edu.ort.battleship.presentadores;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import uy.edu.ort.battleship.dtos.ResumenBatallaDto;
import uy.edu.ort.battleship.modelo.Batalla;
import uy.edu.ort.battleship.modelo.Fachada;
import uy.edu.ort.battleship.observador.Observable;
import uy.edu.ort.battleship.observador.Observador;

@RestController
@Scope("session")
public class SalaOperacionesPresentador implements Observador {

    private final Fachada fachada;
    private final ConexionNavegador conexionNavegador;

    public SalaOperacionesPresentador(Fachada fachada, ConexionNavegador conexionNavegador) {
        this.fachada = fachada;
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/sala/registrarSSE",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE() {
        return conexionNavegador.conectarSSE();
    }

    @PostMapping("/sala/vistaConectada")
    public Commands vistaConectada() {
        fachada.agregarObservador(this);
        return resumenSala();
    }

    @PostMapping("/sala/vistaDesconectada")
    public void vistaDesconectada() {
        fachada.quitarObservador(this);
    }

    @Override
    public void actualizar(Observable origen, Object evento) {
        if (evento == Fachada.Evento.BATALLA_CREADA || evento == Fachada.Evento.BATALLA_ACTUALIZADA) {
            conexionNavegador.enviar(resumenSala());
        }
    }

    private Commands resumenSala() {
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

        int exito = batallas.isEmpty()
                ? 0
                : ganadas * 100 / batallas.size();

        return Commands.create(
                new Command("mostrarBatallas", resumenes),
                new Command("mostrarGanadas", ganadas),
                new Command("mostrarPerdidas", perdidas),
                new Command("mostrarExito", exito));
    }
}