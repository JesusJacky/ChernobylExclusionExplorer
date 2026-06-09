package com.chernobyl.explorer.excepciones;

/**
 * Excepción personalizada lanzada cuando una transacción bancaria es rechazada,
 * ya sea por formato de tarjeta incorrecto o parámetros nulos.
 * Desencadena automáticamente un error HTTP 402 (Payment Required).
 */
public class PagoInvalidoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepción con el detalle del fallo en el cobro.
     * @param mensaje Motivo del rechazo de la transacción bancaria.
     */
    public PagoInvalidoException(String mensaje) {
        super(mensaje);
    }
}