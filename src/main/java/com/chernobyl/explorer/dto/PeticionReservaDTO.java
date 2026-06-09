package com.chernobyl.explorer.dto;

import java.time.LocalDate;
import java.util.List;

public class PeticionReservaDTO {
	private Integer idPaquete;
	private LocalDate fechaViaje;
	private String telefonoContacto;
	private List<AcompananteDTO> acompanantes;

	public Integer getIdPaquete() {
		return idPaquete;
	}

	public void setIdPaquete(Integer idPaquete) {
		this.idPaquete = idPaquete;
	}

	public LocalDate getFechaViaje() {
		return fechaViaje;
	}

	public void setFechaViaje(LocalDate fechaViaje) {
		this.fechaViaje = fechaViaje;
	}

	public String getTelefonoContacto() {
		return telefonoContacto;
	}

	public void setTelefonoContacto(String telefonoContacto) {
		this.telefonoContacto = telefonoContacto;
	}

	public List<AcompananteDTO> getAcompanantes() {
		return acompanantes;
	}

	public void setAcompanantes(List<AcompananteDTO> acompanantes) {
		this.acompanantes = acompanantes;
	}

	public static class AcompananteDTO {
		private String nombre;
		private String apellidos;
		private String dni;
		private String nacionalidad;

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getApellidos() {
			return apellidos;
		}

		public void setApellidos(String apellidos) {
			this.apellidos = apellidos;
		}

		public String getDni() {
			return dni;
		}

		public void setDni(String dni) {
			this.dni = dni;
		}

		public String getNacionalidad() {
			return nacionalidad;
		}

		public void setNacionalidad(String nacionalidad) {
			this.nacionalidad = nacionalidad;
		}
	}
}