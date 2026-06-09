package com.chernobyl.explorer.entidades;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Pattern;

/**
 * Entidad de catálogo estático que categoriza la peligrosidad de las zonas de la central.
 * Regula los topes de radiación absorbible y el tiempo máximo que un grupo puede permanecer en un área.
 */
@Entity
@Schema(description = "Categoría estática de peligrosidad radiológica que define los límites de una zona.")
public class NivelPeligro extends DomainEntity {

	@Schema(description = "Categoría oficial de la zona.", allowableValues = {"BAJO", "MEDIO", "ALTO"}, requiredMode = Schema.RequiredMode.REQUIRED)
	@Pattern(regexp = "^BAJO|MEDIO|ALTO$")
	private String nivel;
	
	@Schema(description = "Límite máximo de radiación ambiental en la zona, medida en miliSieverts (mSv).", example = "2.5")
	private double nivelMsv; 
	
	@Schema(description = "Tiempo máximo permitido de estancia ininterrumpida en minutos.", example = "120")
	private int tiempoMaximoExposicion;

	public NivelPeligro() {
		super();
	}

	public NivelPeligro(String nivel, double nivelMsv, int tiempoMaximoExposicion) {
		super();
		this.nivel = nivel;
		this.nivelMsv = nivelMsv;
		this.tiempoMaximoExposicion = tiempoMaximoExposicion;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public double getNivelMsv() {
		return nivelMsv;
	}

	public void setNivelMsv(double nivelMsv) {
		this.nivelMsv = nivelMsv;
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
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NivelPeligro other = (NivelPeligro) obj;
		return Objects.equals(nivel, other.nivel);
	}
}