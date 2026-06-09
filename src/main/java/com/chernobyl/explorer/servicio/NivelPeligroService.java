package com.chernobyl.explorer.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.entidades.NivelPeligro;
import com.chernobyl.explorer.excepciones.ElementoNoEncontradaException;
import com.chernobyl.explorer.repositorio.NivelPeligroRepository;

/**
 * Servicio encargado de gestionar los niveles de peligrosidad radiológica.
 * Aunque su controlador esté desactivado, este servicio provee la lógica interna
 * necesaria para vincular las nuevas expediciones con su nivel correspondiente.
 */
@Service
public class NivelPeligroService {

	@Autowired
	private NivelPeligroRepository nivelPeligroRepository;

	/**
	 * Recupera el catálogo completo de niveles de peligro (BAJO, MEDIO, ALTO).
	 * * @return Lista con los niveles disponibles en la base de datos.
	 */
	public List<NivelPeligro> listarNiveles() {
		return nivelPeligroRepository.findAll();
	}

	/**
	 * Busca un nivel de peligrosidad específico por su ID.
	 * Utilizado internamente al crear nuevos paquetes de viaje para asignarles su nivel.
	 * * @param id El identificador del nivel (ej. 1 para BAJO).
	 * @return El objeto {@link NivelPeligro} encontrado.
	 * @throws ElementoNoEncontradaException Si el ID no existe en el catálogo.
	 */
	public NivelPeligro buscarPorId(Integer id) {
		return nivelPeligroRepository.findById(id)
				.orElseThrow(() -> new ElementoNoEncontradaException("Nivel de peligro no encontrado con el ID " + id));
	}

	/**
	 * Registra un nuevo nivel de peligrosidad. 
	 * Reservado para uso interno o futura escalabilidad desde un panel de administración.
	 * * @param nivelPeligro El objeto a persistir.
	 * @return El nivel guardado.
	 */
	public NivelPeligro crear(NivelPeligro nivelPeligro) {
		return nivelPeligroRepository.save(nivelPeligro);
	}

	/**
	 * Elimina un nivel de peligrosidad de la base de datos.
	 * * @param id El identificador del nivel a borrar.
	 * @throws ElementoNoEncontradaException Si se intenta borrar un nivel que no existe.
	 */
	public void eliminar(Integer id) {
		if (nivelPeligroRepository.existsById(id)) {
			nivelPeligroRepository.deleteById(id);
		} else {
			throw new ElementoNoEncontradaException("Nivel de peligro no encontrado con el ID " + id);
		}
	}
}