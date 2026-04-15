package com.chernobyl.explorer.servicio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.entidades.NivelPeligro;
import com.chernobyl.explorer.entidades.PaqueteViaje;
import com.chernobyl.explorer.repositorio.PaqueteViajeRepository;

import jakarta.transaction.Transactional;

@Service
public class PaqueteViajeService {

    //private final PaqueteController paqueteController;

	@Autowired
	private PaqueteViajeRepository paqueteViajeRepository;

    PaqueteViajeService() {
        //this.paqueteController = paqueteController;
    }

	// Listar todos los paquetes de viaje
	public List<PaqueteViaje> listarTodos() {
		return paqueteViajeRepository.findAll();
	}

	// Buscar paquete de viaje por id
	public PaqueteViaje buscarPorId(Integer id) {
		return paqueteViajeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Paquete de viaje no encontrado con el ID " + id));
	}

	// Crear paquete de viaje
	public PaqueteViaje crear(PaqueteViaje paqueteViaje) {
		if (paqueteViaje.getPrecioPaquete() <= 0) {
			throw new RuntimeException("El viaje no puede ser a coste 0 o tener valor negativo");
		}
		

		
		return paqueteViajeRepository.save(paqueteViaje);
	}

	// Actualizar paquete de viaje
	@Transactional
	public PaqueteViaje actualizar(Integer id, PaqueteViaje paqueteDetalles) {
		if (!paqueteViajeRepository.existsById(id)) {
			throw new RuntimeException("No se puede actualizar el paquete de viaje con ID " + id + " porque no existe");
		}
		return paqueteViajeRepository.save(paqueteDetalles);
	}

	// Eliminar un paquete de viaje
	public void eliminar(Integer id) {
		if (paqueteViajeRepository.existsById(id)) {
			paqueteViajeRepository.deleteById(id);
		} else {
			throw new RuntimeException("Paquete de viaje no encontrado con el ID " + id);
		}
	}

	@Autowired
	private NivelPeligroService nivelPeligroService;

	// Crear paquete de viaje con nivel de peligrosidad
	public PaqueteViaje crearConNivel(PaqueteViaje paquete, Integer id) {
		if(!nivelPeligroService.existsById(id)){
			throw new RuntimeException("El nivel con el ID " + id + " no existe");
		}
		NivelPeligro nivel = nivelPeligroService.buscarPorId(id);
		paquete.setNivelpeligro(nivel);
		return paqueteViajeRepository.save(paquete);
	}
	
	// método que dado un nivel me devuelva una lista con todos los
	// paquetes con dicho nivel
	public List<PaqueteViaje> listarPorNivel(String nivel){
		List<PaqueteViaje> todosLosPquetes = paqueteViajeRepository.findAll();
		List<PaqueteViaje> paquetesFiltrados = new ArrayList<>();

		for(PaqueteViaje p : todosLosPquetes){
			if(p.getNivelpeligro() != null && p.getNivelpeligro().getNivel().equals(nivel)){
				paquetesFiltrados.add(p);
			}
		}

		if(paquetesFiltrados.isEmpty()){
			throw new RuntimeException("No existen paquetes del nivel: " + nivel);
		}
		return paquetesFiltrados;
	}
}
