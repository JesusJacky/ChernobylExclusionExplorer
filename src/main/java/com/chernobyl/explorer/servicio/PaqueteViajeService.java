package com.chernobyl.explorer.servicio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.entidades.NivelPeligro;
import com.chernobyl.explorer.entidades.PaqueteViaje;
import com.chernobyl.explorer.excepciones.ElementoNoEncontradaException;
import com.chernobyl.explorer.excepciones.ValidacionNegocioException;
import com.chernobyl.explorer.repositorio.PaqueteViajeRepository;

/**
 * Servicio encargado del catálogo de rutas y expediciones ofertadas por la empresa.
 */
@Service
public class PaqueteViajeService {

	@Autowired
	private PaqueteViajeRepository paqueteViajeRepository;

	@Autowired
	private NivelPeligroService nivelPeligroService;

	/**
	 * Recupera el catálogo completo de rutas disponibles.
	 * * @return Lista de todos los paquetes de viaje.
	 */
	public List<PaqueteViaje> listarTodos() {
		return paqueteViajeRepository.findAll();
	}

	/**
	 * Obtiene los detalles específicos de una ruta.
	 * * @param id ID del paquete.
	 * @return El paquete correspondiente.
	 */
	public PaqueteViaje buscarPorId(Integer id) {
		return paqueteViajeRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("Paquete de viaje no encontrado con el ID " + id));
	}

	/**
	 * Añade una nueva ruta comercial al catálogo de la empresa.
	 * * @param paqueteViaje Datos de la nueva ruta.
	 * @return El paquete persistido.
	 * @throws ValidacionNegocioException Si se introduce un precio igual o menor a 0.
	 */
	public PaqueteViaje crear(PaqueteViaje paqueteViaje) {
		if (paqueteViaje.getPrecioPaquete() <= 0) {
			throw new ValidacionNegocioException("El paquete de viaje debe tener un precio mayor que 0.");
		}
		return paqueteViajeRepository.save(paqueteViaje);
	}

	/**
	 * Actualiza la información de un paquete (precios, descripciones o puntos de visita).
	 * * @param id ID del paquete a modificar.
	 * @param paqueteDetalles Los datos sobrescritos.
	 * @return El paquete modificado.
	 */
	public PaqueteViaje actualizar(Integer id, PaqueteViaje paqueteDetalles) {
		if (!paqueteViajeRepository.existsById(id)) {
			throw new ElementoNoEncontradaException("No se puede actualizar el paquete de viaje con ID " + id + " porque no existe");
		}
		return paqueteViajeRepository.save(paqueteDetalles);
	}

	/**
	 * Retira un paquete del catálogo comercial.
	 * * @param id ID de la ruta a eliminar.
	 */
	public void eliminar(Integer id) {
		if (paqueteViajeRepository.existsById(id)) {
			paqueteViajeRepository.deleteById(id);
		} else {
			throw new ElementoNoEncontradaException("Paquete de viaje no encontrado con el ID " + id);
		}
	}

	/**
	 * Permite crear un paquete vinculándolo automáticamente a un Nivel de Peligro base de datos.
	 * * @param paquete Los datos base del paquete.
	 * @param id ID del {@link NivelPeligro} (Ej. 1=Bajo, 2=Medio, 3=Alto).
	 * @return El paquete creado y vinculado.
	 */
	public PaqueteViaje crearConNivel(PaqueteViaje paquete, Integer id) {
		try {
			NivelPeligro nivel = nivelPeligroService.buscarPorId(id);
			paquete.setNivelpeligro(nivel);
			return paqueteViajeRepository.save(paquete);
		} catch (ElementoNoEncontradaException e) {
			throw new ElementoNoEncontradaException("El nivel con el ID " + id + " no existe");
		}
	}
	
	/**
	 * Filtra los paquetes disponibles según su nivel de peligrosidad.
	 * * @param nivel Cadena de texto (ej. "BAJO", "MEDIO", "ALTO").
	 * @return Lista de paquetes que coinciden con dicho nivel.
	 */
	public List<PaqueteViaje> listarPorNivel(String nivel){
		List<PaqueteViaje> todosLosPquetes = paqueteViajeRepository.findAll();
		List<PaqueteViaje> paquetesFiltrados = new ArrayList<>();
		for (PaqueteViaje paqueteViaje : todosLosPquetes) {
			if (paqueteViaje.getNivelpeligro().getNivel().equalsIgnoreCase(nivel)) {
				paquetesFiltrados.add(paqueteViaje);
			}
		}
		if (paquetesFiltrados.isEmpty()) {
			throw new ElementoNoEncontradaException("No existen paquetes de viaje con el nivel de peligrosidad " + nivel);
		}
		return paquetesFiltrados;
	}
}