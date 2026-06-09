package com.chernobyl.explorer.configuracion;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.chernobyl.explorer.entidades.Cliente;
import com.chernobyl.explorer.entidades.Usuario;
import com.chernobyl.explorer.repositorio.UsuarioRepository;
import com.chernobyl.explorer.servicio.ClienteService;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

	// ====================================================================
	// BLOQUE 1: CADENA DE FILTROS Y RESTRICCIONES DE RUTAS
	// ====================================================================
	@Bean
	public SecurityFilterChain cadenaFiltrosSeguridad(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
				.authorizeHttpRequests(solicitudes -> solicitudes

						// --- ZONA PRIVADA (INTRANET Y GESTIÓN) ---

						// Toda la carpeta intranet bloqueada a clientes
						.requestMatchers("/intranet/**", "/gestion-clientes.html")
						.hasAnyRole("EMPLEADO", "SUPERVISOR", "ADMIN")

						// Permiso para dar de alta empleados (Admin y Supervisor)
						.requestMatchers(HttpMethod.POST, "/usuarios/registro-empleado")
						.hasAnyRole("ADMIN", "SUPERVISOR")

						// Permisos para manipular reservas y buscar clientes
						.requestMatchers("/clientes/buscar-menores", "/clientes/buscar-nacionalidad-mayores")
						.hasAnyRole("EMPLEADO", "SUPERVISOR", "ADMIN")
						.requestMatchers(HttpMethod.PUT, "/reservas/*/aprobar", "/reservas/*/rechazar")
						.hasAnyRole("EMPLEADO", "SUPERVISOR", "ADMIN")

						// --- ZONA PÚBLICA (ACCESO LIBRE) ---
						.requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
						.permitAll()
						.requestMatchers("/css/**", "/js/**", "/img/**", "/assets/**", "/error", "/componentes/**",
								"/monitor/**", "/legalidades/**")
						.permitAll().requestMatchers("/", "/**/*.html").permitAll()
						.requestMatchers("/clientes/**", "/paquetes/**", "/usuarios/**").permitAll()

						// --- POR DEFECTO ---
						.anyRequest().authenticated())

				// ====================================================================
				// BLOQUE 2: LÓGICA DE LOGIN Y REDIRECCIÓN INTELIGENTE
				// ====================================================================
				.formLogin(formulario -> formulario.loginPage("/login.html").loginProcessingUrl("/login")
						.successHandler((request, response, authentication) -> {
							String rol = authentication.getAuthorities().iterator().next().getAuthority();

							// Redirección dinámica según el rol
							if (rol.equals("ROLE_ADMIN")) {
								response.sendRedirect("/intranet/panel-admin.html");
							} else if (rol.equals("ROLE_SUPERVISOR") || rol.equals("ROLE_EMPLEADO")) {
								response.sendRedirect("/intranet/panel-empleado.html");
							} else {
								response.sendRedirect("/index.html"); // Los clientes van a la portada
							}
						}).failureUrl("/login.html?error=true").permitAll())

				// ====================================================================
				// BLOQUE 3: LÓGICA DE CERRAR SESIÓN
				// ====================================================================
				.logout(salida -> salida.logoutUrl("/logout").logoutSuccessUrl("/index.html").permitAll());

		return http.build();
	}

	// ====================================================================
	// BLOQUE 4: EXCEPCIONES GLOBALES DE SEGURIDAD (Consolas y APIs)
	// ====================================================================
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
				"/h2-console/**");
	}

	@Bean
	public PasswordEncoder codificadorContrasenas() {
		return new BCryptPasswordEncoder();
	}

	// ====================================================================
	// BLOQUE 5: GENERADOR AUTOMÁTICO Y ENCRIPTADOR (Transaccional)
	// ====================================================================
	@Bean
	public CommandLineRunner inicializarUsuariosBase(UsuarioRepository usuarioRepository, ClienteService clienteService,
			PasswordEncoder passwordEncoder, PlatformTransactionManager transactionManager) {

		return args -> {
			TransactionTemplate tt = new TransactionTemplate(transactionManager);
			tt.execute(status -> {

				// --- PARTE 1: CREACIÓN DE USUARIOS BASE (Tu código original) ---
				boolean existeAdmin = usuarioRepository.findAll().stream().anyMatch(u -> "admin".equals(u.getCuenta()));

				if (!existeAdmin) {
					// 1. ADMIN
					Usuario uAdmin = new Usuario();
					uAdmin.setCuenta("admin");
					uAdmin.setClave(passwordEncoder.encode("admin"));
					uAdmin.setRol("ROLE_ADMIN");
					uAdmin = usuarioRepository.save(uAdmin);

					Cliente cAdmin = new Cliente();
					cAdmin.setUsuario(uAdmin);
					cAdmin.setNombre("Director");
					cAdmin.setApellido1("Sistema");
					cAdmin.setDni("00000000A");
					cAdmin.setNacionalidad("Desconocida");
					cAdmin.setFechaNacimiento(LocalDate.of(1970, 1, 1));
					cAdmin.setEmail("proyecto.chernobyl.explorer@gmail.com");
					cAdmin.setTelefono("000000000");
					cAdmin.setActivo(true);
					cAdmin.setConsentimiento(true);
					clienteService.crear(cAdmin);

					// 2. SUPERVISOR
					Usuario uSup = new Usuario();
					uSup.setCuenta("supervisor");
					uSup.setClave(passwordEncoder.encode("supervisor"));
					uSup.setRol("ROLE_SUPERVISOR");
					uSup = usuarioRepository.save(uSup);

					Cliente cSup = new Cliente();
					cSup.setUsuario(uSup);
					cSup.setNombre("José");
					cSup.setApellido1("Álvarez");
					cSup.setDni("11111111S");
					cSup.setNacionalidad("Española");
					cSup.setFechaNacimiento(LocalDate.of(1980, 1, 1));
					cSup.setEmail("proyecto.chernobyl.explorer@gmail.com");
					cSup.setTelefono("600000001");
					cSup.setActivo(true);
					cSup.setConsentimiento(true);
					clienteService.crear(cSup);

					// 3. EMPLEADO
					Usuario uEmp = new Usuario();
					uEmp.setCuenta("empleado");
					uEmp.setClave(passwordEncoder.encode("empleado"));
					uEmp.setRol("ROLE_EMPLEADO");
					uEmp = usuarioRepository.save(uEmp);

					Cliente cEmp = new Cliente();
					cEmp.setUsuario(uEmp);
					cEmp.setNombre("Mario");
					cEmp.setApellido1("Pérez");
					cEmp.setDni("22222222E");
					cEmp.setNacionalidad("Española");
					cEmp.setFechaNacimiento(LocalDate.of(1995, 5, 5));
					cEmp.setEmail("proyecto.chernobyl.explorer@gmail.com");
					cEmp.setTelefono("600000002");
					cEmp.setActivo(true);
					cEmp.setConsentimiento(true);
					clienteService.crear(cEmp);

					// 4. CLIENTE
					Usuario uCli = new Usuario();
					uCli.setCuenta("cliente");
					uCli.setClave(passwordEncoder.encode("cliente"));
					uCli.setRol("ROLE_CLIENTE");
					uCli = usuarioRepository.save(uCli);

					Cliente cCli = new Cliente();
					cCli.setUsuario(uCli);
					cCli.setNombre("Jesús");
					cCli.setApellido1("Hidalgo");
					cCli.setApellido2("Rodríguez");
					cCli.setDni("33333333C");
					cCli.setNacionalidad("Española");
					cCli.setFechaNacimiento(LocalDate.of(1988, 1, 1));
					cCli.setEmail("jesusjacky88@gmail.com");
					cCli.setTelefono("600000003");
					cCli.setActivo(true);
					cCli.setConsentimiento(true);
					clienteService.crear(cCli);

					System.out.println(
							"✅ SEGURIDAD SBU: Administrador, Supervisor, Empleado y Cliente creados con éxito.");
				}

				// --- PARTE 2: INTERCEPTOR DEL SCRIPT SQL (Nueva Lógica) ---
				boolean seEncriptoAlgo = false;
				for (Usuario u : usuarioRepository.findAll()) {
					// Si la contraseña no es nula y NO empieza por el prefijo de BCrypt...
					if (u.getClave() != null && !u.getClave().startsWith("$2a$")) {
						// La encriptamos usando el codificador oficial y guardamos
						u.setClave(passwordEncoder.encode(u.getClave()));
						usuarioRepository.save(u);
						seEncriptoAlgo = true;
					}
				}

				if (seEncriptoAlgo) {
					System.out.println(
							"✅ SEGURIDAD SBU: Se han detectado y encriptado exitosamente las contraseñas en texto plano del data.sql.");
				}

				return null;
			});
		};
	}
}