package com.chernobyl.explorer.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Interceptor global de excepciones (Global Exception Handler).
 * Utiliza Spring AOP (Programación Orientada a Aspectos) para "escuchar" en toda la aplicación
 * cuando salta cualquier excepción no controlada. Intercepta el error antes de que llegue al 
 * navegador o a Postman, y lo transforma en una respuesta HTTP formateada con el código de estado correcto.
 */
@ControllerAdvice
public class CapturadorExcepciones {
	
	/**
	 * Captura los errores de elementos no existentes en Base de Datos.
	 * @param ex La excepción capturada.
	 * @return Respuesta HTTP 404 (Not Found) con el mensaje de error.
	 */
	@ExceptionHandler(ElementoNoEncontradaException.class)
	public ResponseEntity<String> manejarEntidadNoEncontrada(ElementoNoEncontradaException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	/**
	 * Captura las violaciones de reglas lógicas dictaminadas por la empresa.
	 * @param ex La excepción capturada.
	 * @return Respuesta HTTP 400 (Bad Request) con el mensaje de error.
	 */
	@ExceptionHandler(ValidacionNegocioException.class)
	public ResponseEntity<String> manejarValidacionNegocio(ValidacionNegocioException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	/**
	 * Captura los errores de saturación de aforo y fechas no disponibles.
	 * @param ex La excepción capturada.
	 * @return Respuesta HTTP 409 (Conflict) con el mensaje de error.
	 */
	@ExceptionHandler(CapacidadExcedidaException.class)
	public ResponseEntity<String> manejarCapacidadExcedida(CapacidadExcedidaException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	/**
	 * Captura los fallos detectados por la pasarela de pagos.
	 * @param ex La excepción capturada.
	 * @return Respuesta HTTP 402 (Payment Required) con el mensaje de error.
	 */
	@ExceptionHandler(PagoInvalidoException.class)
	public ResponseEntity<String> manejarPagoInvalido(PagoInvalidoException ex){
		return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(ex.getMessage());
	}
	
	/**
	 * Red de seguridad final (Fallback) para cualquier otro error de Java imprevisto 
	 * (ej. NullPointerExceptions, fallos de red o errores de conexión a la BD).
	 * @param ex La excepción general (genérica) capturada.
	 * @return Respuesta HTTP 500 (Internal Server Error) detallando el fallo crítico.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> manejarErrorGeneral(Exception ex){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado en el sistema: " + ex.getMessage());
	}
}