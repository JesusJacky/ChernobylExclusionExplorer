package com.chernobyl.explorer.servicio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.dto.PagoDTO;
import com.chernobyl.explorer.dto.PeticionReservaDTO;
import com.chernobyl.explorer.entidades.Cliente;
import com.chernobyl.explorer.entidades.PaqueteViaje;
import com.chernobyl.explorer.entidades.Reserva;
import com.chernobyl.explorer.excepciones.CapacidadExcedidaException;
import com.chernobyl.explorer.excepciones.ElementoNoEncontradaException;
import com.chernobyl.explorer.excepciones.PagoInvalidoException;
import com.chernobyl.explorer.excepciones.ValidacionNegocioException;
import com.chernobyl.explorer.repositorio.ReservaRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio principal (Core) de la aplicación. Gestiona el ciclo de vida de las
 * expediciones: creación, aprobación, pagos, cancelaciones y el cálculo médico
 * de dosimetría (radiación acumulada).
 */
@Service
public class ReservaService {

	@Autowired
	private ReservaRepository reservaRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PaqueteViajeService paqueteViajeService;

	@Autowired
	private ClienteService clienteService;

	/**
	 * Lista todas las reservas del sistema sin filtros. * @return Lista de
	 * {@link Reserva}.
	 */
	public List<Reserva> listarTodos() {
		return reservaRepository.findAll();
	}

	/**
	 * Lista reservas filtrando por su estado (PENDIENTE, ACEPTADA, PAGADA,
	 * CANCELADA). Ordena los resultados por fecha de viaje (las más próximas
	 * primero). * @param estado El estado exacto a buscar. * @return Lista ordenada
	 * de reservas.
	 * 
	 * @throws ValidacionNegocioException    Si el estado es nulo o vacío.
	 * @throws ElementoNoEncontradaException Si no hay reservas en ese estado.
	 */
	public List<Reserva> listarReservasPorEstado(String estado) {
		if (estado == null || estado.isBlank()) {
			throw new ValidacionNegocioException("El estado no puede estar vacío");
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
	 * Recupera una reserva a través de su ID interno. * @param id El identificador
	 * único de la base de datos. * @return La reserva encontrada.
	 * 
	 * @throws ElementoNoEncontradaException Si no existe la reserva.
	 */
	public Reserva buscarPorId(Integer id) {
		return reservaRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("Reserva no encontrada con el ID " + id));
	}

	/**
	 * Busca una reserva utilizando el Localizador (ej. LOC-001) proporcionado al
	 * cliente. * @param localizador Código alfanumérico generado en la reserva.
	 * * @return La reserva correspondiente.
	 * 
	 * @throws ElementoNoEncontradaException Si el localizador no existe.
	 */
	public Reserva buscarReservaPorLocalizador(String localizador) {
		if (localizador == null || localizador.isBlank()) {
			throw new ValidacionNegocioException("El localizador no puede estar vacío.");
		}

		return reservaRepository.findAll().stream().filter(r -> localizador.equals(r.getLocalizadorCliente()))
				.findFirst().orElseThrow(() -> new ElementoNoEncontradaException(
						"No se ha encontrado una reserva con el localizador " + localizador));
	}

	/**
	 * Localiza todas las expediciones en las que un cliente específico participa
	 * como viajero. * @param dni El DNI del cliente. * @return Lista de reservas
	 * asociadas a ese DNI.
	 * 
	 * @throws ElementoNoEncontradaException Si el DNI no está en ninguna reserva.
	 */
	public List<Reserva> buscarReservasPorDni(String dni) {
		if (dni == null || dni.isBlank()) {
			throw new ValidacionNegocioException("El DNI no puede estar vacío.");
		}

		List<Reserva> resultado = reservaRepository.findAll().stream().filter(r -> r.getViajeros() != null
				&& r.getViajeros().stream().anyMatch(viajero -> dni.equals(viajero.getDni()))).toList();

		if (resultado.isEmpty()) {
			throw new ElementoNoEncontradaException("No se encontraron reservas por el DNI " + dni);
		}

		return resultado;
	}

	/**
	 * Crea una reserva básica inicializando su localizador y estado a PENDIENTE.
	 * * @param reserva El objeto reserva a persistir. * @return La reserva
	 * persistida.
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
	 * Elimina una reserva físicamente de la base de datos. Solo recomendado para
	 * mantenimiento. * @param id El ID de la reserva.
	 */
	public void eliminar(Integer id) {
		if (!reservaRepository.existsById(id)) {
			throw new ElementoNoEncontradaException("Reserva no encontrada con el ID " + id);
		}
		reservaRepository.deleteById(id);
	}

	/**
	 * Permite a un cliente cancelar su expedición. Aplica una regla de negocio
	 * estricta: No se puede cancelar si faltan menos de 7 días. * @param id El ID
	 * de la reserva a cancelar. * @return La reserva con el estado cambiado a
	 * CANCELADA.
	 * 
	 * @throws ValidacionNegocioException Si el plazo de 7 días ha expirado.
	 */
	public Reserva cancelarReserva(Integer id) {
		Reserva reserva = reservaRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("No se encontró la reserva con el ID " + id));

		if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
			throw new ValidacionNegocioException("La reserva con ID " + id + " ya está cancelada.");
		}

		if (reserva.getFechaViaje() == null) {
			throw new ValidacionNegocioException("La reserva con ID " + id + " no tiene fecha de viaje asignada.");
		}

		long diasFaltantes = ChronoUnit.DAYS.between(LocalDate.now(), reserva.getFechaViaje());

		if (diasFaltantes < 7) {
			throw new ValidacionNegocioException("Lo sentimos, el período de cancelación ha finalizado (faltan "
					+ diasFaltantes + " días). "
					+ "Los trámites administrativos para su entrada en la zona de exclusión están en proceso y no pueden ser revocados.");
		}

		reserva.setEstado("CANCELADA");
		return reservaRepository.save(reserva);
	}

	/**
	 * Ensambla una reserva completa desde el portal web. Vincula al cliente
	 * principal, asigna el paquete, calcula el precio y envía un correo de
	 * confirmación. * @param reserva Los datos rellenados en el formulario web.
	 * * @param idCliente El cliente que realiza la compra.
	 * 
	 * @param idPaquete El paquete de expedición seleccionado.
	 * @return La reserva generada en estado PENDIENTE.
	 * @throws ValidacionNegocioException Si el cliente no ha aceptado el
	 *                                    consentimiento de términos y condiciones.
	 */
	public Reserva crearReservaDetallada(Reserva reserva, Integer idCliente, Integer idPaquete) {
		Cliente clienteReserva = clienteService.buscarPorId(idCliente);
		PaqueteViaje paqueteEscogido = paqueteViajeService.buscarPorId(idPaquete);

		if (!clienteReserva.isConsentimiento()) {
			throw new ValidacionNegocioException(
					"Para hacer la reserva debe aceptar los términos legales de mayoría de edad.");
		}

		if (reserva.getFechaViaje() == null) {
			throw new ValidacionNegocioException("La reserva debe tener una fecha de viaje.");
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

		Reserva reservaGuardada = reservaRepository.save(reserva);

		try {
			emailService.enviarResguardoReserva(reservaGuardada.getEmailContacto(), clienteReserva, reservaGuardada);
		} catch (Exception e) {
			System.out.println("Aviso: No se pudo enviar el correo. Detalle: " + e.getMessage());
		}

		return reservaGuardada;
	}

	/**
	 * Actualiza los datos de una expedición e incorpora viajeros adicionales.
	 * Verifica matemáticamente que no se exceda el aforo máximo de 10 personas por
	 * día. * @param idReserva ID de la reserva a modificar. * @param
	 * reservaActualizada Los nuevos datos (fecha, observaciones).
	 * 
	 * @param nuevosViajerosIds Lista de IDs de los clientes acompañantes.
	 * @return La reserva con los viajeros incorporados.
	 * @throws CapacidadExcedidaException Si el número de nuevos viajeros supera las
	 *                                    plazas del grupo.
	 */
	@Transactional
	public Reserva actualizarReserva(Integer idReserva, Reserva reservaActualizada, List<Integer> nuevosViajerosIds) {
		Reserva reservaExistente = reservaRepository.findById(idReserva)
				.orElseThrow(() -> new ElementoNoEncontradaException("Reserva no encontrada con ID " + idReserva));

		if ("CANCELADA".equalsIgnoreCase(reservaExistente.getEstado())) {
			throw new ValidacionNegocioException("No se puede modificar una reserva cancelada.");
		}

		if (nuevosViajerosIds != null && !nuevosViajerosIds.isEmpty()) {
			int plazasLibresActuales = comprobarPlazasRestantesReserva(idReserva);
			if (nuevosViajerosIds.size() > plazasLibresActuales) {
				throw new CapacidadExcedidaException("No hay plazas suficientes. Quedan " + plazasLibresActuales);
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

	/**
	 * Algoritmo que calcula el aforo disponible para la fecha de una reserva en
	 * concreto. El aforo máximo del parque es de 10 plazas diarias. * @param
	 * idReserva La reserva sobre la que se consulta. * @return Número entero con
	 * las plazas disponibles (mínimo 0).
	 */
	public int comprobarPlazasRestantesReserva(Integer idReserva) {
		Reserva reservaActual = reservaRepository.findById(idReserva)
				.orElseThrow(() -> new ElementoNoEncontradaException("Reserva no encontrada con ID " + idReserva));

		if (reservaActual.getFechaViaje() == null) {
			throw new ValidacionNegocioException(
					"La reserva con ID " + idReserva + " no tiene fecha de viaje asignada.");
		}

		final int MAX_PLAZAS = 10;
		LocalDate fechaReserva = reservaActual.getFechaViaje();

		int plazasOcupadas = reservaRepository.findAll().stream().filter(r -> fechaReserva.equals(r.getFechaViaje()))
				.filter(r -> r.getViajeros() != null).filter(r -> !"CANCELADA".equalsIgnoreCase(r.getEstado()))
				.mapToInt(r -> r.getViajeros().size()).sum();

		return Math.max(0, MAX_PLAZAS - plazasOcupadas);
	}

	/**
	 * Verifica si un día concreto está lleno (>= 10 reservas en total). En caso de
	 * estar lleno, sugiere el día más próximo disponible lanzando una excepción
	 * informativa. * @param fechaViaje Fecha elegida por el usuario. * @throws
	 * CapacidadExcedidaException Si la fecha está llena, informando de la siguiente
	 * fecha libre.
	 */
	public void comprobarDisponibilidadDia(LocalDate fechaViaje) {
		final int MAX_RESERVAS_POR_DIA = 10;

		List<Reserva> reservasValidas = reservaRepository.findAll().stream()
				.filter(r -> r.getFechaViaje() != null && !"CANCELADA".equalsIgnoreCase(r.getEstado())).toList();

		long reservasEseDia = reservasValidas.stream().filter(r -> r.getFechaViaje().equals(fechaViaje)).count();

		if (reservasEseDia >= MAX_RESERVAS_POR_DIA) {
			LocalDate proximoDia = fechaViaje.plusDays(1);

			while (true) {
				final LocalDate diaAComprobar = proximoDia;
				long reservasProximoDia = reservasValidas.stream().filter(r -> r.getFechaViaje().equals(diaAComprobar))
						.count();

				if (reservasProximoDia < MAX_RESERVAS_POR_DIA) {
					throw new CapacidadExcedidaException("El día " + fechaViaje
							+ " no tiene reservas disponibles. El siguiente día más cercano disponible es el "
							+ proximoDia + ".");
				}
				proximoDia = proximoDia.plusDays(1);
			}
		}
	}

	/**
	 * Simula la lógica de una pasarela de pago bancaria. Valida la tarjeta, genera
	 * un hash de transacción y envía el recibo al correo del cliente. * @param
	 * idReserva ID de la reserva a abonar. * @param pago Objeto DTO que transporta
	 * los datos sensibles de la tarjeta.
	 * 
	 * @return Cadena con el Hash único de transacción (ej. TX-5B3C8A).
	 * @throws PagoInvalidoException Si la tarjeta es inválida.
	 */
	public String procesarPago(Integer idReserva, PagoDTO pago) {
		Reserva reserva = buscarPorId(idReserva);

		if (!"PENDIENTE".equalsIgnoreCase(reserva.getEstado())) {
			throw new ValidacionNegocioException("La reserva ya ha sido pagada o se encuentra cancelada.");
		}

		if (pago.getNumeroTarjeta() == null || pago.getNumeroTarjeta().length() < 16) {
			throw new PagoInvalidoException("Número de tarjeta inválido o incompleto.");
		}

		reserva.setEstado("PAGADA");
		reservaRepository.save(reserva);

		String hashTransaccion = "TX-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

		try {
			emailService.enviarReciboPago(reserva.getEmailContacto(), reserva, hashTransaccion);
		} catch (Exception e) {
			System.out.println("Aviso: Fallo al enviar el recibo de pago: " + e.getMessage());
		}

		return hashTransaccion;
	}

	/**
	 * Calcula la dosimetría acumulada (mSv) de un cliente durante el año en curso.
	 * Fundamental para el protocolo de seguridad y salud radiológica. * @param dni
	 * Documento de identidad del expedicionario. * @return Suma de miliSieverts
	 * (Double) absorbidos en expediciones este año.
	 */
	public Double calcularRadiacionAnualCliente(String dni) {
		int anioActual = LocalDate.now().getYear();

		List<Reserva> reservasCliente;
		try {
			reservasCliente = buscarReservasPorDni(dni);
		} catch (ElementoNoEncontradaException e) {
			return 0.0;
		}

		return reservasCliente.stream()
				.filter(r -> r.getFechaViaje() != null && r.getFechaViaje().getYear() == anioActual)
				.filter(r -> !"CANCELADA".equalsIgnoreCase(r.getEstado()))
				.filter(r -> r.getTipoPaquete() != null && r.getTipoPaquete().getDosisEstimadaMsv() != null)
				.mapToDouble(r -> r.getTipoPaquete().getDosisEstimadaMsv()).sum();
	}

	/**
	 * Lógica de Intranet: Autoriza una expedición previamente solicitada. Cambia el
	 * estado a ACEPTADA y notifica al cliente con la normativa pertinente. * @param
	 * idReserva ID de la solicitud PENDIENTE. * @return La reserva autorizada.
	 */
	public Reserva aprobarReservaPorPersonal(Integer idReserva, String empleado) {
		Reserva reserva = buscarPorId(idReserva);
		if (!"PENDIENTE".equalsIgnoreCase(reserva.getEstado())) {
			throw new ValidacionNegocioException("Solo se pueden aprobar solicitudes en estado PENDIENTE.");
		}
		reserva.setEstado("ACEPTADA");
		reserva.setEmpleadoGestor(empleado);
		Reserva reservaGuardada = reservaRepository.save(reserva);

		try {
			emailService.enviarAvisoAceptacion(reservaGuardada.getEmailContacto(), reservaGuardada);
		} catch (Exception e) {
			System.out.println("Aviso correo: " + e.getMessage());
		}

		return reservaGuardada;
	}

	/**
	 * Lógica de Intranet: Deniega una expedición por motivos logísticos o de
	 * seguridad (clima, radiación alta, aforo). Cancela la reserva, registra el
	 * motivo y notifica sobre devoluciones bancarias. * @param idReserva ID de la
	 * solicitud a denegar. * @param motivo Texto explicativo que recibirá el
	 * cliente.
	 * 
	 * @return La reserva cancelada.
	 */
	public Reserva rechazarReservaPorPersonal(Integer idReserva, String motivo, String empleado) {
		Reserva reserva = buscarPorId(idReserva);
		if (!"PENDIENTE".equalsIgnoreCase(reserva.getEstado())) {
			throw new ValidacionNegocioException("Solo se pueden rechazar solicitudes en estado PENDIENTE.");
		}
		if (motivo == null || motivo.isBlank()) {
			throw new ValidacionNegocioException("Debe proporcionar un motivo para la cancelación.");
		}
		reserva.setEstado("CANCELADA");
		reserva.setEmpleadoGestor(empleado);
		Reserva reservaGuardada = reservaRepository.save(reserva);

		try {
			emailService.enviarAvisoCancelacionPorPersonal(reservaGuardada.getEmailContacto(), reservaGuardada, motivo);
		} catch (Exception e) {
			System.out.println("Aviso correo: " + e.getMessage());
		}

		return reservaGuardada;
	}

	/**
	 * Lógica de Intranet: Modifica los datos desde el panel de empleado.
	 */
	public Reserva actualizarReservaSBU(Integer idReserva, Reserva datosNuevos, String empleado) {
		Reserva reservaExistente = buscarPorId(idReserva);
		reservaExistente.setFechaViaje(datosNuevos.getFechaViaje());
		reservaExistente.setEmailContacto(datosNuevos.getEmailContacto());
		reservaExistente.setTelefono(datosNuevos.getTelefono());
		reservaExistente.setObservaciones(datosNuevos.getObservaciones());
		reservaExistente.setEmpleadoGestor(empleado);
		return reservaRepository.save(reservaExistente);
	}

	// =========================================================================================
	// NUEVOS MÉTODOS PARA LA PASARELA DE PAGOS DE 3 PASOS
	// =========================================================================================

	/**
	 * Procesa la reserva proveniente de la pasarela web de 3 pasos. Vincula a los
	 * acompañantes, guarda la reserva con estado PAGADA y envía los correos usando
	 * estrictamente los métodos originales de EmailService.
	 */
	@Transactional
	public Reserva tramitarReservaWeb(String cuentaUsuario, com.chernobyl.explorer.dto.PeticionReservaDTO peticion) {

		Cliente titular = clienteService.listarTodos().stream()
				.filter(c -> c.getUsuario() != null && c.getUsuario().getCuenta().equals(cuentaUsuario)).findFirst()
				.orElse(null);

		if (titular == null) {
			throw new ValidacionNegocioException("Sesión inválida o usuario no encontrado.");
		}

		PaqueteViaje paquete = paqueteViajeService.buscarPorId(peticion.getIdPaquete());

		// 1. Instanciar la Reserva
		Reserva reserva = new Reserva();
		reserva.setFechaViaje(peticion.getFechaViaje());
		reserva.setTelefono(peticion.getTelefonoContacto());
		reserva.setEmailContacto(titular.getEmail());
		reserva.setTipoPaquete(paquete);
		reserva.setPrecioTotal(paquete.getPrecioPaquete());
		reserva.setConfirmacionMayorEdad(true);
		reserva.generarLocalizador();
		reserva.setEstado("PENDIENTE"); // Se queda pendiente para los empleados

		Reserva guardada = reservaRepository.save(reserva);

		// 2. Asociar al Titular y Acompañantes
		List<Cliente> viajeros = new java.util.ArrayList<>();
		titular.setReserva(guardada);
		viajeros.add(titular);

		if (peticion.getAcompanantes() != null && !peticion.getAcompanantes().isEmpty()) {
			for (com.chernobyl.explorer.dto.PeticionReservaDTO.AcompananteDTO dto : peticion.getAcompanantes()) {
				Cliente acomp = new Cliente();
				acomp.setNombre(dto.getNombre());
				acomp.setApellido1(dto.getApellidos());
				acomp.setDni(dto.getDni());
				acomp.setNacionalidad(dto.getNacionalidad());

				// === EL ARREGLO ESTÁ AQUÍ ===
				// Rellenamos datos obligatorios para que la Base de Datos no explote
				acomp.setFechaNacimiento(LocalDate.now().minusYears(25));
				acomp.setEmail(dto.getDni().toLowerCase() + "@pasajero.com");
				acomp.setTelefono(peticion.getTelefonoContacto());

				acomp.setActivo(true);
				acomp.setConsentimiento(true);
				acomp.setFechaAlta(LocalDate.now());
				acomp.setReserva(guardada);

				clienteService.crear(acomp);
				viajeros.add(acomp);
			}
		}
		guardada.setViajeros(viajeros);

		// 3. Mandar Correos
		try {
			emailService.enviarConfirmacionReservaCompleta(titular.getEmail(), titular, guardada);
		} catch (Exception e) {
			System.out.println("Aviso: Correos no enviados. Detalle: " + e.getMessage());
		}

		return guardada;
	}

	/**
	 * Obtiene el listado de reservas para el área personal del cliente web.
	 */
	public List<Reserva> obtenerMisReservas(String cuentaUsuario) {
		Cliente titular = clienteService.listarTodos().stream()
				.filter(c -> c.getUsuario() != null && c.getUsuario().getCuenta().equals(cuentaUsuario)).findFirst()
				.orElse(null);

		if (titular == null)
			return new ArrayList<>();

		try {
			return buscarReservasPorDni(titular.getDni());
		} catch (ElementoNoEncontradaException e) {
			return new ArrayList<>();
		}
	}
}