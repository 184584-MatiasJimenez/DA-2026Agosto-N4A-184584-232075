package uy.edu.ort.battleship.presentadores;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Scope("session")
public class ConexionNavegador {

    private SseEmitter conexionSSE;

    public SseEmitter conectarSSE() {
        if (conexionSSE != null) {
            conexionSSE.complete();
        }

        conexionSSE = new SseEmitter(30L * 60 * 1000);
        return conexionSSE;
    }

    public void enviar(Commands comandos) {
        if (conexionSSE == null) {
            return;
        }

        try {
            conexionSSE.send(comandos);
        } catch (Exception e) {
            conexionSSE.complete();
            conexionSSE = null;
        }
    }
}