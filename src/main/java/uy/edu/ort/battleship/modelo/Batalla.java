package uy.edu.ort.battleship.modelo;

import lombok.Getter;

import java.util.List;

import java.util.ArrayList;

public class Batalla {

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

public Batalla(String lugar, int filas, int columnas, Tactica tactica)
        throws BatallaException {
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

    void agregarTipoBarcoEnPreparacion(TipoBarco tipo, int cantidad)throws BatallaException {
        for (Barco barco : flotaPropia.getBarcos()) {
    if (oceanoPropio.barcoYaUbicado(barco)) {
        throw new BatallaException("No se puede modificar la flota después de ubicar un barco");
    }
}
    if (tipo == null) {
        throw new BatallaException("Debe indicar un tipo de barco");
    }

    int celdasOcupadas = flotaPropia.getTamanoTotal()+ tipo.getTamano() * cantidad;
    int celdasDelOceano = oceanoPropio.getFilas()* oceanoPropio.getColumnas();

    if (celdasOcupadas * 4 >= celdasDelOceano) {
        throw new BatallaException("La flota debe ocupar menos del 25% del océano");
    }

    flotaPropia.agregarBarcos(tipo, cantidad);
    flotaEnemiga.agregarBarcos(tipo, cantidad);
}

public void validarFlotaConfigurada() throws BatallaException {
    if (flotaPropia.getBarcos().isEmpty()) {
        throw new BatallaException("Debe seleccionar al menos un barco");
    }
}

public void ubicarBarcoPropio(Barco barco, int fila, int columna,Orientacion orientacion) throws BatallaException {
    estado.ubicarBarcoPropio(this, barco, fila, columna, orientacion);
}

void ubicarBarcoPropioEnPreparacion(Barco barco, int fila, int columna,Orientacion orientacion) throws BatallaException {
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

void ubicarFlotaEnemiga() throws BatallaException {
    if (!tieneFlotaPropiaUbicada()) {
        throw new BatallaException("Debe posicionar todos sus barcos primero");
    }

    List<Barco> barcos = flotaEnemiga.getBarcos();
    barcos.sort((a, b) -> b.getTipo().getTamano() - a.getTipo().getTamano());

    for (int intento = 0; intento < 1000; intento++) {
        Oceano candidato = new Oceano(oceanoEnemigo.getFilas(), oceanoEnemigo.getColumnas());
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

    throw new BatallaException( "No se pudo distribuir la flota enemiga en el océano");
}

public void iniciarCombate() throws BatallaException {
    estado.iniciarCombate(this);
}

void iniciarCombateEnPreparacion() throws BatallaException {
    ubicarFlotaEnemiga();
    estado = new EstadoEnCombate();
}

public void agregarTipoBarco(TipoBarco tipo, int cantidad)throws BatallaException {
    estado.agregarTipoBarco(this, tipo, cantidad);
}

public List<Ronda> getRondas() {
    return new ArrayList<>(rondas);
}

public synchronized Ronda realizarRonda(int fila, int columna) throws BatallaException {
    return estado.realizarRonda(this, fila, columna);
}

Ronda realizarRondaEnCombate(int fila, int columna)throws BatallaException {
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
}