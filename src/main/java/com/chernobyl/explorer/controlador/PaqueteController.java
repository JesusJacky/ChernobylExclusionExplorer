package com.chernobyl.explorer.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chernobyl.explorer.entidades.PaqueteViaje;
import com.chernobyl.explorer.servicio.PaqueteViajeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/paquetes")
@Tag(name = "Paquetes de Expedición", description = "Gestión de las diferentes rutas por la Zona de Exclusión")
public class PaqueteController {

	@Autowired
	private PaqueteViajeService paqueteViajeService;

	@Operation(summary = "Catálogo de expediciones", description = "Lista todas las rutas disponibles con sus respectivos precios y niveles de radiación.")
	@GetMapping
	public List<PaqueteViaje> listarPaquetes() {
		return paqueteViajeService.listarTodos();
	}

	@Operation(summary = "Obtener detalles de expedición", description = "Busca una ruta específica por su ID.")
	@GetMapping("/{id}")
	public ResponseEntity<PaqueteViaje> obtenerPorId(@PathVariable Integer id) {
		PaqueteViaje paquete = paqueteViajeService.buscarPorId(id);
		return ResponseEntity.ok(paquete);
	}

	@Operation(summary = "Filtrar por peligrosidad", description = "Busca expediciones filtrando por nivel: BAJO, MEDIO o ALTO.")
	@GetMapping("/nivel/{nivel}")
	public ResponseEntity<List<PaqueteViaje>> listarPorNivel(@PathVariable String nivel) {
		List<PaqueteViaje> paquetes = paqueteViajeService.listarPorNivel(nivel);
		return ResponseEntity.ok(paquetes);
	}

	@Operation(summary = "Crear nueva expedición", description = "Da de alta un nuevo paquete de viaje en el catálogo.")
	@PostMapping
	public ResponseEntity<PaqueteViaje> crear(@Valid @RequestBody PaqueteViaje paqueteViaje) {
		PaqueteViaje nuevoPaquete = paqueteViajeService.crear(paqueteViaje);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaquete);
	}

	@Operation(summary = "Actualizar paquete", description = "Modifica los precios, descripción o lugares incluidos de una ruta existente.")
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<PaqueteViaje> actualizar(@PathVariable Integer id, @Valid @RequestBody PaqueteViaje viaje) {
		PaqueteViaje paqueteActualizado = paqueteViajeService.actualizar(id, viaje);
		return ResponseEntity.ok(paqueteActualizado);
	}

	@Operation(summary = "Eliminar expedición", description = "Retira un paquete del catálogo.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		paqueteViajeService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}