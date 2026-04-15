package com.chernobyl.explorer.servicio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.entidades.Cliente;
import com.chernobyl.explorer.entidades.PaqueteViaje;
import com.chernobyl.explorer.entidades.Reserva;
import com.chernobyl.explorer.excepciones.ElementoNoEncontradaException;
import com.chernobyl.explorer.repositorio.ReservaRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservaService {

	@Autowired
	private ReservaRepository reservaRepository;

	/**
	 * Lista todos los clientes
	 * 
	 * @return lista de todos los clientes
	 */
	public List<Reserva> listarTodos() {
		return reservaRepository.findAll();
	}

	public List<Reserva> listarReservasPorEstado(String estado) {
		if (estado == null || estado.isBlank()) {
			throw new ElementoNoEncontradaException("El estado no puede estar vacío");
		}

		List<Reserva> resultado = reservaRepository.findAll().stream()
				.filter(r -> r.getEstado() != null && r.getEstado().equalsIgnoreCase(estado)).sorted((r1, r2) -> {
					if (r1.getFechaViaje() == null && r2.getFechaViaje() == null)
						return 0;
					if (r1.getFechaViaje() == null)
						return 1;
					if (r2.getFechaViaje() == null)
						return -1;
					return r1.getFechaViaje().compareTo(r2.getFechaViaje());
				}).toList();

		if (resultado.isEmpty()) {
			throw new ElementoNoEncontradaException("No se encontraron reservas con el estado: " + estado);
		}
		return resultado;
	}

	/**
	 * Busca un cliente por id
	 * 
	 * @param id
	 * @return un cliente por id
	 */
	public Reserva buscarPorId(Integer id) {
		return reservaRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("Reserva no encontrada con el ID " + id));
	}

	public Reserva buscarReservaPorLocalizador(String localizador) {
		if (localizador == null || localizador.isBlank()) {
			throw new ElementoNoEncontradaException("El localizador no puede estar vacío.");
		}

		return reservaRepository.findAll().stream().filter(r -> localizador.equals(r.getLocalizadorCliente()))
				.findFirst().orElseThrow(() -> new ElementoNoEncontradaException(
						"No se ha encontrado una reserva con el localizador " + localizador));

	}

	/**
	 * Busca reservas por DNI que tiene hecha un cliente
	 * 
	 * @param dni
	 * @return una lista de las reservas que tiene un cliente
	 */
	public List<Reserva> buscarReservasPorDni(String dni) {

		if (dni == null || dni.isBlank()) {
			throw new ElementoNoEncontradaException("El DNI no puede estar vacío.");
		}

		List<Reserva> resultado = reservaRepository.findAll().stream().filter(r -> r.getViajeros() != null
				&& r.getViajeros().stream().anyMatch(viajero -> dni.equals(viajero.getDni()))).toList();

		if (resultado.isEmpty()) {
			throw new ElementoNoEncontradaException("No se encontraron reservas por el DNI " + dni);
		}

		return resultado;
	}

	/**
	 * Crea una nueva reserva
	 * 
	 * @param reserva
	 * @return reserva nueva
	 */
	public Reserva crear(Reserva reserva) {

		if (reserva.getLocalizadorCliente() == null || reserva.getLocalizadorCliente().isBlank()) {
			reserva.generarLocalizador();
		}

		if (reserva.getEstado() == null || reserva.getEstado().isBlank()) {
			reserva.setEstado("PENDIENTE");
		}
		return reservaRepository.save(reserva);
	}

	/**
	 * Elimina una reserva
	 * 
	 * @param id
	 */
	public void eliminar(Integer id) {
		if (!reservaRepository.existsById(id)) {
			throw new ElementoNoEncontradaException("Reserva no encontrada con el ID " + id);
		}
		reservaRepository.deleteById(id);
	}

	public Reserva cancelarReserva(Integer id) {
		Reserva reserva = reservaRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("No se encontró la reserva con el ID " + id));

		if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
			throw new ElementoNoEncontradaException("La reserva con ID " + id + " ya está cancelada.");
		}

		if (reserva.getFechaViaje() == null) {
			throw new ElementoNoEncontradaException("La reserva con ID " + id + " no tiene fecha de viaje asignada.");
		}

		long diasFaltantes = ChronoUnit.DAYS.between(LocalDate.now(), reserva.getFechaViaje());

		if (diasFaltantes < 7) {
			throw new ElementoNoEncontradaException("Lo sentimos, el período de cancelación ha finalizado (faltan "
					+ diasFaltantes + " días). "
					+ "Los trámites administrativos para su entrada en la zona de exclusión están en proceso y no pueden ser revocados.");
		}

		reserva.setEstado("CANCELADA");
		return reservaRepository.save(reserva);
	}

//	public Reserva cancelarReserva(Integer id) {
//		Reserva reserva = reservaRepository.findById(id)
//				.orElseThrow(() -> new ElementoNoEncontradaException("No se encontró la reserva con el ID " + id));
//
//		LocalDate hoy = LocalDate.now();
//		LocalDate fechaViaje = reserva.getFechaViaje();
//		long diasFaltantes = ChronoUnit.DAYS.between(hoy, fechaViaje);
//
//		if (diasFaltantes < 7) {
//			throw new ElementoNoEncontradaException("Lo sentimos, el periódo de cancelación ha finalizado (faltan "
//					+ diasFaltantes + " días)."
//					+ "Los trámites administrativos para su entrada en la zona de exclusión están en proceso y no pueden ser revocados.");
//		}
//
//		reserva.setEstado("CANCELADA");
//		return reservaRepository.save(reserva);
//	}

	@Autowired
	private PaqueteViajeService paqueteViajeService;

	@Autowired
	private ClienteService clienteService;

	/**
	 * Crea una reserva detallada
	 * 
	 * @param reserva
	 * @param id
	 * @param idViajeros
	 * @return
	 */
//	public Reserva crearReservaDetallada(Reserva reserva, Integer idCliente, Integer idPAquete) {
//		Cliente clienteReserva = clienteService.buscarPorId(idCliente);
//		PaqueteViaje paqueteEscogido = paqueteViajeService.buscarPorId(idPAquete);
//
//		if (!clienteReserva.isConsentimiento()) {
//			throw new ElementoNoEncontradaException(
//					"Para hacer la reserva debe aceptar los términos legales de mayoría de edad.");
//		}
//
//		reserva.setTipoPaquete(paqueteEscogido);
//		reserva.setPrecioTotal(paqueteEscogido.getPrecioPaquete());
//
//		List<Cliente> viajeros = new ArrayList<>();
//		viajeros.add(clienteReserva);
//		reserva.setViajeros(viajeros);
//		clienteReserva.setReserva(reserva);
//
//		if (reserva.getLocalizadorCliente() == null || reserva.getLocalizadorCliente().isBlank()) {
//			reserva.generarLocalizador();
//		}
//
//		reserva.setEstado("PENDIENTE");
//
//		return reservaRepository.save(reserva);
//	}
	public Reserva crearReservaDetallada(Reserva reserva, Integer idCliente, Integer idPaquete) {
	    Cliente clienteReserva = clienteService.buscarPorId(idCliente);
	    PaqueteViaje paqueteEscogido = paqueteViajeService.buscarPorId(idPaquete);

	    if (!clienteReserva.isConsentimiento()) {
	        throw new ElementoNoEncontradaException(
	                "Para hacer la reserva debe aceptar los términos legales de mayoría de edad.");
	    }

	    if (reserva.getFechaViaje() == null) {
	        throw new ElementoNoEncontradaException("La reserva debe tener una fecha de viaje.");
	    }

	    reserva.setTipoPaquete(paqueteEscogido);
	    reserva.setPrecioTotal(paqueteEscogido.getPrecioPaquete());

	    List<Cliente> viajeros = new ArrayList<>();
	    viajeros.add(clienteReserva);
	    reserva.setViajeros(viajeros);
	    clienteReserva.setReserva(reserva);

	    if (reserva.getLocalizadorCliente() == null || reserva.getLocalizadorCliente().isBlank()) {
	        reserva.generarLocalizador();
	    }

	    reserva.setEstado("PENDIENTE");

	    return reservaRepository.save(reserva);
	}

	// Posible metodo para actualizar reserva pero tengo dudas para dejarlo o
	// eliminarlo
	// Actualizar una reserva ya existente
//	@Transactional
//	public Reserva actualizarReserva(Integer idReserva, Reserva reservaActualizada, List<Integer> nuevosViajerosIds) {
//		// 1. Buscamos la reserva original
//		Reserva reservaExistente = reservaRepository.findById(idReserva)
//				.orElseThrow(() -> new ElementoNoEncontradaException("Reserva no encontrada"));
//
//		// 2. Validación de aforo (la que ya tenías)
//		if (nuevosViajerosIds != null && !nuevosViajerosIds.isEmpty()) {
//			int plazasLibresActuales = comprobarPlazasRestantesReserva(idReserva);
//			if (nuevosViajerosIds.size() > plazasLibresActuales) {
//				throw new ElementoNoEncontradaException("No hay plazas suficientes. Quedan " + plazasLibresActuales);
//			}
//		}
//
//		// 3. Actualizamos datos básicos
//		reservaExistente.setFechaViaje(reservaActualizada.getFechaViaje());
//		reservaExistente.setEmailContacto(reservaActualizada.getEmailContacto());
//		reservaExistente.setObservaciones(reservaActualizada.getObservaciones());
//		reservaExistente.setTelefono(reservaActualizada.getTelefono());
//
//		// 4. Gestionamos los viajeros y la relación bidireccional
//		if (nuevosViajerosIds != null && !nuevosViajerosIds.isEmpty()) {
//			List<Cliente> listaActual = reservaExistente.getViajeros();
//			for (Integer id : nuevosViajerosIds) {
//				Cliente cliente = clienteService.buscarPorId(id);
//				if (!listaActual.contains(cliente)) {
//					cliente.setReserva(reservaExistente); // ¡Vital para la FK!
//					listaActual.add(cliente);
//				}
//			}
//		}
//
//		// 5. Guardamos en la base de datos
//		Reserva guardada = reservaRepository.save(reservaExistente);
//
//		// 6. CALCULAMOS LAS PLAZAS RESTANTES PARA LA WEB
//		// Llamamos a tu método de conteo después de la actualización
//		int trasActualizar = comprobarPlazasRestantesReserva(idReserva);
//		guardada.setPlazasLibres(trasActualizar);
//
//		return guardada;
//	}
	@Transactional
	public Reserva actualizarReserva(Integer idReserva, Reserva reservaActualizada, List<Integer> nuevosViajerosIds) {
	    Reserva reservaExistente = reservaRepository.findById(idReserva)
	            .orElseThrow(() -> new ElementoNoEncontradaException("Reserva no encontrada con ID " + idReserva));

	    if ("CANCELADA".equalsIgnoreCase(reservaExistente.getEstado())) {
	        throw new ElementoNoEncontradaException("No se puede modificar una reserva cancelada.");
	    }

	    if (nuevosViajerosIds != null && !nuevosViajerosIds.isEmpty()) {
	        int plazasLibresActuales = comprobarPlazasRestantesReserva(idReserva);
	        if (nuevosViajerosIds.size() > plazasLibresActuales) {
	            throw new ElementoNoEncontradaException("No hay plazas suficientes. Quedan " + plazasLibresActuales);
	        }

	        List<Cliente> listaActual = reservaExistente.getViajeros();
	        for (Integer id : nuevosViajerosIds) {
	            Cliente cliente = clienteService.buscarPorId(id);
	            if (!listaActual.contains(cliente)) {
	                cliente.setReserva(reservaExistente);
	                listaActual.add(cliente);
	            }
	        }
	    }

	    reservaExistente.setFechaViaje(reservaActualizada.getFechaViaje());
	    reservaExistente.setEmailContacto(reservaActualizada.getEmailContacto());
	    reservaExistente.setObservaciones(reservaActualizada.getObservaciones());
	    reservaExistente.setTelefono(reservaActualizada.getTelefono());

	    Reserva guardada = reservaRepository.save(reservaExistente);
	    guardada.setPlazasLibres(comprobarPlazasRestantesReserva(idReserva));

	    return guardada;
	}

//	public int comprobarPlazasRestantesReserva(Integer idReserva) {
//
//		Reserva reservaActual = reservaRepository.findById(idReserva)
//				.orElseThrow(() -> new ElementoNoEncontradaException("Reserva no encontrada"));
//
//		final int MAX_PLAZAS = 10;
//
//		LocalDate fechaReserva = reservaActual.getFechaViaje();
//		int plazasOcupadas = reservaRepository.findAll().stream().filter(r -> r.getFechaViaje().equals(fechaReserva))
//				.filter(r -> !"CANCELADA".equals(r.getEstado())).mapToInt(r -> r.getViajeros().size()).sum();
//
//		int plazasLibres = MAX_PLAZAS - plazasOcupadas;
//
//		return Math.max(0, plazasLibres);
//	}
	public int comprobarPlazasRestantesReserva(Integer idReserva) {
	    Reserva reservaActual = reservaRepository.findById(idReserva)
	            .orElseThrow(() -> new ElementoNoEncontradaException("Reserva no encontrada con ID " + idReserva));

	    if (reservaActual.getFechaViaje() == null) {
	        throw new ElementoNoEncontradaException("La reserva con ID " + idReserva + " no tiene fecha de viaje asignada.");
	    }

	    final int MAX_PLAZAS = 10;
	    LocalDate fechaReserva = reservaActual.getFechaViaje();

	    int plazasOcupadas = reservaRepository.findAll().stream()
	            .filter(r -> fechaReserva.equals(r.getFechaViaje()))
	            .filter(r -> r.getViajeros() != null)
	            .filter(r -> !"CANCELADA".equalsIgnoreCase(r.getEstado()))
	            .mapToInt(r -> r.getViajeros().size())
	            .sum();

	    return Math.max(0, MAX_PLAZAS - plazasOcupadas);
	}
}
