package uy.edu.ort.battleship.presentadores;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import uy.edu.ort.battleship.dtos.InicializarDto;
import uy.edu.ort.battleship.dtos.ValorDto;
import uy.edu.ort.battleship.modelo.Barco;
import uy.edu.ort.battleship.modelo.Batalla;
import uy.edu.ort.battleship.modelo.BatallaException;
import uy.edu.ort.battleship.modelo.Celda;
import uy.edu.ort.battleship.modelo.Disparo;
import uy.edu.ort.battleship.modelo.Fachada;
import uy.edu.ort.battleship.modelo.Orientacion;
import uy.edu.ort.battleship.modelo.ResultadoDisparo;
import uy.edu.ort.battleship.modelo.Ronda;
import uy.edu.ort.battleship.observador.Observable;
import uy.edu.ort.battleship.observador.Observador;

@RestController
@Scope("session")
public class BatallaPresentador implements Observador {

    private final Fachada fachada;
    private final ConexionNavegador conexionNavegador;
    private Batalla batallaActual;
    private Orientacion orientacionSeleccionada = Orientacion.HORIZONTAL;

    public BatallaPresentador(
            Fachada fachada,
            ConexionNavegador conexionNavegador) {
        this.fachada = fachada;
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(
            value = "/batalla/registrarSSE",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE() {
        return conexionNavegador.conectarSSE();
    }

    @PostMapping("/batalla/vistaConectada")
    public Commands vistaConectada(@RequestParam String lugar)
            throws BatallaException {

        Batalla nuevaBatalla = fachada.buscarBatalla(lugar);

        if (batallaActual != null) {
            batallaActual.quitarObservador(this);
        }

        batallaActual = nuevaBatalla;
        batallaActual.agregarObservador(this);
        orientacionSeleccionada = Orientacion.HORIZONTAL;

        InicializarDto tablero = new InicializarDto(
                batallaActual.getOceanoPropio().getFilas(),
                batallaActual.getOceanoPropio().getColumnas(),
                "/batalla/ubicarBarco",
                "/batalla/disparar");

        Commands respuesta = Commands.create(
                new Command("mostrarLugar", batallaActual.getLugar()),
                new Command("mostrarEstado", batallaActual.getNombreEstado()),
                new Command("mostrarRonda", batallaActual.getNumeroRonda()),
                new Command("inicializar", tablero),
                barcosDisponibles(batallaActual),
                bajasEnemigas(batallaActual));

        for (Command comando : barcosEnTablero(batallaActual)) {
            respuesta.add(comando);
        }

        for (Ronda ronda : batallaActual.getRondas()) {
            respuesta.add(mostrarDisparo(ronda.getDisparoHumano(), false));

            if (ronda.getDisparoMaquina() != null) {
                respuesta.add(mostrarDisparo(ronda.getDisparoMaquina(), true));
            }
        }

        for (Command comando : barcosEnemigosPorRevelar(batallaActual)) {
            respuesta.add(comando);
        }

        return respuesta;
    }

    @PostMapping("/batalla/vistaDesconectada")
    public void vistaDesconectada() {
        if (batallaActual != null) {
            batallaActual.quitarObservador(this);
            batallaActual = null;
        }
    }

   @Override
public void actualizar(Observable origen, Object evento) {
    if (origen != batallaActual) {
        return;
    }

    Commands respuesta = Commands.create(
            new Command("mostrarEstado", batallaActual.getNombreEstado()),
            new Command("mostrarRonda", batallaActual.getNumeroRonda()),
            barcosDisponibles(batallaActual),
            bajasEnemigas(batallaActual));

    for (Command comando : barcosEnTablero(batallaActual)) {
        respuesta.add(comando);
    }

    for (Ronda ronda : batallaActual.getRondas()) {
        respuesta.add(mostrarDisparo(ronda.getDisparoHumano(), false));

        if (ronda.getDisparoMaquina() != null) {
            respuesta.add(mostrarDisparo(ronda.getDisparoMaquina(), true));
        }
    }

    for (Command comando : barcosEnemigosPorRevelar(batallaActual)) {
        respuesta.add(comando);
    }

    conexionNavegador.enviar(respuesta);
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

        if (!batallaActual.estaEnPreparacion()) {
            throw new BatallaException("No puede disparar en aguas propias");
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
                new Command("mostrarRonda", batallaActual.getNumeroRonda()),
                new Command("inicializar", tablero));

        for (Command comando : barcosEnTablero(batallaActual)) {
            respuesta.add(comando);
        }

        return respuesta;
    }

    @PostMapping("/batalla/disparar")
    public Commands disparar(
            @RequestParam int coordenadaVertical,
            @RequestParam int coordenadaHorizontal)
            throws BatallaException {

        if (batallaActual == null) {
            throw new BatallaException("Debe entrar a una batalla");
        }

        Ronda ronda = batallaActual.realizarRonda(
                coordenadaVertical, coordenadaHorizontal);

        Commands respuesta = Commands.create(
                mostrarDisparo(ronda.getDisparoHumano(), false));

        if (ronda.getDisparoMaquina() != null) {
            respuesta.add(mostrarDisparo(ronda.getDisparoMaquina(), true));
        }

        respuesta.add(new Command(
                "mostrarEstado", batallaActual.getNombreEstado()));
        respuesta.add(new Command(
                "mostrarRonda", batallaActual.getNumeroRonda()));
        respuesta.add(bajasEnemigas(batallaActual));

        for (Command comando : barcosEnemigosPorRevelar(batallaActual)) {
            respuesta.add(comando);
        }

        return respuesta;
    }

    private Command mostrarDisparo(Disparo disparo, boolean aguasPropias) {
        String id = aguasPropias
                ? "mostrarAguasPropias"
                : "mostrarAguasEnemigas";

        String valor = disparo.getResultado() == ResultadoDisparo.AGUA
                ? "agua"
                : "impacto";

        return new Command(id, new ValorDto(
                disparo.getFila(),
                disparo.getColumna(),
                valor));
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

    private List<Command> barcosEnemigosPorRevelar(Batalla batalla) {
        List<Command> comandos = new ArrayList<>();

        for (Celda celda : batalla.getBarcosEnemigosPorRevelar()) {
            comandos.add(new Command(
                    "mostrarAguasEnemigas",
                    new ValorDto(
                            celda.getFila(),
                            celda.getColumna(),
                            "barco")));
        }

        return comandos;
    }

    private Command bajasEnemigas(Batalla batalla) {
        StringBuilder texto = new StringBuilder();

        for (int tamano : batalla.getBajasEnemigas()) {
            if (texto.length() > 0) {
                texto.append(" ");
            }
            texto.append("{").append(tamano).append("}");
        }

        return new Command(
                "bajasEnemigas",
                texto.length() == 0 ? "Ninguna" : texto.toString());
    }
}