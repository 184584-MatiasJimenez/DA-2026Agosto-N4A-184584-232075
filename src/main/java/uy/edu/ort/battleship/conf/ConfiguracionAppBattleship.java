package uy.edu.ort.battleship.conf;

import org.springframework.context.annotation.Bean;

import uy.edu.ort.battleship.modelo.Fachada;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionAppBattleship {

    @Bean
    public Fachada fachada() {
        return Fachada.getInstancia();
    }

}
