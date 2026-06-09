package com.chernobyl.explorer.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * Clase de configuración para la documentación interactiva de la API mediante OpenAPI/Swagger.
 * Permite personalizar los metadatos de la interfaz gráfica expuesta en /swagger-ui.html.
 */
@Configuration
public class ConfiguracionSwagger {

	/**
	 * Personaliza la información principal de la especificación OpenAPI.
	 * * @return Objeto OpenAPI configurado con título, versión, descripción y contacto oficial.
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("API REST - Chernobyl Exclusion Explorer")
				.version("1.0")
				.description("Documentación oficial de la pasarela lógica para la gestión de reservas, control dosimétrico y expediciones en la Zona de Alienación.")
				.contact(new Contact()
						.name("Jesús Hidalgo Rodríguez")
						.email("proyecto.chernobyl.explorer@gmail.com")));
	}
}