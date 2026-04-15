package com.chernobyl.explorer.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chernobyl.explorer.entidades.Cliente;
import com.chernobyl.explorer.servicio.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping
	public List<Cliente> listarClientes() {
		return clienteService.listarTodos();
	}

	@Operation(summary = "Obtener cliente por ID", description = "Busca un explorador por su identificador único.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Cliente encontrado con éxito."),
			@ApiResponse(responseCode = "404", description = "No se encontró ningún cliente con ese ID") })
	@GetMapping("/{id}")
	public ResponseEntity<Cliente> obtenerPorId(@PathVariable Integer id) {
		Cliente clienteObtenido = clienteService.buscarPorId(id);
		return ResponseEntity.ok(clienteObtenido);
	}

	@Operation(summary = "Registrar nuevo cliente", description = "Crea un nuevo explorador en la base de datos.")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Cliente creado con éxito."),
			@ApiResponse(responseCode = "400", description = "Datos de creación invalidos") })
	@PostMapping
	public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
		Cliente nuevoCliente = clienteService.crear(cliente);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
	}

	@Operation(summary = "Eliminar cliente", description = "Elimina a un cliente mediante su ID.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Cliente eliminado correctamente."),
			@ApiResponse(responseCode = "404", description = "Cliente no encontrado.") })
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		clienteService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Buscar por nacionalidad y mayor de edad", description = "Filtra los exploradores por nacionadlidad y mayoría de edad.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Búsqueda realizada con éxito."),
			@ApiResponse(responseCode = "404", description = "No hay clientes con esa nacionalidad y/o mayores de edad.") })
	@GetMapping("/buscar-nacionalidad-mayores")
	public ResponseEntity<List<Cliente>> buscarPorNacionalidadYMayorEdad(@RequestParam String nacionalidad) {
		List<Cliente> clientes = clienteService.buscarPorNacionalidadYMayorDeEdad(nacionalidad);
		return ResponseEntity.ok(clientes);
	}

	@Operation(summary = "Busca por menor de edad", description = "Filtra los exploradores por minoría de edad.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Búsqueda realizada con éxito."),
			@ApiResponse(responseCode = "404", description = "No hay clientes menores de edad.")})
	@GetMapping("/buscar-menores")
	public ResponseEntity<List<Cliente>> filtrarPorMenorDeEdad() {
		List<Cliente> clientes = clienteService.filtrarPorMenorDeEdad();
		return ResponseEntity.ok(clientes);
	}

}
