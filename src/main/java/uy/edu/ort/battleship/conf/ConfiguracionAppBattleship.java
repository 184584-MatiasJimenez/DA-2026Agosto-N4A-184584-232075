package uy.edu.ort.battleship.conf;

import org.springframework.context.annotation.Bean;

import uy.edu.ort.battleship.modelo.Fachada;

public class ConfiguracionAppBattleship {

    @Bean
    public Fachada fachada() {
        return Fachada.getInstancia();
    }

}
