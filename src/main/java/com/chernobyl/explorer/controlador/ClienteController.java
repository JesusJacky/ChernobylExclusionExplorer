package com.chernobyl.explorer.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chernobyl.explorer.entidades.Cliente;
import com.chernobyl.explorer.servicio.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Fichas de Clientes", description = "Operaciones CRUD y filtros para los expedicionarios")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@Operation(summary = "Obtener mis datos", description = "Devuelve los datos del cliente que ha iniciado sesión actualmente para autocompletar formularios.")
	@GetMapping("/mi-perfil")
	public ResponseEntity<Cliente> obtenerMiPerfil(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getName().equals("anonymousUser")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// CORRECCIÓN: Filtrado por stream en memoria, sin inventar métodos en el
		// repositorio
		Cliente cliente = clienteService.listarTodos().stream()
				.filter(c -> c.getUsuario() != null && authentication.getName().equals(c.getUsuario().getCuenta()))
				.findFirst().orElse(null);

		if (cliente == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(cliente);
	}

	@Operation(summary = "Listar todos los clientes", description = "Devuelve el registro completo de personas dadas de alta en el sistema.")
	@GetMapping
	public List<Cliente> listarClientes() {
		return clienteService.listarTodos();
	}

	@Operation(summary = "Dar de baja cliente", description = "Desactiva el perfil lógicamente sin purgar sus datos históricos de la base de datos.")
	@PutMapping("/{id}/baja")
	public ResponseEntity<Void> darDeBajaLógica(@PathVariable Integer id) {
		clienteService.darDeBaja(id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Obtener cliente por ID", description = "Busca un explorador por su identificador único de base de datos.")
	@GetMapping("/{id}")
	public ResponseEntity<Cliente> obtenerPorId(@PathVariable Integer id) {
		Cliente cliente = clienteService.buscarPorId(id);
		return ResponseEntity.ok(cliente);
	}

	@Operation(summary = "Filtrar por nacionalidad", description = "Busca clientes de un país específico.")
	@GetMapping("/buscar-nacionalidad-mayores")
	public ResponseEntity<List<Cliente>> buscarPorNacionalidadYMayorEdad(@RequestParam String nacionalidad) {
		// CORRECCIÓN: Llamamos al método nuevo que creamos ayer en ClienteService
		List<Cliente> clientes = clienteService.buscarPorNacionalidad(nacionalidad);
		return ResponseEntity.ok(clientes);
	}

	@Operation(summary = "Alerta de menores", description = "Uso interno. Filtra clientes que no han alcanzado la mayoría de edad.")
	@GetMapping("/buscar-menores")
	public ResponseEntity<List<Cliente>> filtrarPorMenorDeEdad() {
		List<Cliente> clientes = clienteService.filtrarPorMenorDeEdad();
		return ResponseEntity.ok(clientes);
	}

	@Operation(summary = "Búsqueda por DNI", description = "Encuentra el perfil exacto de un cliente mediante su documento de identidad.")
	@GetMapping("/buscar-dni/{dni}")
	public ResponseEntity<Cliente> buscarPorDni(@PathVariable String dni) {
		Cliente cliente = clienteService.buscarPorDni(dni);
		return ResponseEntity.ok(cliente);
	}

	@Operation(summary = "Dar de alta cliente", description = "Registra un nuevo cliente aplicando validaciones de edad y formato.")
	@PostMapping
	public ResponseEntity<Cliente> crear(@Valid @RequestBody Cliente cliente) {
		Cliente nuevoCliente = clienteService.crear(cliente);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
	}

	@Operation(summary = "Modificar ficha de cliente", description = "Actualiza los datos personales de un explorador (El DNI no puede ser modificado por seguridad).")
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<Cliente> actualizar(@PathVariable Integer id, @Valid @RequestBody Cliente detallesCliente) {
		Cliente clienteActualizado = clienteService.actualizar(id, detallesCliente);
		return ResponseEntity.ok(clienteActualizado);
	}

	@Operation(summary = "Eliminar cliente", description = "Borra físicamente a un cliente de la base de datos.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		clienteService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	// ====================================================================
	// BLOQUE: BANDEJA DE OPERACIONES (Personal Activo SBU)
	// ====================================================================
	@Operation(summary = "Listar empleados conectados", description = "Devuelve los trabajadores de la BD que están activos.")
	@GetMapping("/empleados-activos")
	public ResponseEntity<java.util.List<java.util.Map<String, Object>>> obtenerEmpleadosActivos() {
		java.time.LocalDateTime limite = java.time.LocalDateTime.now().minusMinutes(5);

		return ResponseEntity.ok(clienteService.listarTodos().stream()
				.filter(c -> c.getUsuario() != null && !"ROLE_CLIENTE".equals(c.getUsuario().getRol()))
				.filter(c -> Boolean.TRUE.equals(c.getActivo())).map(c -> {
					boolean online = c.getUsuario().getUltimaConexion() != null
							&& c.getUsuario().getUltimaConexion().isAfter(limite);

					return java.util.Map.<String, Object>of("id", c.getId(), // AÑADIDO: Necesario para identificar al
																				// empleado en el Modal
							"nombre", c.getNombre() + " " + (c.getApellido1() != null ? c.getApellido1() : ""), "rol",
							c.getUsuario().getRol().replace("ROLE_", ""), "online", online, "ultimaConexion",
							c.getUsuario().getUltimaConexion() != null ? c.getUsuario().getUltimaConexion()
									.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "N/A",
							"email", c.getEmail() != null ? c.getEmail() : "", "telefono",
							c.getTelefono() != null ? c.getTelefono() : "");
				}).collect(java.util.stream.Collectors.toList()));
	}

	// ====================================================================
	// BLOQUE: HERRAMIENTAS DE AUDITORÍA E INTELIGENCIA SBU
	// ====================================================================
	@Operation(summary = "Auditoría por DNI", description = "Búsqueda exacta de un expediente por su documento de identidad.")
	@GetMapping("/auditoria/dni/{dni}")
	public ResponseEntity<?> auditarPorDni(@PathVariable String dni) {
		try {
			return ResponseEntity.ok(clienteService.buscarPorDni(dni));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@Operation(summary = "Auditoría de Menores de Edad", description = "Devuelve una lista con todos los clientes activos menores de 18 años.")
	@GetMapping("/auditoria/menores")
	public ResponseEntity<?> auditarMenores() {
		try {
			return ResponseEntity.ok(clienteService.filtrarPorMenorDeEdad());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@Operation(summary = "Auditoría Demográfica", description = "Filtra el censo de exploradores según su país de origen.")
	@GetMapping("/auditoria/nacionalidad")
	public ResponseEntity<?> auditarNacionalidad(@RequestParam String pais) {
		try {
			return ResponseEntity.ok(clienteService.buscarPorNacionalidad(pais));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@Operation(summary = "Búsqueda por Teléfono", description = "Encuentra el perfil de un cliente mediante su número de contacto.")
	@GetMapping("/buscar-telefono/{telefono}")
	public ResponseEntity<Cliente> buscarPorTelefono(@PathVariable String telefono) {
		Cliente cliente = clienteService.buscarPorTelefono(telefono);
		return ResponseEntity.ok(cliente);
	}

	@Operation(summary = "Reactivar cliente", description = "Vuelve a activar un perfil que había sido dado de baja.")
	@PutMapping("/{id}/alta")
	public ResponseEntity<Void> reactivarLogicamente(@PathVariable Integer id) {
		clienteService.reactivar(id);
		return ResponseEntity.ok().build();
	}
}