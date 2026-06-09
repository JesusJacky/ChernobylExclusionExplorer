package com.chernobyl.explorer.entidades;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Schema(description = "Ficha de datos personales de un explorador, requerida por las autoridades ucranianas para el control perimetral.")
public class Cliente extends DomainEntity {

	@Schema(description = "Usuario del sistema asociado a este cliente para el login.")
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_usuario", referencedColumnName = "id")
	private Usuario usuario;

	@Schema(description = "Referencia a la reserva a la que pertenece actualmente el viajero.")
	@JsonIgnoreProperties("viajeros")
	@ManyToOne
	@JoinColumn(name = "id_reserva_cliente")
	private Reserva reserva;

	@Schema(description = "Nombre oficial del cliente.", example = "Jesús", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String nombre;

	@Schema(description = "Primer apellido.", example = "Hidalgo", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String apellido1;

	@Schema(description = "Segundo apellido (opcional).", example = "Rodríguez")
	private String apellido2;

	@Schema(description = "DNI Español con formato estricto (8 números y 1 letra mayúscula).", example = "12345678A", requiredMode = Schema.RequiredMode.REQUIRED)
	@Pattern(regexp = "^([0-9]{8}[A-Z])$")
	@NotBlank
	private String dni;

	@Schema(description = "Fecha de nacimiento requerida para comprobaciones legales de mayoría de edad.", example = "1988-05-15")
	private LocalDate fechaNacimiento;

	@Schema(description = "País de origen del documento de identidad.", example = "Española", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String nacionalidad;

	@Schema(description = "Correo electrónico de contacto.", example = "ejemplo@correo.com", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String email;

	@Schema(description = "Teléfono móvil de contacto.", example = "+34600000000", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String telefono;

	@Schema(description = "Verificación explícita de aceptación de riesgos y políticas médicas.")
	private Boolean consentimiento;

	@Schema(description = "Fecha automática de registro en el sistema.")
	private LocalDate fechaAlta;

	@Schema(description = "Fecha de desactivación o veto del perfil.")
	private LocalDate fechaBaja = null;

	@Schema(description = "Estado de disponibilidad del perfil. Falso si está vetado o inactivo.")
	private Boolean activo = true;

	public Cliente() {
	}

	public Cliente(Usuario usuario, Reserva reserva, @NotBlank String nombre, @NotBlank String apellido1,
			String apellido2, @Pattern(regexp = "^([0-9]{8}[A-Z])$") @NotBlank String dni, LocalDate fechaNacimiento,
			@NotBlank String nacionalidad, @NotBlank String email, @NotBlank String telefono, Boolean consentimiento,
			LocalDate fechaAlta, LocalDate fechaBaja, Boolean activo) {
		super();
		this.usuario = usuario;
		this.reserva = reserva;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.dni = dni;
		this.fechaNacimiento = fechaNacimiento;
		this.nacionalidad = nacionalidad;
		this.email = email;
		this.telefono = telefono;
		this.consentimiento = consentimiento;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
		this.activo = activo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
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

	@Override
	public int hashCode() {
		return Objects.hash(dni);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(dni, other.dni);
	}
}