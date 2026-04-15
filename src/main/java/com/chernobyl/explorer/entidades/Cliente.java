package com.chernobyl.explorer.entidades;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Cliente extends DomainEntity {

	@JsonIgnoreProperties("viajeros")
	@ManyToOne
	@JoinColumn(name = "id_reserva_cliente")
	private Reserva reserva;

	@NotBlank
	private String nombre;

	@NotBlank
	private String apellido1;

	private String apellido2;

	@Pattern(regexp = "^([0-9]{8}[A-Z])$")
	@NotBlank
	private String dni;

	private LocalDate fechaNacimiento;

	@NotBlank
	private String nacionalidad;

	private Boolean consentimiento;

	private LocalDate fechaAlta;

	private LocalDate fechaBaja;

	private Boolean activo = true;

	public Cliente() {
	}

	public Cliente(Reserva reserva, @NotBlank String nombre, @NotBlank String apellido1, String apellido2,
			@Pattern(regexp = "^([0-9]{8}[A-Z])$") @NotBlank String dni, LocalDate fechaNacimiento,
			@NotBlank String nacionalidad, Boolean consentimiento, LocalDate fechaAlta, LocalDate fechaBaja,
			Boolean activo) {
		super();
		this.reserva = reserva;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.dni = dni;
		this.fechaNacimiento = fechaNacimiento;
		this.nacionalidad = nacionalidad;
		this.consentimiento = consentimiento;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
		this.activo = activo;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public Boolean isConsentimiento() {
		return consentimiento;
	}

	public void setConsentimiento(Boolean consentimiento) {
		this.consentimiento = consentimiento;
	}

	public LocalDate getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(LocalDate fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public LocalDate getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(LocalDate fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getConsentimiento() {
		return consentimiento;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nacionalidad);
	}

	public Boolean igualNacionalidad(String nacionalidad) {
		return this.nacionalidad.equalsIgnoreCase(nacionalidad);
	};
	
	@jakarta.persistence.PrePersist
	protected void onCreate() {
		this.fechaAlta = LocalDate.now();
	}

}
