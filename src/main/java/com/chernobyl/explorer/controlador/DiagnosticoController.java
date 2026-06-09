package com.chernobyl.explorer.controlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/diagnostico")
@Tag(name = "Telemetría del Servidor", description = "Monitorización en tiempo real del Nodo SBU")
public class DiagnosticoController {

	@Operation(summary = "Obtener estado de los servidores SBU")
	@GetMapping("/estado")
	public ResponseEntity<Map<String, Object>> obtenerEstadoServidor() {
		Map<String, Object> estado = new HashMap<>();

		// 1. Datos de Memoria JVM (En Megabytes)
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory() / (1024 * 1024);
		long totalMemory = runtime.totalMemory() / (1024 * 1024);
		long freeMemory = runtime.freeMemory() / (1024 * 1024);
		long usedMemory = totalMemory - freeMemory;

		// Calculamos porcentaje sobre la memoria total asignada al proceso
		long ramPercent = (maxMemory > 0) ? (usedMemory * 100) / maxMemory : 0;

		estado.put("ramUsada", usedMemory);
		estado.put("ramTotal", maxMemory);
		estado.put("ramPercent", ramPercent);

		// 2. Tiempo de Actividad (Uptime real del servidor)
		long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
		long horas = TimeUnit.MILLISECONDS.toHours(uptimeMillis);
		long minutos = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60;
		long segundos = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60;
		estado.put("uptime", String.format("%02d:%02d:%02d", horas, minutos, segundos));

		// 3. Carga del procesador e Hilos (Simula peticiones activas de Tomcat)
		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
		int activeThreads = ManagementFactory.getThreadMXBean().getThreadCount();

		estado.put("procesadores", osBean.getAvailableProcessors());
		estado.put("peticiones", activeThreads); // Número de hilos trabajando en este instante

		return ResponseEntity.ok(estado);
	}
}