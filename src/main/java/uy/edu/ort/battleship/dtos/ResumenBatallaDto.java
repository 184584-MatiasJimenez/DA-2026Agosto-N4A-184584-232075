package uy.edu.ort.battleship.dtos;
import lombok.Getter;
import uy.edu.ort.battleship.modelo.Batalla;

@Getter
public class ResumenBatallaDto {

    private final String lugar;
    private final int ancho;
    private final int alto;
    private final String estado;
    private final int partesSanasPropias;
    private final int tamanoTotalPropio;
    private final int partesSanasEnemigas;
    private final int tamanoTotalEnemigo;

    public ResumenBatallaDto(Batalla batalla) {
        this.lugar = batalla.getLugar();
        this.ancho = batalla.getOceanoPropio().getColumnas();
        this.alto = batalla.getOceanoPropio().getFilas();
        this.estado = batalla.getNombreEstado();
        this.partesSanasPropias = batalla.getFlotaPropia().getPartesSanas();
        this.tamanoTotalPropio = batalla.getFlotaPropia().getTamanoTotal();
        this.partesSanasEnemigas = batalla.getFlotaEnemiga().getPartesSanas();
        this.tamanoTotalEnemigo = batalla.getFlotaEnemiga().getTamanoTotal();
    }
}