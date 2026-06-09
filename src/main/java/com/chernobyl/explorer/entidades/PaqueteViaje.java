package com.chernobyl.explorer.entidades;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;

/**
 * Entidad que representa un producto comercial (Expedición).
 * Vincula un nombre comercial, un precio y lugares de interés con un nivel de peligro radiológico específico.
 */
@Entity
@Schema(description = "Catálogo de expediciones y rutas ofrecidas por la empresa en la Zona de Alienación.")
public class PaqueteViaje extends DomainEntity {

	@Schema(description = "Nombre comercial de la ruta.", example = "Zona Roja Reactor 4")
	private String nombre;

	@Schema(description = "Explicación detallada del recorrido.", example = "Tour extremo cerca del reactor y zonas altamente contaminadas.")
	@Column(length = 2000) // Ampliado para descripciones épicas
	private String descripcion;

	@Schema(description = "Precio total del paquete por persona. No puede ser negativo.", example = "250.0")
	@Min(0)
	private Double precioPaquete;

	@Schema(description = "Nivel de peligro asociado a esta ruta.")
	@ManyToOne
	private NivelPeligro nivelpeligro;

	@Schema(description = "Puntos de interés turístico incluidos en el itinerario.", example = "Reactor 4, Bosque Rojo, Sarcófago")
	@Column(length = 1000) // Ampliado para listas de lugares largas
	private String lugaresIncluidos;

	@Schema(description = "Estimación de radiación absorbida por el cliente al realizar esta ruta (usado para el diario de dosimetría).", example = "0.60")
	private Double dosisEstimadaMsv;

	public PaqueteViaje() {
		super();
	}

	public PaqueteViaje(String nombre, String descripcion, Double precioPaquete, NivelPeligro nivelpeligro,
			String lugaresIncluidos, Double dosisEstimadaMsv) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precioPaquete = precioPaquete;
		this.nivelpeligro = nivelpeligro;
		this.lugaresIncluidos = lugaresIncluidos;
		this.dosisEstimadaMsv = dosisEstimadaMsv;
	}

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

	public Double getPrecioPaquete() { return precioPaquete; }
	public void setPrecioPaquete(Double precioPaquete) { this.precioPaquete = precioPaquete; }

	public NivelPeligro getNivelpeligro() { return nivelpeligro; }
	public void setNivelpeligro(NivelPeligro nivelpeligro) { this.nivelpeligro = nivelpeligro; }

	public String getLugaresIncluidos() { return lugaresIncluidos; }
	public void setLugaresIncluidos(String lugaresIncluidos) { this.lugaresIncluidos = lugaresIncluidos; }

	public Double getDosisEstimadaMsv() { return dosisEstimadaMsv; }
	public void setDosisEstimadaMsv(Double dosisEstimadaMsv) { this.dosisEstimadaMsv = dosisEstimadaMsv; }
}