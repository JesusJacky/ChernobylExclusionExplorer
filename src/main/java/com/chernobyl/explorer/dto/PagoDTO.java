package com.chernobyl.explorer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO (Data Transfer Object) para el procesamiento de pagos.
 * * Un DTO es un objeto plano que se utiliza exclusivamente para transportar datos
 * entre el cliente (Front-End) y el servidor (Back-End) a través de la API REST.
 * * ¿Por qué se usa?
 * 1. Seguridad: Evita exponer el diseño de las entidades de la base de datos.
 * 2. Limpieza: Solo transporta los campos estrictamente necesarios para una operación.
 * 3. Eficiencia: Evita el problema de cargar relaciones infinitas en memoria.
 * * En este caso, recoge los datos efímeros de la tarjeta de crédito para simular 
 * una pasarela de pago sin llegar a persistir estos datos sensibles en la base de datos.
 */
@Schema(description = "Objeto de Transferencia de Datos (DTO) que encapsula la información efímera de la tarjeta de crédito para procesar el pago de una expedición.")
public class PagoDTO {
	
	@Schema(description = "Número de la tarjeta de crédito sin espacios ni guiones.", example = "1111222233334444", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "El número de tarjeta es obligatorio")
	@Pattern(regexp = "^[0-9]{16}$", message = "El número de tarjeta debe tener exactamente 16 dígitos")
	private String numeroTarjeta;

	@Schema(description = "Nombre completo del titular tal y como aparece en la tarjeta.", example = "Jesús Hidalgo Rodríguez", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "El nombre del titular es obligatorio")
	private String titular;

	@Schema(description = "Código de seguridad de 3 dígitos (CVV) situado en el reverso de la tarjeta.", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "El CVV es obligatorio")
	@Pattern(regexp = "^[0-9]{3}$", message = "El CVV debe tener exactamente 3 dígitos")
	private String cvv;

	public PagoDTO() {}

	public String getNumeroTarjeta() { return numeroTarjeta; }
	public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

	public String getTitular() { return titular; }
	public void setTitular(String titular) { this.titular = titular; }

	public String getCvv() { return cvv; }
	public void setCvv(String cvv) { this.cvv = cvv; }
}