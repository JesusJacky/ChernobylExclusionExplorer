package com.chernobyl.explorer.excepciones;

public class ElementoNoEncontradaException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElementoNoEncontradaException(String mensaje) {
		super(mensaje);
	}
}
