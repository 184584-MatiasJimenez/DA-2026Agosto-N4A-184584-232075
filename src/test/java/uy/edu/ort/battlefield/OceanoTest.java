package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import uy.edu.ort.battleship.modelo.*;


public class OceanoTest {

    @Test
    void ubicaUnBarcoHorizontal() throws BatallaException {
        Oceano oceano = new Oceano(6, 6);
        Barco barco = new Barco(new TipoBarco("Crucero", 3));

        oceano.ubicarBarco(barco, 2, 1, Orientacion.HORIZONTAL);

        assertTrue(oceano.getCelda(2, 1).estaOcupada());
        assertTrue(oceano.getCelda(2, 2).estaOcupada());
        assertTrue(oceano.getCelda(2, 3).estaOcupada());
        assertFalse(oceano.getCelda(2, 4).estaOcupada());
    }

    @Test
    void rechazaUnBarcoQueSaleDelOceano() {
        Oceano oceano = new Oceano(6, 6);
        Barco barco = new Barco(new TipoBarco("Crucero", 3));

        assertThrows(BatallaException.class,
                () -> oceano.ubicarBarco(barco, 5, 4, Orientacion.HORIZONTAL));

        assertFalse(oceano.getCelda(5, 4).estaOcupada());
    }

    @Test
void disparaAguaImpactaYHunde() throws BatallaException {
    Oceano oceano = new Oceano(6, 6);
    Barco barco = new Barco(new TipoBarco("Submarino", 2));
    oceano.ubicarBarco(barco, 0, 0, Orientacion.HORIZONTAL);

    assertEquals(ResultadoDisparo.AGUA, oceano.disparar(5, 5));
    assertEquals(ResultadoDisparo.IMPACTO, oceano.disparar(0, 0));
    assertEquals(1, barco.getPartesSanas());

    assertEquals(ResultadoDisparo.HUNDIDO, oceano.disparar(0, 1));
    assertTrue(barco.estaHundido());

    assertThrows(BatallaException.class, () -> oceano.disparar(0, 1));
}

@Test
void batallaConfiguraYUbicaSuFlota() throws BatallaException {
Batalla batalla = new Batalla("Montevideo", 8, 8, new TacticaSecuencial());
    TipoBarco tipo = new TipoBarco("Submarino", 2);

    batalla.agregarTipoBarco(tipo, 1);

    assertEquals(1, batalla.getFlotaPropia().getBarcos().size());
    assertEquals(1, batalla.getFlotaEnemiga().getBarcos().size());
    assertFalse(batalla.tieneFlotaPropiaUbicada());

    Barco barcoPropio = batalla.getFlotaPropia().getBarcos().get(0);
    batalla.ubicarBarcoPropio(barcoPropio, 0, 0, Orientacion.HORIZONTAL);

    assertTrue(batalla.tieneFlotaPropiaUbicada());
}

@Test
void iniciaCombateSoloConLaFlotaUbicada() throws BatallaException {
Batalla batalla = new Batalla("Montevideo", 8, 8, new TacticaSecuencial());
    batalla.agregarTipoBarco(new TipoBarco("Destructor", 1), 1);

    assertEquals("En preparación", batalla.getNombreEstado());
    assertThrows(BatallaException.class, batalla::iniciarCombate);

    Barco propio = batalla.getFlotaPropia().getBarcos().get(0);
    batalla.ubicarBarcoPropio(propio, 0, 0, Orientacion.HORIZONTAL);
    batalla.iniciarCombate();

    assertEquals("En combate", batalla.getNombreEstado());

    Barco enemigo = batalla.getFlotaEnemiga().getBarcos().get(0);
    assertTrue(batalla.getOceanoEnemigo().barcoYaUbicado(enemigo));

    assertThrows(BatallaException.class,
            () -> batalla.ubicarBarcoPropio(propio, 2, 2, Orientacion.HORIZONTAL));
}

@Test
void tacticaSecuencialRecorreFilas() throws BatallaException {
    Oceano oceano = new Oceano(6, 6);
    Tactica tactica = new TacticaSecuencial();

    Celda primera = tactica.elegirDisparo(oceano);
    assertEquals(0, primera.getFila());
    assertEquals(0, primera.getColumna());

    for (int columna = 0; columna < 6; columna++) {
        oceano.disparar(0, columna);
    }

    Celda siguiente = tactica.elegirDisparo(oceano);
    assertEquals(1, siguiente.getFila());
    assertEquals(0, siguiente.getColumna());
}

@Test
void noPermiteCambiarFlotaDespuesDeUbicarUnBarco() throws BatallaException {
    Batalla batalla = new Batalla(
            "Montevideo", 8, 8, new TacticaSecuencial());
    batalla.agregarTipoBarco(new TipoBarco("Destructor", 1), 1);

    Barco barco = batalla.getFlotaPropia().getBarcos().get(0);
    batalla.ubicarBarcoPropio(barco, 0, 0, Orientacion.HORIZONTAL);

    assertThrows(BatallaException.class,
            () -> batalla.agregarTipoBarco(
                    new TipoBarco("Submarino", 2), 1));
}

@Test
void victoriaHumanaTerminaSinDisparoDeLaMaquina()
        throws BatallaException {
    Batalla batalla = new Batalla(
            "Montevideo", 6, 6, new TacticaSecuencial());
    batalla.agregarTipoBarco(new TipoBarco("Destructor", 1), 1);

    Barco propio = batalla.getFlotaPropia().getBarcos().get(0);
    batalla.ubicarBarcoPropio(propio, 0, 0, Orientacion.HORIZONTAL);
    batalla.iniciarCombate();

    Celda objetivo = null;
    for (int fila = 0; fila < 6 && objetivo == null; fila++) {
        for (int columna = 0; columna < 6 && objetivo == null; columna++) {
            Celda celda = batalla.getOceanoEnemigo().getCelda(fila, columna);
            if (celda.estaOcupada()) {
                objetivo = celda;
            }
        }
    }

    assertNotNull(objetivo);
    Ronda ronda = batalla.realizarRonda(
            objetivo.getFila(), objetivo.getColumna());

    assertEquals(ResultadoDisparo.HUNDIDO,
            ronda.getDisparoHumano().getResultado());
    assertNull(ronda.getDisparoMaquina());
    assertEquals("Ganada", batalla.getNombreEstado());
    assertEquals(1, batalla.getRondas().size());
}

@Test
void laMaquinaRespondeCuandoLaBatallaContinua()
        throws BatallaException {
    Batalla batalla = new Batalla(
            "Montevideo", 6, 6, new TacticaSecuencial());
    batalla.agregarTipoBarco(new TipoBarco("Submarino", 2), 1);

    Barco propio = batalla.getFlotaPropia().getBarcos().get(0);
    batalla.ubicarBarcoPropio(propio, 4, 4, Orientacion.HORIZONTAL);
    batalla.iniciarCombate();

    Celda aguaEnemiga = null;
    for (int fila = 0; fila < 6 && aguaEnemiga == null; fila++) {
        for (int columna = 0; columna < 6 && aguaEnemiga == null; columna++) {
            Celda celda = batalla.getOceanoEnemigo().getCelda(fila, columna);
            if (!celda.estaOcupada()) {
                aguaEnemiga = celda;
            }
        }
    }

    assertNotNull(aguaEnemiga);
    Ronda ronda = batalla.realizarRonda(
            aguaEnemiga.getFila(), aguaEnemiga.getColumna());

    assertEquals(1, ronda.getNumero());
    assertEquals(ResultadoDisparo.AGUA,
            ronda.getDisparoHumano().getResultado());
    assertNotNull(ronda.getDisparoMaquina());
    assertEquals(0, ronda.getDisparoMaquina().getFila());
    assertEquals(0, ronda.getDisparoMaquina().getColumna());
    assertEquals("En combate", batalla.getNombreEstado());
}

@Test
void tacticaEspiralRecorreDesdeElCentro() throws BatallaException {
    Oceano oceano = new Oceano(8, 6);
    Tactica tactica = new TacticaEspiral();
    int[][] esperadas = {
        {4, 2}, {4, 3}, {3, 3}, {3, 2}, {3, 1}
    };

    for (int[] esperada : esperadas) {
        Celda celda = tactica.elegirDisparo(oceano);

        assertEquals(esperada[0], celda.getFila());
        assertEquals(esperada[1], celda.getColumna());

        oceano.disparar(celda.getFila(), celda.getColumna());
    }
}

@Test
void cazaYAtaqueBuscaJuntoAUnImpacto() throws BatallaException {
    Oceano oceano = new Oceano(6, 6);
    Barco barco = new Barco(new TipoBarco("Crucero", 3));
    oceano.ubicarBarco(barco, 2, 2, Orientacion.HORIZONTAL);

    assertEquals(ResultadoDisparo.IMPACTO, oceano.disparar(2, 2));

    Tactica tactica = new TacticaCazaYAtaque();
    Celda objetivo = tactica.elegirDisparo(oceano);

    assertEquals(2, objetivo.getFila());
    assertEquals(3, objetivo.getColumna());
}

@Test
void cazaYAtaqueSigueLaOrientacionDelBarco() throws BatallaException {
    Oceano oceano = new Oceano(6, 6);
    Barco barco = new Barco(new TipoBarco("Crucero", 3));
    oceano.ubicarBarco(barco, 2, 2, Orientacion.HORIZONTAL);

    oceano.disparar(2, 2);
    oceano.disparar(2, 3);

    Celda objetivo = new TacticaCazaYAtaque().elegirDisparo(oceano);

    assertEquals(2, objetivo.getFila());
    assertEquals(4, objetivo.getColumna());
}

@Test
void fachadaSoloGuardaBatallasValidas() throws BatallaException {
    Fachada fachada = Fachada.getInstancia();
    int cantidadInicial = fachada.getBatallas().size();

    Batalla batalla = fachada.crearBatalla(
            null, 8, 8, "Secuencial",
            Map.of("Submarino", 1, "Destructor", 1));

    assertEquals(cantidadInicial + 1, fachada.getBatallas().size());
    assertEquals("En preparación", batalla.getNombreEstado());
    assertEquals(2, batalla.getFlotaPropia().getBarcos().size());
    assertTrue(batalla.getLugar().matches("[A-Z]{6}"));

    assertThrows(BatallaException.class,
            () -> fachada.crearBatalla(
                    "Flota demasiado grande", 6, 6, "Secuencial",
                    Map.of("Portaaviones", 2)));

    assertEquals(cantidadInicial + 1, fachada.getBatallas().size());
}
}