package uy.edu.ort.battleship.modelo;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.Random;

public class Fachada {

    private static Fachada instancia;

    private final List<TipoBarco> tiposBarco = new ArrayList<>();
    private final List<Tactica> tacticas = new ArrayList<>();
    private final List<Batalla> batallas = new ArrayList<>();

    private Fachada() {
        tiposBarco.add(new TipoBarco("Portaaviones", 5));
        tiposBarco.add(new TipoBarco("Acorazado", 4));
        tiposBarco.add(new TipoBarco("Crucero", 3));
        tiposBarco.add(new TipoBarco("Submarino", 2));
        tiposBarco.add(new TipoBarco("Destructor", 1));

        tacticas.add(new TacticaSecuencial());
        tacticas.add(new TacticaEspiral());
        tacticas.add(new TacticaCazaYAtaque());
    }

    public static Fachada getInstancia() {
        if (instancia == null) {
            instancia = new Fachada();
        }
        return instancia;
    }

    public List<TipoBarco> getTiposBarco() {
        return new ArrayList<>(tiposBarco);
    }

    public List<Tactica> getTacticas() {
        return new ArrayList<>(tacticas);
    }

    public List<Batalla> getBatallas() {
    return new ArrayList<>(batallas);
}

public Tactica buscarTactica(String nombre) throws BatallaException {
    for (Tactica tactica : tacticas) {
        if (tactica.getNombre().equals(nombre)) {
            return tactica;
        }
    }
    throw new BatallaException("La táctica indicada no existe");
}

public TipoBarco buscarTipoBarco(String nombre) throws BatallaException {
    for (TipoBarco tipo : tiposBarco) {
        if (tipo.getNombre().equals(nombre)) {
            return tipo;
        }
    }
    throw new BatallaException("El tipo de barco indicado no existe");
}

public synchronized Batalla crearBatalla(String lugar, int filas, int columnas,String nombreTactica, Map<String, Integer> cantidades)throws BatallaException {

    if (filas < 6 || filas > 12) {
        throw new BatallaException("Las filas deben estar entre 6 y 12");
    }
    if (columnas < 6 || columnas > 12) {
        throw new BatallaException("Las columnas deben estar entre 6 y 12");
    }
    if (cantidades == null) {
        throw new BatallaException("Debe indicar la flota");
    }

    Tactica tactica = buscarTactica(nombreTactica);
    String nombreLugar = lugar == null ? "" : lugar.trim();

    if (nombreLugar.isEmpty()) {
        nombreLugar = generarLugar();
    }
    if (existeLugar(nombreLugar)) {
        throw new BatallaException("No se puede librar más de una batalla en el mismo lugar, cambie de nombre");
    }

    for (String nombreTipo : cantidades.keySet()) {
        buscarTipoBarco(nombreTipo);
    }

    int partes = 0;
    for (TipoBarco tipo : tiposBarco) {
        Integer valor = cantidades.get(tipo.getNombre());
        int cantidad = valor == null ? 0 : valor;

        if (cantidad < 0 || cantidad > 6) {
            throw new BatallaException("La cantidad de " + tipo.getNombre() + " debe estar entre 0 y 6");
        }
        partes += tipo.getTamano() * cantidad;
    }

    if (partes == 0) {
        throw new BatallaException("Debe seleccionar al menos un barco");
    }
    if (partes * 4 >= filas * columnas) {
        throw new BatallaException("La flota debe ocupar menos del 25% del océano");
    }

    Batalla batalla = new Batalla(nombreLugar, filas, columnas, tactica);
    for (TipoBarco tipo : tiposBarco) {
        Integer valor = cantidades.get(tipo.getNombre());
        int cantidad = valor == null ? 0 : valor;
        if (cantidad > 0) {
            batalla.agregarTipoBarco(tipo, cantidad);
        }
    }

    batallas.add(batalla);
    return batalla;
}

private boolean existeLugar(String lugar) {
    for (Batalla batalla : batallas) {
        if (batalla.getLugar().equals(lugar)) {
            return true;
        }
    }
    return false;
}

private String generarLugar() {
    Random azar = new Random();
    String nombre;

    do {
        StringBuilder letras = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            letras.append((char) ('A' + azar.nextInt(26)));
        }
        nombre = letras.toString();
    } while (existeLugar(nombre));

    return nombre;
}

public Batalla buscarBatalla(String lugar) throws BatallaException {
    for (Batalla batalla : batallas) {
        if (batalla.getLugar().equals(lugar)) {
            return batalla;
        }
    }
    throw new BatallaException("La batalla indicada no existe");
}
}