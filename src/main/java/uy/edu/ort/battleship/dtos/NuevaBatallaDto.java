package uy.edu.ort.battleship.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NuevaBatallaDto {

    private String lugar;
    private String tactica;
    private int filas;
    private int columnas;

    private int portaaviones;
    private int acorazados;
    private int cruceros;
    private int submarinos;
    private int destructores;
}