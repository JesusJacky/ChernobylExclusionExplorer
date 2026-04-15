package com.chernobyl.explorer.servicio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.entidades.Cliente;
import com.chernobyl.explorer.excepciones.ElementoNoEncontradaException;
import com.chernobyl.explorer.repositorio.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	/**
	 * Lista todos los clientes
	 * 
	 * @return lista de todos los clientes
	 */
	public List<Cliente> listarTodos() {
		return clienteRepository.findAll();
	}

	/**
	 * Busca a un cliente por id
	 * 
	 * @param id
	 * @return un cliente por id
	 */
	public Cliente buscarPorId(Integer id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("Cliente no encontrado con el ID " + id));
	}

	/**
	 * Crea un nuevo cliente
	 * 
	 * @param cliente
	 * @return
	 */
	public Cliente crear(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	/**
	 * Elimina un cliente
	 * 
	 * @param id
	 */
	public void eliminar(Integer id) {
		if (clienteRepository.existsById(id)) {
			clienteRepository.deleteById(id);
		} else {
			throw new ElementoNoEncontradaException("Cliente no encontrado con el ID " + id);
		}
	}
	
	public void eliminarCliente(Integer id) {
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("Cliente no encontrado con el ID " + id));
		
		cliente.setActivo(false);
		cliente.setFechaBaja(LocalDate.now());
		
		clienteRepository.save(cliente);
	}

	/**
	 * Busca a un cliente por nacionalidad y filtra si es mayor de edad
	 * 
	 * @param nacionalidad
	 * @param edad
	 * @return lista completa de los clientes filtrados por la nacionalidad y
	 *         mayor de edad
	 */
	public List<Cliente> buscarPorNacionalidadYMayorDeEdad(String nacionalidad) {
		List<Cliente> todosClientes = clienteRepository.findAll();

		LocalDate hoy = LocalDate.now();

		int mayoriaEdad = 18;

		List<Cliente> resultado = todosClientes.stream()
				.filter(c -> ChronoUnit.YEARS.between(c.getFechaNacimiento(), hoy) >= mayoriaEdad
						&& c.igualNacionalidad(nacionalidad))
				.toList();

		if (resultado.isEmpty()) {
			throw new ElementoNoEncontradaException(
					"No se ha encontrado un cliente con la nacionalidad " + nacionalidad + " y que sea mayor de edad");
		}

		return resultado;
	}

	/**
	 * Busca un cliente y lo filtra por edad
	 * @return lista completa de los clientes filtrador por menor de edad
	 */
	public List<Cliente> filtrarPorMenorDeEdad() {
		List<Cliente> todosClientes = clienteRepository.findAll();

		LocalDate hoy = LocalDate.now();

		int mayoriaEdad = 18;

		List<Cliente> resultado = todosClientes.stream()
				.filter(c -> c.getFechaNacimiento() != null && ChronoUnit.YEARS.between(c.getFechaNacimiento(), hoy) < mayoriaEdad)
				.filter(c -> c.getActivo() == true).toList();

		if (resultado.isEmpty()) {
			throw new ElementoNoEncontradaException("No se ha encontrado menor de edad");
		}
		return resultado;
	}

}