package uy.edu.ort.battleship.presentadores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uy.edu.ort.battleship.modelo.BatallaException;

public class GlobalExceptionHandler {

    private final int errorCodeStatus= 299; //cambiar si se quiere otro codigopara indicar excepcionde aplicacion

  @ExceptionHandler(BatallaException.class)
  public ResponseEntity<String> manejarException(BatallaException ex) {  
        return ResponseEntity.status(errorCodeStatus).body(ex.getMessage());
  }
}
