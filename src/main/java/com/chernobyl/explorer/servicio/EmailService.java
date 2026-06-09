package com.chernobyl.explorer.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.chernobyl.explorer.entidades.Reserva;
import com.chernobyl.explorer.entidades.Cliente;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emisorCorreos;

	public void enviarConfirmacionReservaCompleta(String emailCliente, Cliente titular, Reserva reserva) {

		// 1. CORREO PARA EL CLIENTE
		SimpleMailMessage mensajeCliente = new SimpleMailMessage();
		mensajeCliente.setTo(emailCliente);
		mensajeCliente.setSubject("✅ Reserva Confirmada y Pagada - CE Explorer");

		StringBuilder cuerpoC = new StringBuilder();
		cuerpoC.append("Estimado/a ").append(titular.getNombre()).append(",\n\n");
		cuerpoC.append(
				"Su pago en la pasarela ha sido procesado con éxito. Su expedición ha quedado registrada y está PENDIENTE de asignación y verificación por parte de nuestro equipo.\n\n");

		cuerpoC.append("--- DATOS DE LA RESERVA ---\n");
		cuerpoC.append("📍 Localizador: ").append(reserva.getLocalizadorCliente()).append("\n");
		cuerpoC.append("☢️ Paquete: ").append(reserva.getTipoPaquete().getNombre()).append("\n");
		cuerpoC.append("📅 Fecha: ").append(reserva.getFechaViaje()).append("\n\n");

		cuerpoC.append("--- VIAJEROS REGISTRADOS ---\n");
		for (int i = 0; i < reserva.getViajeros().size(); i++) {
			Cliente c = reserva.getViajeros().get(i);
			cuerpoC.append(i + 1).append(". ").append(c.getNombre()).append(" ").append(c.getApellido1()).append("\n");
		}
		cuerpoC.append("\nAtentamente,\nChernobyl Explorer.");
		mensajeCliente.setText(cuerpoC.toString());
		emisorCorreos.send(mensajeCliente);

		// 2. CORREO PARA LA EMPRESA (Aviso a Empleados)
		SimpleMailMessage mensajeEmpresa = new SimpleMailMessage();
		mensajeEmpresa.setTo("proyecto.chernobyl.explorer@gmail.com");
		mensajeEmpresa.setSubject("NUEVA RESERVA PENDIENTE - Loc: " + reserva.getLocalizadorCliente());

		StringBuilder cuerpoE = new StringBuilder();
		cuerpoE.append("AVISO AL EQUIPO DE OPERACIONES:\n\n");
		cuerpoE.append(
				"Se ha recibido un nuevo pago en la pasarela web. Hay una reserva PENDIENTE de revisar en el Panel de Administración.\n\n");
		cuerpoE.append("Titular: ").append(titular.getNombre()).append(" ").append(titular.getApellido1()).append("\n");
		cuerpoE.append("Paquete: ").append(reserva.getTipoPaquete().getNombre()).append("\n");
		cuerpoE.append("Fecha solicitada: ").append(reserva.getFechaViaje()).append("\n\n");
		cuerpoE.append("Por favor, inicie sesión en el sistema para Aprobar o Rechazar esta solicitud.");

		mensajeEmpresa.setText(cuerpoE.toString());
		emisorCorreos.send(mensajeEmpresa);
	}

	public void enviarResguardoReserva(String emailDestino, Cliente clientePrincipal, Reserva reserva) {
	}

	public void enviarAvisoAceptacion(String emailDestino, Reserva reserva) {
	}

	public void enviarAvisoCancelacionPorPersonal(String emailDestino, Reserva reserva, String motivo) {
	}

	public void enviarReciboPago(String emailDestino, Reserva reserva, String transaccion) {
	}
}