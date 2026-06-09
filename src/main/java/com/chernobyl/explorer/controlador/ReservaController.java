package com.chernobyl.explorer.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chernobyl.explorer.dto.PeticionReservaDTO;
import com.chernobyl.explorer.entidades.Reserva;
import com.chernobyl.explorer.servicio.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

	@Autowired
	private ReservaService reservaService;

	// ====================================================================
	// PASARELA WEB CLIENTES
	// ====================================================================
	@PostMapping("/tramitar")
	public ResponseEntity<String> tramitarReservaWeb(Authentication auth, @RequestBody PeticionReservaDTO peticion) {
		if (auth == null || !auth.isAuthenticated())
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		reservaService.tramitarReservaWeb(auth.getName(), peticion);
		return ResponseEntity.ok("Expedición registrada y abonada con éxito.");
	}

	@GetMapping("/mis-expediciones")
	public ResponseEntity<List<Reserva>> misExpediciones(Authentication auth) {
		if (auth == null || !auth.isAuthenticated())
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		return ResponseEntity.ok(reservaService.obtenerMisReservas(auth.getName()));
	}

	// ====================================================================
	// MÉTODOS CRUD GENÉRICOS DE INTRANET
	// ====================================================================
	@GetMapping
	public List<Reserva> listarReservas() {
		return reservaService.listarTodos();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Reserva> obtenerPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(reservaService.buscarPorId(id));
	}

	@GetMapping("/buscar-localizador/{localizador}")
	public ResponseEntity<Reserva> buscarPorLocalizador(@PathVariable String localizador) {
		return ResponseEntity.ok(reservaService.buscarReservaPorLocalizador(localizador));
	}

	// ====================================================================
	// NUEVOS ENDPOINTS: ACTUALIZAR Y CANCELAR (Para Intranet y Cliente)
	// ====================================================================
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<Reserva> actualizarReserva(@PathVariable Integer id,
			@RequestBody Reserva reservaActualizada) {
		// Pasamos null a los nuevos viajeros ya que solo modificamos datos de contacto
		// y fecha
		return ResponseEntity.ok(reservaService.actualizarReserva(id, reservaActualizada, null));
	}

	@PutMapping("/{id}/cancelar")
	public ResponseEntity<Reserva> cancelarReserva(@PathVariable Integer id) {
		return ResponseEntity.ok(reservaService.cancelarReserva(id));
	}

	// ====================================================================
	// MÉTODOS DE APROBACIÓN SBU
	// ====================================================================
	@PutMapping("/{id}/aprobar")
	public ResponseEntity<Reserva> aprobarPorPersonal(@PathVariable Integer id, Authentication auth) {
		return ResponseEntity.ok(reservaService.aprobarReservaPorPersonal(id, auth.getName()));
	}

	@PutMapping("/{id}/rechazar")
	public ResponseEntity<Reserva> rechazarPorPersonal(@PathVariable Integer id, @RequestParam String motivo,
			Authentication auth) {
		return ResponseEntity.ok(reservaService.rechazarReservaPorPersonal(id, motivo, auth.getName()));
	}

	@PutMapping("/actualizar-sbu/{id}")
	public ResponseEntity<Reserva> actualizarReservaSBU(@PathVariable Integer id, @RequestBody Reserva reserva,
			Authentication auth) {
		return ResponseEntity.ok(reservaService.actualizarReservaSBU(id, reserva, auth.getName()));
	}

	// ====================================================================
	// BLOQUE: BANDEJA DE OPERACIONES (Top 10 Reservas Pendientes)
	// ====================================================================
	@GetMapping("/pendientes-urgentes")
	public ResponseEntity<List<Reserva>> obtenerReservasUrgentes() {
		List<Reserva> reservasPendientes = reservaService.listarTodos().stream()
				.filter(r -> "PENDIENTE".equalsIgnoreCase(r.getEstado())).filter(r -> r.getFechaViaje() != null)
				.sorted(java.util.Comparator.comparing(Reserva::getFechaViaje)).limit(10)
				.collect(java.util.stream.Collectors.toList());

		return ResponseEntity.ok(reservasPendientes);
	}
}