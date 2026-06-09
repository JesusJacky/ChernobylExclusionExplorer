package com.chernobyl.explorer.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.chernobyl.explorer.dto.MonitorEstadoDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

	private final String API_KEY = "e5f8e2401627dba8d09fd83919e333e3";
	private final String URL = "https://api.openweathermap.org/data/2.5/weather?lat=51.4056&lon=30.0556&appid="
			+ API_KEY + "&units=metric&lang=es";

	@GetMapping("/tiempo-pripyat")
	public MonitorEstadoDTO obtenerTiempoReal() {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String jsonResponse = restTemplate.getForObject(URL, String.class);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(jsonResponse);

			// Extraemos la descripción, la temperatura y el código del icono
			String descripcion = root.path("weather").get(0).path("description").asText();
			String iconoCode = root.path("weather").get(0).path("icon").asText();
			double temp = root.path("main").path("temp").asDouble();

			return new MonitorEstadoDTO(descripcion, 0.12, temp, iconoCode);
		} catch (Exception e) {
			return new MonitorEstadoDTO("No disponible", 0.12, 0.0, "error");
		}
	}
}