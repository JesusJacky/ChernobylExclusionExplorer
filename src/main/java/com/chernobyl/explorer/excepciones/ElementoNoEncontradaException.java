package com.chernobyl.explorer.excepciones;

/**
 * Excepción personalizada lanzada cuando se intenta buscar, actualizar o eliminar
 * un registro (Cliente, Reserva, Paquete, etc.) que no existe en la base de datos.
 * Desencadena automáticamente un error HTTP 404 (Not Found).
 */
public class ElementoNoEncontradaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Construye la excepción con un mensaje detallado del error.
	 * @param mensaje Descripción del elemento que no pudo ser encontrado.
	 */
	public ElementoNoEncontradaException(String mensaje) {
		super(mensaje);
	}
}