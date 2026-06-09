package com.chernobyl.explorer.dto;

/**
 * DTO que encapsula el estado actual de la zona. Utilizado por el Front-End
 * para actualizar el widget de monitorización.
 */
public class MonitorEstadoDTO {
	private String descripcion;
	private double radiacion; // Estático
	private double temperatura; // en °C
	private String iconoCode; // Código de icono de OpenWeatherMap

	public MonitorEstadoDTO(String descripcion, double radiacion, double temperatura, String iconoCode) {
		this.descripcion = descripcion;
		this.radiacion = radiacion;
		this.temperatura = temperatura;
		this.iconoCode = iconoCode;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public double getRadiacion() {
		return radiacion;
	}

	public double getTemperatura() {
		return temperatura;
	}

	public String getIconoCode() {
		return iconoCode;
	}
}