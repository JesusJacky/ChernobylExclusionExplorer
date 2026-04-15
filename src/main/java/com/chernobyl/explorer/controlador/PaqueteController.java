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
import org.springframework.web.bind.annotation.RestController;

import com.chernobyl.explorer.entidades.PaqueteViaje;
import com.chernobyl.explorer.servicio.PaqueteViajeService;

@RestController
@RequestMapping("/paquetes")
public class PaqueteController {

	@Autowired
	private PaqueteViajeService paqueteViajeService;

	// http://localhost/paquetes
	// TODO: Cambiar para usar ResponseEntity -> si la lista está vacía
	// enviar un NO_CONTENT
	@GetMapping
	public List<PaqueteViaje> listarPaquetes() {
		return paqueteViajeService.listarTodos();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PaqueteViaje> obtenerPorId(@PathVariable Integer id) {
		try {
			PaqueteViaje resultado = paqueteViajeService.buscarPorId(id);
			return ResponseEntity.ok(resultado);
		} catch (Exception e) {
			System.err.println(e);
			return ResponseEntity.notFound().build();
		}
	}

	// GET de los paquetes que tengan un nivel de peligrosidad concreto,
	// filtrado por el nombre (BAJO, MEDIO...)
	// http://localhost/paquetes/BAJO
	@GetMapping("/nivel/{nivel}")
	public ResponseEntity<List<PaqueteViaje>> obtenerPorNivel(@PathVariable String nivel){
		try {
			List<PaqueteViaje> paquetes = paqueteViajeService.listarPorNivel(nivel);
			return ResponseEntity.ok(paquetes);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	} 
	

	// POST para añadir nuevo paquete
	@PostMapping
	public ResponseEntity<PaqueteViaje> crear (@RequestBody PaqueteViaje paqueteViaje){
		PaqueteViaje nuevoPaquete = paqueteViajeService.crear(paqueteViaje);
		//System.out.println("BIEN");
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaquete);
		}
	/*@PostMapping("{crear}")
	public ResponseEntity<PaqueteViaje> crearConNivel (@RequestBody PaqueteViaje paqueteViaje){
		PaqueteViaje nuevoPaquete = paqueteViajeService.crear(paqueteViaje);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaquete);
		}*/

	// PUT para actualizar
	@PutMapping("/actualizar/{id}")
	public ResponseEntity<PaqueteViaje> actualizar(@PathVariable Integer id, @RequestBody PaqueteViaje viaje) {
		try {
			PaqueteViaje paqueteActualizado = paqueteViajeService.actualizar(id, viaje);
			return ResponseEntity.ok(paqueteActualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// DELETE para borrrar un paquete concreto
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
		try {
			paqueteViajeService.eliminar(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
