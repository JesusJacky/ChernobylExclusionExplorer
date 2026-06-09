package com.chernobyl.explorer.entidades;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

/**
 * Entidad que representa las credenciales de acceso al sistema (Login).
 * Independiente de la entidad Cliente o Personal, esta clase se encarga
 * exclusivamente de la seguridad y los roles bajo el marco de Spring Security.
 */
@Entity
@Schema(description = "Entidad para el manejo de credenciales y roles de acceso (Login).")
public class Usuario extends DomainEntity {

	@Schema(description = "Nombre de usuario o identificador para iniciar sesión.", example = "admin_pripyat", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String cuenta;

	@Schema(description = "Contraseña en texto plano al recibirla, se almacena encriptada mediante BCrypt.", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String clave;

	@Schema(description = "Rol del usuario en el sistema. Debe ir precedido por 'ROLE_'.", example = "ROLE_CLIENTE", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	private String rol;

	@Schema(description = "Fecha y hora de la última interacción del usuario con el sistema.", example = "2026-06-08T19:53:55")
	private LocalDateTime ultimaConexion;

	// ====================================================================
	// CONSTRUCTORES
	// ====================================================================
	public Usuario() {
		super();
	}

	public Usuario(String cuenta, String clave, String rol, LocalDateTime ultimaConexion) {
		super();
		this.cuenta = cuenta;
		this.clave = clave;
		this.rol = rol;
		this.ultimaConexion = ultimaConexion;
	}

	// ====================================================================
	// GETTERS Y SETTERS
	// ====================================================================
	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public LocalDateTime getUltimaConexion() {
		return ultimaConexion;
	}

	public void setUltimaConexion(LocalDateTime ultimaConexion) {
		this.ultimaConexion = ultimaConexion;
	}
}