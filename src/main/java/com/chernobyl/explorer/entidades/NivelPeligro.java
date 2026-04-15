package com.chernobyl.explorer.entidades;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Pattern;

@Entity
public class NivelPeligro extends DomainEntity{

	@Pattern(regexp = "^BAJO|MEDIO|ALTO$")
	private String nivel;
	private double nivelRoengents;
	private int tiempoMaximoExposicion;

	public NivelPeligro() {
		super();
	}

	public NivelPeligro(String nivel, double nivelRoengents, int tiempoMaximoExposicion) {
		super();
		this.nivel = nivel;
		this.nivelRoengents = nivelRoengents;
		this.tiempoMaximoExposicion = tiempoMaximoExposicion;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public double getNivelRoengents() {
		return nivelRoengents;
	}

	public void setNivelRoengents(double nivelRoengents) {
		this.nivelRoengents = nivelRoengents;
	}

	public int getTiempoMaximoExposicion() {
		return tiempoMaximoExposicion;
	}

	public void setTiempoMaximoExposicion(int tiempoMaximoExposicion) {
		this.tiempoMaximoExposicion = tiempoMaximoExposicion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nivel);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NivelPeligro other = (NivelPeligro) obj;
		return Objects.equals(nivel, other.nivel);
	}

}
