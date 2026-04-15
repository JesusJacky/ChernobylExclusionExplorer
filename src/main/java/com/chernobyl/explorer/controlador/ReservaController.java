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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chernobyl.explorer.entidades.Reserva;
import com.chernobyl.explorer.servicio.ReservaService;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

	@Autowired
	private ReservaService reservaService;

	// GET de todas las reservas
	@GetMapping
	public List<Reserva> listarReservas() {
		return reservaService.listarTodos();
	}

	// GET de UNA reserva
	@GetMapping("/{id}")
	public ResponseEntity<Reserva> obtenerPorId(@PathVariable Integer id) {
		try {
			Reserva resultado = reservaService.buscarPorId(id);
			return ResponseEntity.ok(resultado);
		} catch (Exception e) {
			System.err.println(e);
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/estado/{estado}")
	public ResponseEntity<List<Reserva>> listarPorEstado(@PathVariable String estado) {
		try {
			List<Reserva> resultado = reservaService.listarReservasPorEstado(estado);
			return ResponseEntity.ok(resultado);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/buscar-localizador{localizador}")
	public ResponseEntity<Reserva> buscarPorLocalizador(@PathVariable String localizador) {
		try {
			Reserva reserva = reservaService.buscarReservaPorLocalizador(localizador);
			return ResponseEntity.ok(reserva);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}

	}

	// GET -> alguno que use algún filtro avanzado (relacionado con cliente)
	@GetMapping("/buscar-dni/{dni}")
	public ResponseEntity<List<Reserva>> buscarPorDni(@PathVariable String dni) {
		try {
			List<Reserva> reservas = reservaService.buscarReservasPorDni(dni);
			return ResponseEntity.ok(reservas);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// POST, PUT, DELETE
	@PostMapping
	public ResponseEntity<Reserva> crear(@RequestBody Reserva reserva) {
		Reserva nuevaReserva = reservaService.crear(reserva);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
	}

	@PutMapping("/dni/{dni}")
	public ResponseEntity<Reserva> actualizar(@PathVariable Integer id, @RequestBody Reserva reserva) {
		try {
			Reserva reservaActualizada = reservaService.actualizarReserva(id, reserva, null);
			return ResponseEntity.ok(reservaActualizada);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		try {
			reservaService.eliminar(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}/cancelar")
	public ResponseEntity<?> cancelar(@PathVariable Integer id) {
		try {
			Reserva cancelada = reservaService.cancelarReserva(id);
			return ResponseEntity.ok(cancelada);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error al cancelar: " + e.getMessage());
		}
	}

	@PostMapping("/proceso-reserva")
	public ResponseEntity<Reserva> crearReservaDetallada(@RequestBody(required = false) Reserva reserva,
			@RequestParam Integer idCliente, @RequestParam Integer idPaquete) {
		try {
			Reserva base = (reserva == null) ? new Reserva() : reserva;
			Reserva nueva = reservaService.crearReservaDetallada(base, idCliente, idPaquete);
			return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
		} catch (Exception e) {
			e.printStackTrace(); // Esto es vital para que veas el error real en la consola si falla
			return ResponseEntity.badRequest().build();
		}
	}

	// Controlador para el posible metodo para actualizar reserva

	// PUT: /reservas/actualizar/5/nuevosViajeros=1,2,3
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<Reserva> actualizar(@PathVariable Integer id, @RequestBody Reserva reservaActualizada,
			@RequestParam(required = false) List<Integer> nuevosViajeros) {

		try {
			Reserva actualizada = reservaService.actualizarReserva(id, reservaActualizada, nuevosViajeros);
			return ResponseEntity.ok(actualizada);
		} catch (RuntimeException e) {
			// Si la reserva no existe (lanzado por orElseThrow), devolvemos 404
			if (e.getMessage().contains("no encontrada")) {
				return ResponseEntity.notFound().build();
			}
			// Para otros errores (datos incorrectos), devolvemos 400
			return ResponseEntity.badRequest().build();
		}
	}
}
