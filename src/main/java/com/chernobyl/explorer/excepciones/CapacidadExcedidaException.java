package com.chernobyl.explorer.excepciones;

/**
 * Excepción personalizada lanzada cuando se intentan superar los topes físicos 
 * de aforo en la Zona de Exclusión (ej. más de 10 reservas al día, falta de plazas).
 * Desencadena automáticamente un error HTTP 409 (Conflict).
 */
public class CapacidadExcedidaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepción informando del límite sobrepasado y, opcionalmente, de fechas alternativas.
     * @param mensaje Descripción del problema de aforo.
     */
    public CapacidadExcedidaException(String mensaje) {
        super(mensaje);
    }
}