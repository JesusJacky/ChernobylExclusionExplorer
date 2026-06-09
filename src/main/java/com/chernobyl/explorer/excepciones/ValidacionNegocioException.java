package com.chernobyl.explorer.excepciones;

/**
 * Excepción personalizada lanzada cuando se incumple una regla estricta de negocio
 * (ej. intentar cancelar una reserva fuera de plazo, precios en negativo, etc.).
 * Desencadena automáticamente un error HTTP 400 (Bad Request).
 */
public class ValidacionNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepción con el motivo por el cual la operación fue rechazada.
     * @param mensaje Descripción de la regla de negocio vulnerada.
     */
    public ValidacionNegocioException(String mensaje) {
        super(mensaje);
    }
}