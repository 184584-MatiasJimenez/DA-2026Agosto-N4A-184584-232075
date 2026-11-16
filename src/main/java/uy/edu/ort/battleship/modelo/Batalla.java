package uy.edu.ort.battleship.modelo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import uy.edu.ort.battleship.observador.Observable;

public class Batalla extends Observable {

    public enum Evento {
        BARCO_UBICADO,
        COMBATE_INICIADO,
        RONDA_REALIZADA
    }

    @Getter
    private String lugar;

    @Getter
    private Oceano oceanoPropio;

    @Getter
    private Oceano oceanoEnemigo;

    @Getter
    private Flota flotaPropia;

    @Getter
    private Flota flotaEnemiga;

    private EstadoBatalla estado;

    @Getter
    private Tactica tactica;

    private List<Ronda> rondas = new ArrayList<>();

    public Batalla(String lugar, int filas, int columnas, Tactica tactica) throws BatallaException {
        validarDimensiones(filas, columnas);
        if (tactica == null) {
            throw new BatallaException("Debe seleccionar una táctica");
        }

        this.lugar = lugar;
        this.oceanoPropio = new Oceano(filas, columnas);
        this.oceanoEnemigo = new Oceano(filas, columnas);
        this.flotaPropia = new Flota();
        this.flotaEnemiga = new Flota();
        this.estado = new EstadoEnPreparacion();
        this.tactica = tactica;
    }

    public static void validarDimensiones(int filas, int columnas) throws BatallaException {
        if (filas < 6 || filas > 12) {
            throw new BatallaException("Las filas deben estar entre 6 y 12");
        }
        if (columnas < 6 || columnas > 12) {
            throw new BatallaException("Las columnas deben estar entre 6 y 12");
        }
    }

    public void agregarTipoBarco(TipoBarco tipo, int cantidad) throws BatallaException {
        estado.agregarTipoBarco(this, tipo, cantidad);
    }

    void agregarTipoBarcoEnPreparacion(TipoBarco tipo, int cantidad) throws BatallaException {
        if (tipo == null) {
            throw new BatallaException("Debe indicar un tipo de barco");
        }
        if (cantidad == 0) {
            throw new BatallaException("Debe indicar una cantidad de barcos");
        }

        for (Barco barco : flotaPropia.getBarcos()) {
            if (oceanoPropio.barcoYaUbicado(barco)) {
                throw new BatallaException("No se puede modificar la flota después de ubicar un barco");
            }
        }

        flotaPropia.validarCantidad(tipo, cantidad);

        int celdasOcupadas = flotaPropia.getTamanoTotal() + tipo.getTamano() * cantidad;
        int celdasDelOceano = oceanoPropio.getFilas() * oceanoPropio.getColumnas();

        if (celdasOcupadas * 4 >= celdasDelOceano) {
            throw new BatallaException("La flota debe ocupar menos del 25% del océano");
        }

        flotaPropia.agregarBarcos(tipo, cantidad);
        flotaEnemiga.agregarBarcos(tipo, cantidad);
    }

    public void ubicarBarcoPropio(Barco barco, int fila, int columna, Orientacion orientacion) throws BatallaException {
        estado.ubicarBarcoPropio(this, barco, fila, columna, orientacion);
        avisar(Evento.BARCO_UBICADO);
    }

    void ubicarBarcoPropioEnPreparacion(Barco barco, int fila, int columna, Orientacion orientacion) throws BatallaException {
        if (!flotaPropia.getBarcos().contains(barco)) {
            throw new BatallaException("El barco no pertenece a esta batalla");
        }

        oceanoPropio.ubicarBarco(barco, fila, columna, orientacion);
    }

    public boolean tieneFlotaPropiaUbicada() {
        if (flotaPropia.getBarcos().isEmpty()) {
            return false;
        }

        for (Barco barco : flotaPropia.getBarcos()) {
            if (!oceanoPropio.barcoYaUbicado(barco)) {
                return false;
            }
        }

        return true;
    }

    public String getNombreEstado() {
        return estado.getNombre();
    }

    public boolean esGanada() {
        return estado.esGanada();
    }

    public boolean esPerdida() {
        return estado.esPerdida();
    }

    private void ubicarFlotaEnemiga() throws BatallaException {
        if (!tieneFlotaPropiaUbicada()) {
            throw new BatallaException("Debe posicionar todos sus barcos primero");
        }

        List<Barco> barcos = flotaEnemiga.getBarcos();
        barcos.sort((a, b) -> b.getTipo().getTamano() - a.getTipo().getTamano());

        for (int intento = 0; intento < 1000; intento++) {
            Oceano candidato = new Oceano(
                    oceanoEnemigo.getFilas(),
                    oceanoEnemigo.getColumnas());
            boolean todosUbicados = true;

            for (Barco barco : barcos) {
                try {
                    candidato.ubicarBarcoAleatorio(barco);
                } catch (BatallaException e) {
                    todosUbicados = false;
                    break;
                }
            }

            if (todosUbicados) {
                oceanoEnemigo = candidato;
                return;
            }
        }

        throw new BatallaException("No se pudo distribuir la flota enemiga en el océano");
    }

    public void iniciarCombate() throws BatallaException {
        estado.iniciarCombate(this);
        avisar(Evento.COMBATE_INICIADO);
    }

    void iniciarCombateEnPreparacion() throws BatallaException {
        ubicarFlotaEnemiga();
        estado = new EstadoEnCombate();
    }

    public List<Ronda> getRondas() {
        return new ArrayList<>(rondas);
    }

    public synchronized Ronda realizarRonda(int fila, int columna) throws BatallaException {
        Ronda ronda = estado.realizarRonda(this, fila, columna);
        avisar(Evento.RONDA_REALIZADA);
        return ronda;
    }

    Ronda realizarRondaEnCombate(int fila, int columna) throws BatallaException {
        int numero = rondas.size() + 1;

        ResultadoDisparo resultadoHumano = oceanoEnemigo.disparar(fila, columna);
        Disparo disparoHumano = new Disparo(fila, columna, resultadoHumano);
        Disparo disparoMaquina = null;

        if (flotaEnemiga.estaHundida()) {
            estado = new EstadoGanada();
        } else {
            Celda objetivo = tactica.elegirDisparo(oceanoPropio);
            int filaMaquina = objetivo.getFila();
            int columnaMaquina = objetivo.getColumna();

            ResultadoDisparo resultadoMaquina = oceanoPropio.disparar(filaMaquina, columnaMaquina);
            disparoMaquina = new Disparo(filaMaquina, columnaMaquina, resultadoMaquina);

            if (flotaPropia.estaHundida()) {
                estado = new EstadoPerdida();
            }
        }

        Ronda ronda = new Ronda(numero, disparoHumano, disparoMaquina);
        rondas.add(ronda);
        return ronda;
    }

    public List<Barco> getBarcosPropiosSinUbicar() {
        List<Barco> disponibles = new ArrayList<>();

        for (Barco barco : flotaPropia.getBarcos()) {
            if (!oceanoPropio.barcoYaUbicado(barco)) {
                disponibles.add(barco);
            }
        }

        disponibles.sort((primero, segundo) -> Integer.compare(
            segundo.getTipo().getTamano(),
            primero.getTipo().getTamano()
        ));

        return disponibles;
    }

    public List<Integer> getBajasEnemigas() {
        List<Integer> bajas = new ArrayList<>();

        for (Ronda ronda : rondas) {
            Disparo disparo = ronda.getDisparoHumano();
            if (disparo.getResultado() == ResultadoDisparo.HUNDIDO) {
                Celda celda = oceanoEnemigo.getCelda(disparo.getFila(), disparo.getColumna());
                bajas.add(celda.getTamanoBarcoHundido());
            }
        }

        return bajas;
    }

    public int getNumeroRonda() {
        return rondas.size() + (estado.estaFinalizada() ? 0 : 1);
    }

    public boolean estaEnPreparacion() {
        return estado.permiteUbicarBarcos();
    }

    public List<Celda> getBarcosEnemigosPorRevelar() {
        List<Celda> celdas = new ArrayList<>();

        if (estado.revelaFlotaEnemiga()) {
            for (Celda celda : oceanoEnemigo.getCeldasConBarco()) {
                if (!celda.fueDisparada()) {
                    celdas.add(celda);
                }
            }
        }
        return celdas;
    }
}