package uy.edu.ort.battleship.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uy.edu.ort.battleship.observador.Observable;
import uy.edu.ort.battleship.observador.Observador;

public class Fachada extends Observable implements Observador {

    public enum Evento {
        BATALLA_CREADA,
        BATALLA_ACTUALIZADA
    }

    private static Fachada instancia;

    private final FabricaTacticas fabricaTacticas = new FabricaTacticas();
    private final List<TipoBarco> tiposBarco = new ArrayList<>();
    private final List<Batalla> batallas = new ArrayList<>();

    private Fachada() {
        tiposBarco.add(new TipoBarco("Portaaviones", 5));
        tiposBarco.add(new TipoBarco("Acorazado", 4));
        tiposBarco.add(new TipoBarco("Crucero", 3));
        tiposBarco.add(new TipoBarco("Submarino", 2));
        tiposBarco.add(new TipoBarco("Destructor", 1));
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
        return fabricaTacticas.getTacticasDisponibles();
    }

    public List<Batalla> getBatallas() {
        return new ArrayList<>(batallas);
    }

    public synchronized int getBatallasGanadas() {
        int ganadas = 0;

        for (Batalla batalla : batallas) {
            if (batalla.esGanada()) {
                ganadas++;
            }
        }

        return ganadas;
    }

    public synchronized int getBatallasPerdidas() {
        int perdidas = 0;

        for (Batalla batalla : batallas) {
            if (batalla.esPerdida()) {
                perdidas++;
            }
        }

        return perdidas;
    }

    public synchronized int getExitoCampana() {
        if (batallas.isEmpty()) {
            return 0;
        }

        return getBatallasGanadas() * 100 / batallas.size();
    }

    public TipoBarco buscarTipoBarco(String nombre) throws BatallaException {
        for (TipoBarco tipo : tiposBarco) {
            if (tipo.getNombre().equals(nombre)) {
                return tipo;
            }
        }
        throw new BatallaException("El tipo de barco indicado no existe");
    }

    public synchronized Batalla crearBatalla(String lugar, int filas, int columnas, String nombreTactica, Map<String, Integer> cantidades) throws BatallaException {
        Batalla.validarDimensiones(filas, columnas);

        Tactica tactica = fabricaTacticas.crear(nombreTactica);
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

        Batalla batalla = new Batalla(nombreLugar, filas, columnas, tactica);

        for (TipoBarco tipo : tiposBarco) {
            Integer valor = cantidades.get(tipo.getNombre());
            int cantidad = valor == null ? 0 : valor;

            if (cantidad != 0) {
                batalla.agregarTipoBarco(tipo, cantidad);
            }
        }

        batalla.validarFlotaConfigurada();

        batalla.agregarObservador(this);
        batallas.add(batalla);
        avisar(Evento.BATALLA_CREADA);
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

    @Override
    public void actualizar(Observable origen, Object evento) {
        avisar(Evento.BATALLA_ACTUALIZADA);
    }
}