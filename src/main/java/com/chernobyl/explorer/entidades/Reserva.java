package com.chernobyl.explorer.entidades;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * Entidad transaccional central. Conecta al cliente con un paquete de viaje en
 * una fecha concreta. Almacena el estado del ciclo de vida de la reserva
 * (PENDIENTE, PAGADA, etc.).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Schema(description = "Registro de la solicitud de expedición vinculando a los clientes con un paquete en una fecha determinada.")
public class Reserva extends DomainEntity {

	@Schema(description = "Fecha planificada para la entrada a la zona de exclusión.", example = "2026-08-15")
	private LocalDate fechaViaje;

	@Schema(description = "Correo electrónico de contacto donde se enviarán comprobantes y avisos.", example = "usuario@correo.com")
	@Email
	private String emailContacto;

	@Schema(description = "Suma total económica a abonar.", example = "500.0")
	private Double precioTotal;

	@Schema(description = "Verificación booleana de que el titular de la reserva es mayor de edad.")
	private Boolean confirmacionMayorEdad;

	@Schema(description = "Estado actual en el flujo logístico.", example = "PENDIENTE")
	private String estado;

	@Schema(description = "Código alfanumérico único proporcionado al cliente para seguimiento.", example = "LOC-1A2B3C")
	private String localizadorCliente;

	@Schema(description = "Anotaciones adicionales sobre requisitos médicos, alergias o solicitudes especiales.")
	private String observaciones;

	@Schema(description = "Teléfono de contacto español (comienza por 6, 7, 8 o 9 y tiene 9 dígitos).", example = "600123456")
	@Pattern(regexp = "^(\\+?[0-9]{9,15})?$")
	private String telefono;

	@Schema(description = "Listado de expedicionarios vinculados a esta solicitud.")
	@JsonIgnoreProperties("reserva")
	@OneToMany(mappedBy = "reserva")
	private List<Cliente> viajeros;

	@Schema(description = "La ruta comercial elegida.")
	@JsonIgnoreProperties("reservas")
	@ManyToOne
	private PaqueteViaje tipoPaquete;

	@Schema(description = "Campo virtual (no guardado en BD) que calcula cuántas plazas restan para la expedición de ese día.")
	@Transient
	private Integer plazasLibres;

	@Schema(description = "Usuario interno de SBU que gestionó o modificó la reserva por última vez.")
	private String empleadoGestor;

	public Reserva() {
		super();
	}

	public Reserva(LocalDate fechaViaje, @Email String emailContacto, Double precioTotal, Boolean confirmacionMayorEdad,
			String estado, String localizadorCliente, String observaciones,
			@Pattern(regexp = "^([6789]\\d{8})?$") String telefono, List<Cliente> viajeros, PaqueteViaje tipoPaquete) {
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
		this.empleadoGestor = empleadoGestor;
	}

	public void generarLocalizador() {
		this.localizadorCliente = "LOC-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
	}

	// Getters y Setters
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

	public Boolean getConfirmacionMayorEdad() {
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

	public String getEmpleadoGestor() {
		return empleadoGestor;
	}

	public void setEmpleadoGestor(String empleadoGestor) {
		this.empleadoGestor = empleadoGestor;
	}
}