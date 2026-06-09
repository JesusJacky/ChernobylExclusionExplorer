package com.chernobyl.explorer.entidades;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Superclase abstracta de la que heredan todas las entidades del dominio.
 * Centraliza la generación de la clave primaria (ID) autoincremental para la base de datos,
 * evitando la repetición de código en las subclases.
 */
@MappedSuperclass
public abstract class DomainEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	public DomainEntity() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}