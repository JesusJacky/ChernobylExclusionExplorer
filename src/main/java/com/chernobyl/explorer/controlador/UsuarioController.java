package com.chernobyl.explorer.controlador;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chernobyl.explorer.dto.RegistroClienteDTO;
import com.chernobyl.explorer.entidades.Cliente;
import com.chernobyl.explorer.entidades.Usuario;
import com.chernobyl.explorer.repositorio.UsuarioRepository;
import com.chernobyl.explorer.servicio.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Gestión de Usuarios (Login)", description = "Controladores para el registro y credenciales de acceso")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private PasswordEncoder codificadorContrasenas;

	// ====================================================================
	// BLOQUE 1: ALTA DE CLIENTES (Zona Pública)
	// ====================================================================
	@Operation(summary = "Registrar nuevo cliente completo", description = "Crea las credenciales de acceso y la ficha personal del cliente de forma simultánea. Valida edad y DNI.")
	@PostMapping("/registro-cliente")
	public ResponseEntity<String> registrarCliente(@Valid @RequestBody RegistroClienteDTO dto) {

		// VALIDACIÓN EXACTA DE 18 AÑOS
		java.time.LocalDate hoy = java.time.LocalDate.now();
		if (dto.getFechaNacimiento() != null
				&& java.time.temporal.ChronoUnit.YEARS.between(dto.getFechaNacimiento(), hoy) < 18) {
			return ResponseEntity.badRequest().body("Error: Debe ser mayor de 18 años para registrarse en el sistema.");
		}

		if (!dto.getConsentimiento()) {
			return ResponseEntity.badRequest()
					.body("Error: Debe aceptar los términos de la expedición y certificar que es mayor de edad.");
		}

		try {
			Usuario nuevoUsuario = new Usuario();
			nuevoUsuario.setCuenta(dto.getCuenta());
			nuevoUsuario.setClave(codificadorContrasenas.encode(dto.getClave()));
			nuevoUsuario.setRol("ROLE_CLIENTE");

			Cliente nuevoCliente = new Cliente();
			nuevoCliente.setNombre(dto.getNombre());
			nuevoCliente.setApellido1(dto.getApellido1());
			nuevoCliente.setApellido2(dto.getApellido2());
			nuevoCliente.setDni(dto.getDni());
			nuevoCliente.setFechaNacimiento(dto.getFechaNacimiento());
			nuevoCliente.setNacionalidad(dto.getNacionalidad());
			nuevoCliente.setEmail(dto.getEmail());
			nuevoCliente.setTelefono(dto.getTelefono());
			nuevoCliente.setConsentimiento(dto.getConsentimiento());
			nuevoCliente.setUsuario(nuevoUsuario);

			clienteService.crear(nuevoCliente);

			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Cliente registrado con éxito y autorizado para solicitar expediciones.");

		} catch (Exception e) {
			// Misma lógica: la BD rechaza el duplicado automáticamente
			return ResponseEntity.badRequest()
					.body("Error: El nombre de usuario '" + dto.getCuenta() + "' o el DNI ya están en uso.");
		}
	}

	// ====================================================================
	// BLOQUE 2: ALTA DE EMPLEADOS (Intranet SBU)
	// ====================================================================
	@Operation(summary = "Registrar personal interno", description = "Exclusivo para administradores y supervisores. Aplica reglas de negocio sobre roles.")
	@PostMapping("/registro-empleado")
	public ResponseEntity<?> registrarEmpleado(@RequestBody Map<String, String> datos, Authentication auth) {

		// 1. VALIDACIÓN DE SEGURIDAD (El "Guardián")
		if (auth == null || !auth.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Debes iniciar sesión.");
		}

		String rolLogueado = auth.getAuthorities().iterator().next().getAuthority();
		String rolDeseado = datos.get("rol");
		boolean tienePermiso = false;

		// Lógica: ADMIN crea SUPERVISOR o EMPLEADO. SUPERVISOR crea EMPLEADO.
		if ("ROLE_ADMIN".equals(rolLogueado)) {
			if ("ROLE_SUPERVISOR".equals(rolDeseado) || "ROLE_EMPLEADO".equals(rolDeseado)) {
				tienePermiso = true;
			}
		} else if ("ROLE_SUPERVISOR".equals(rolLogueado)) {
			if ("ROLE_EMPLEADO".equals(rolDeseado)) {
				tienePermiso = true;
			}
		}

		if (!tienePermiso) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Acceso denegado: No tienes permisos para asignar el rol " + rolDeseado);
		}

		// 2. GUARDADO EN BASE DATOS (Tu código original)
		try {
			Usuario nuevoUsuario = new Usuario();
			nuevoUsuario.setCuenta(datos.get("cuenta"));
			nuevoUsuario.setClave(codificadorContrasenas.encode(datos.get("clave")));
			nuevoUsuario.setRol(rolDeseado);
			usuarioRepository.save(nuevoUsuario);

			Cliente fichaOperario = new Cliente();
			fichaOperario.setUsuario(nuevoUsuario);
			fichaOperario.setNombre(datos.get("nombre"));
			fichaOperario.setApellido1(datos.get("apellido1"));
			fichaOperario.setApellido2(datos.get("apellido2"));
			fichaOperario.setDni(datos.get("dni"));
			fichaOperario.setNacionalidad(datos.get("nacionalidad"));
			fichaOperario.setFechaNacimiento(java.time.LocalDate.parse(datos.get("fechaNacimiento")));
			fichaOperario.setEmail(datos.get("email"));
			fichaOperario.setTelefono(datos.get("telefono"));
			fichaOperario.setConsentimiento(true);
			fichaOperario.setActivo(true);

			clienteService.crear(fichaOperario);

			return ResponseEntity.ok("Credenciales operativas registradas en el sistema SBU.");

		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body("Error al registrar: La cuenta o el DNI ya existen en la base de datos.");
		}
	}

	// ====================================================================
	// BLOQUE 3: LECTURA DE SESIÓN (Validación Frontend)
	// ====================================================================
	@Operation(summary = "Verificar sesión actual", description = "Actualiza la fecha de conexión y devuelve los datos del usuario.")
	@GetMapping("/sesion")
	public ResponseEntity<?> verificarSesion(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getName().equals("anonymousUser")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay sesión activa");
		}

		// Actualizamos la fecha de conexión cada vez que se valida la sesión
		Usuario usuario = usuarioRepository.findAll().stream()
				.filter(u -> u.getCuenta().equals(authentication.getName())).findFirst().orElse(null);

		if (usuario != null) {
			usuario.setUltimaConexion(java.time.LocalDateTime.now());
			usuarioRepository.save(usuario);
		}

		String rol = authentication.getAuthorities().iterator().next().getAuthority();
		return ResponseEntity.ok(Map.of("usuario", authentication.getName(), "rol", rol));
	}
}