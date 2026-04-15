package com.chernobyl.explorer.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;

@Entity
public class PaqueteViaje extends DomainEntity {

	private String nombre;

	private String descripcion;

	@Min(0)
	private Double precioPaquete;

	@ManyToOne
	private NivelPeligro nivelpeligro;

	private String lugaresIncluidos;

	public PaqueteViaje() {
		super();
	}

	public PaqueteViaje(String nombre, String descripcion, Double precioPaquete, NivelPeligro nivelpeligro,
			String lugaresIncluidos) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precioPaquete = precioPaquete;
		this.nivelpeligro = nivelpeligro;
		this.lugaresIncluidos = lugaresIncluidos;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getPrecioPaquete() {
		return precioPaquete;
	}

	public void setPrecioPaquete(Double precioPaquete) {
		this.precioPaquete = precioPaquete;
	}

	public NivelPeligro getNivelpeligro() {
		return nivelpeligro;
	}

	public void setNivelpeligro(NivelPeligro nivelpeligro) {
		this.nivelpeligro = nivelpeligro;
	}

	public String getLugaresIncluidos() {
		return lugaresIncluidos;
	}

	public void setLugaresIncluidos(String lugaresIncluidos) {
		this.lugaresIncluidos = lugaresIncluidos;
	}

}
