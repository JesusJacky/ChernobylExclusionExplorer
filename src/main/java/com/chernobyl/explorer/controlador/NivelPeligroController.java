//package com.chernobyl.explorer.controlador;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.chernobyl.explorer.entidades.NivelPeligro;
//import com.chernobyl.explorer.servicio.NivelPeligroService;
//
//@RestController
//@RequestMapping("/nivel-peligro")
//public class NivelPeligroController {
//	
//	@Autowired
//	private NivelPeligroService nivelPeligroService;
//
//	// Listar todos los niveles de peligro
//	@GetMapping
//	public List<NivelPeligro> listarTodos() {
//		return nivelPeligroService.listarNiveles();
//	}
//
//	// Obtener nivel de peligro por id
//	@GetMapping("/{id}")
//	public ResponseEntity<NivelPeligro> obtenerPorId(@PathVariable Integer id) {
//		try {
//			NivelPeligro nivel = nivelPeligroService.buscarPorId(id);
//			return ResponseEntity.ok(nivel);
//		} catch (RuntimeException e) {
//			return ResponseEntity.notFound().build();
//		}
//	}
//	
//	// Crear nuevo nivel
//	@PostMapping
//	public ResponseEntity<NivelPeligro> crear(@RequestBody NivelPeligro nivelPeligro){
//		NivelPeligro nuevoNivelPeligro = nivelPeligroService.crear(nivelPeligro);
//		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoNivelPeligro);
//	}
//	
//	@DeleteMapping("/{id}")
//	public ResponseEntity<Void> eliminar(@PathVariable Integer id){
//		try {
//			nivelPeligroService.eliminar(id);
//			return ResponseEntity.noContent().build();
//		} catch (RuntimeException e) {
//			return ResponseEntity.notFound().build();
//		}
//	}
//}