package uy.edu.ort.battleship.presentadores;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uy.edu.ort.battleship.dtos.InicializarDto;
import uy.edu.ort.battleship.dtos.ValorDto;
import uy.edu.ort.battleship.modelo.Barco;
import uy.edu.ort.battleship.modelo.Batalla;
import uy.edu.ort.battleship.modelo.BatallaException;
import uy.edu.ort.battleship.modelo.Celda;
import uy.edu.ort.battleship.modelo.Fachada;
import uy.edu.ort.battleship.modelo.Orientacion;

@RestController
@Scope("session")
public class BatallaPresentador {

    private final Fachada fachada;
    private Batalla batallaActual;
    private Orientacion orientacionSeleccionada = Orientacion.HORIZONTAL;

    public BatallaPresentador(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping("/batalla/vistaConectada")
    public Commands vistaConectada(@RequestParam String lugar)
            throws BatallaException {

        batallaActual = fachada.buscarBatalla(lugar);
        orientacionSeleccionada = Orientacion.HORIZONTAL;

        InicializarDto tablero = new InicializarDto(
                batallaActual.getOceanoPropio().getFilas(),
                batallaActual.getOceanoPropio().getColumnas(),
                "/batalla/ubicarBarco",
                "/batalla/disparar");

        Commands respuesta = Commands.create(
                new Command("mostrarLugar", batallaActual.getLugar()),
                new Command("mostrarEstado", batallaActual.getNombreEstado()),
                new Command("inicializar", tablero),
                barcosDisponibles(batallaActual));

        for (Command comando : barcosEnTablero(batallaActual)) {
            respuesta.add(comando);
        }

        return respuesta;
    }

    @PostMapping("/batalla/seleccionarOrientacion")
    public Commands seleccionarOrientacion(
            @RequestParam Orientacion orientacion) {
        orientacionSeleccionada = orientacion;
        return Commands.create();
    }

    @PostMapping("/batalla/ubicarBarco")
    public Commands ubicarBarco(
            @RequestParam int coordenadaVertical,
            @RequestParam int coordenadaHorizontal)
            throws BatallaException {

        if (batallaActual == null) {
            throw new BatallaException("Debe entrar a una batalla");
        }

        List<Barco> disponibles = batallaActual.getBarcosPropiosSinUbicar();
        if (disponibles.isEmpty()) {
            throw new BatallaException("Todos los barcos ya fueron ubicados");
        }

        Barco siguiente = disponibles.get(0);
        batallaActual.ubicarBarcoPropio(
                siguiente,
                coordenadaVertical,
                coordenadaHorizontal,
                orientacionSeleccionada);

        Commands respuesta = Commands.create(
                barcosDisponibles(batallaActual));

        for (Command comando : barcosEnTablero(batallaActual)) {
            respuesta.add(comando);
        }

        return respuesta;
    }
    
    @PostMapping("/batalla/iniciarCombate")
public Commands iniciarCombate() throws BatallaException {
    if (batallaActual == null) {
        throw new BatallaException("Debe entrar a una batalla");
    }

    batallaActual.iniciarCombate();

    InicializarDto tablero = new InicializarDto(
            batallaActual.getOceanoPropio().getFilas(),
            batallaActual.getOceanoPropio().getColumnas(),
            "/batalla/ubicarBarco",
            "/batalla/disparar");

    Commands respuesta = Commands.create(
            new Command("mostrarEstado", batallaActual.getNombreEstado()),
            new Command("inicializar", tablero));

    for (Command comando : barcosEnTablero(batallaActual)) {
        respuesta.add(comando);
    }

    return respuesta;
}

    private Command barcosDisponibles(Batalla batalla) {
        List<String> nombres = new ArrayList<>();

        for (Barco barco : batalla.getBarcosPropiosSinUbicar()) {
            nombres.add(barco.getTipo().getNombre());
        }

        return new Command("mostrarBarcosDisponibles", nombres);
    }

    private List<Command> barcosEnTablero(Batalla batalla) {
        List<Command> comandos = new ArrayList<>();

        for (Celda celda : batalla.getOceanoPropio().getCeldasConBarco()) {
            comandos.add(new Command(
                    "mostrarAguasPropias",
                    new ValorDto(
                            celda.getFila(),
                            celda.getColumna(),
                            "barco")));
        }

        return comandos;
    }
}