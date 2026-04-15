package com.chernobyl.explorer.entidades;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Reserva extends DomainEntity {

	private LocalDate fechaViaje;

	@Email
	private String emailContacto;

	private Double precioTotal;

	private Boolean confirmacionMayorEdad;

	private String estado;

	private String localizadorCliente;

	private String observaciones;

	@Pattern(regexp = "^([6789]\\d{8})?$")
	private String telefono;

	@JsonIgnoreProperties("reserva")
	@OneToMany(mappedBy = "reserva")
	private List<Cliente> viajeros;

	@JsonIgnoreProperties("reservas")
	@ManyToOne
	private PaqueteViaje tipoPaquete;

	@Transient
	private Integer plazasLibres;

	public Reserva() {
		super();
	}

	public Reserva(LocalDate fechaViaje, @Email String emailContacto, Double precioTotal, Boolean confirmacionMayorEdad,
			String estado, String localizadorCliente, String observaciones,
			@Pattern(regexp = "^([6789]\\d{8})?$") String telefono, List<Cliente> viajeros, PaqueteViaje tipoPaquete,
			Integer plazasLibres) {
		super();
		this.fechaViaje = fechaViaje;
		this.emailContacto = emailContacto;
		this.precioTotal = precioTotal;
		this.confirmacionMayorEdad = confirmacionMayorEdad;
		this.estado = estado;
		this.localizadorCliente = localizadorCliente;
		this.observaciones = observaciones;
		this.telefono = telefono;
		this.viajeros = viajeros;
		this.tipoPaquete = tipoPaquete;
		this.plazasLibres = plazasLibres;
	}

	public LocalDate getFechaViaje() {
		return fechaViaje;
	}

	public void setFechaViaje(LocalDate fechaViaje) {
		this.fechaViaje = fechaViaje;
	}

	public String getEmailContacto() {
		return emailContacto;
	}

	public void setEmailContacto(String emailContacto) {
		this.emailContacto = emailContacto;
	}

	public Double getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(Double precioTotal) {
		this.precioTotal = precioTotal;
	}

	public Boolean isConfirmacionMayorEdad() {
		return confirmacionMayorEdad;
	}

	public void setConfirmacionMayorEdad(Boolean confirmacionMayorEdad) {
		this.confirmacionMayorEdad = confirmacionMayorEdad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getLocalizadorCliente() {
		return localizadorCliente;
	}

	public void setLocalizadorCliente(String localizadorCliente) {
		this.localizadorCliente = localizadorCliente;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public List<Cliente> getViajeros() {
		return viajeros;
	}

	public void setViajeros(List<Cliente> viajeros) {
		this.viajeros = viajeros;
	}

	public PaqueteViaje getTipoPaquete() {
		return tipoPaquete;
	}

	public void setTipoPaquete(PaqueteViaje tipoPaquete) {
		this.tipoPaquete = tipoPaquete;
	}

	public Integer getPlazasLibres() {
		return plazasLibres;
	}

	public void setPlazasLibres(Integer plazasLibres) {
		this.plazasLibres = plazasLibres;
	}

	public Boolean getConfirmacionMayorEdad() {
		return confirmacionMayorEdad;
	}

	public void generarLocalizador() {
		this.localizadorCliente = "CH-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

}
