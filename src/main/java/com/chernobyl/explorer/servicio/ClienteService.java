package com.chernobyl.explorer.servicio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.entidades.Cliente;
import com.chernobyl.explorer.excepciones.ElementoNoEncontradaException;
import com.chernobyl.explorer.excepciones.ValidacionNegocioException;
import com.chernobyl.explorer.repositorio.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public List<Cliente> listarTodos() {
		return clienteRepository.findAll();
	}

	public Cliente buscarPorId(Integer id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("Cliente no encontrado con el ID " + id));
	}

	public Cliente crear(Cliente cliente) {
		LocalDate hoy = LocalDate.now();

		if (cliente.getFechaNacimiento() != null) {
			long edad = ChronoUnit.YEARS.between(cliente.getFechaNacimiento(), hoy);

			if (edad < 18 && !Boolean.TRUE.equals(cliente.isConsentimiento())) {
				throw new ValidacionNegocioException(
						"Los menores de edad requieren consentimiento explícito por sus tutores legales.");
			}
		}

		return clienteRepository.save(cliente);
	}

	public Cliente actualizar(Integer id, Cliente clienteModificado) {
		Cliente clienteExistente = buscarPorId(id);

		clienteExistente.setNombre(clienteModificado.getNombre());
		clienteExistente.setApellido1(clienteModificado.getApellido1());
		clienteExistente.setApellido2(clienteModificado.getApellido2());
		clienteExistente.setDni(clienteModificado.getDni());
		clienteExistente.setFechaNacimiento(clienteModificado.getFechaNacimiento());
		clienteExistente.setNacionalidad(clienteModificado.getNacionalidad());
		clienteExistente.setEmail(clienteModificado.getEmail());
		clienteExistente.setTelefono(clienteModificado.getTelefono());

		return clienteRepository.save(clienteExistente);
	}

	public void eliminar(Integer id) {
		Cliente clienteExistente = buscarPorId(id);
		clienteRepository.delete(clienteExistente);
	}

	public Cliente darDeBaja(Integer id) {
		Cliente clienteExistente = buscarPorId(id);
		clienteExistente.setActivo(false);
		clienteExistente.setFechaBaja(LocalDate.now()); // Registramos el momento de la baja
		return clienteRepository.save(clienteExistente);
	}

	public List<Cliente> buscarPorNacionalidad(String nacionalidad) {
		List<Cliente> todosClientes = clienteRepository.findAll();

		List<Cliente> resultado = todosClientes.stream()
				.filter(c -> c.getNacionalidad() != null && c.getNacionalidad().equalsIgnoreCase(nacionalidad))
				.toList();

		if (resultado.isEmpty()) {
			throw new ElementoNoEncontradaException(
					"No se ha encontrado un cliente con la nacionalidad " + nacionalidad);
		}
		return resultado;
	}

	public List<Cliente> filtrarPorMenorDeEdad() {
		List<Cliente> todosClientes = clienteRepository.findAll();
		LocalDate hoy = LocalDate.now();
		int mayoriaEdad = 18;

		List<Cliente> resultado = todosClientes.stream()
				.filter(c -> c.getFechaNacimiento() != null
						&& ChronoUnit.YEARS.between(c.getFechaNacimiento(), hoy) < mayoriaEdad)
				.filter(c -> Boolean.TRUE.equals(c.getActivo())).toList();

		if (resultado.isEmpty()) {
			throw new ElementoNoEncontradaException("No se ha encontrado ningún menor de edad activo.");
		}
		return resultado;
	}

	public Cliente buscarPorDni(String dni) {
		List<Cliente> todosClientes = clienteRepository.findAll();

		return todosClientes.stream().filter(c -> c.getDni().equalsIgnoreCase(dni)).findFirst()
				.orElseThrow(() -> new ElementoNoEncontradaException("No se ha encontrado al cliente con DNI " + dni));
	}

	// ====================================================================
	// NUEVO MÉTODO: Búsqueda por Teléfono
	// ====================================================================
	public Cliente buscarPorTelefono(String telefono) {
		List<Cliente> todosClientes = clienteRepository.findAll();

		return todosClientes.stream().filter(c -> c.getTelefono() != null && c.getTelefono().equalsIgnoreCase(telefono))
				.findFirst().orElseThrow(() -> new ElementoNoEncontradaException(
						"No se ha encontrado registro con el teléfono " + telefono));
	}

	// ====================================================================
	// NUEVO MÉTODO: Reactivación Lógica
	// ====================================================================
	public Cliente reactivar(Integer id) {
		Cliente clienteExistente = buscarPorId(id);
		clienteExistente.setActivo(true);
		clienteExistente.setFechaBaja(null); // Limpiamos la fecha de baja al volver a estar activo
		return clienteRepository.save(clienteExistente);
	}
}