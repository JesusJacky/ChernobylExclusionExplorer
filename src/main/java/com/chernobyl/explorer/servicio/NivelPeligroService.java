package com.chernobyl.explorer.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.entidades.NivelPeligro;
import com.chernobyl.explorer.repositorio.NivelPeligroRepository;

@Service
public class NivelPeligroService {

	@Autowired
	private NivelPeligroRepository nivelPeligroRepository;

	// Listar todos los Niveles
	public List<NivelPeligro> listarNiveles() {
		return nivelPeligroRepository.findAll();
	}

	// Buscar nivel por id
	public NivelPeligro buscarPorId(Integer id) {
		return nivelPeligroRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Nivel de peligro no encontrado con el ID " + id));
	}

	// Buscar nivel por nombre
	/*public Optional<NivelPeligro> findByName(String nombre) {
		return nivelPeligroRepository.findByName(nombre);

	}*/

	// Crear nivel
	public NivelPeligro crear(NivelPeligro nivelPeligro) {
		return nivelPeligroRepository.save(nivelPeligro);
	}

	// Eliminar un nivel
	public void eliminar(Integer id) {
		if (nivelPeligroRepository.existsById(id)) {
			nivelPeligroRepository.deleteById(id);
		} else {
			throw new RuntimeException("Nivel de peligro no encontrado con el ID " + id);
		}
	}

	public boolean existsById(Integer id) {
		// TODO Auto-generated method stub
		return nivelPeligroRepository.existsById(id);
	}

}
