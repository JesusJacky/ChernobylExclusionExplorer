package com.chernobyl.explorer.excepciones;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CapturadorExcepciones {
	
	// Capturar la nueva clase
	@ExceptionHandler(ElementoNoEncontradaException.class)
	public ResponseEntity<String> manejarEntidadNoEncontrada(ElementoNoEncontradaException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	// Capturador general para cualquier otra excepcion
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> manejarErrorGeneral(Exception ex){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado en el sistema:" + ex.getMessage());
	}
}
