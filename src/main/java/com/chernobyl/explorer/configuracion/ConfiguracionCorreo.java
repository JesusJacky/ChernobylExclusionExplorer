package com.chernobyl.explorer.configuracion;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configuración del servicio de mensajería electrónica (SMTP).
 * Define las credenciales y propiedades de conexión para utilizar el servidor de correos de Gmail.
 */
@Configuration
public class ConfiguracionCorreo {

	/**
	 * Crea y configura el bean JavaMailSender que se inyectará en EmailService.
	 * * @return Instancia configurada de JavaMailSender lista para enviar correos.
	 */
    @Bean
    public JavaMailSender emisorCorreos() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // 1. Datos del servidor SMTP de Gmail
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        
        // 2. Credenciales de la cuenta corporativa del proyecto
        mailSender.setUsername("proyecto.chernobyl.explorer@gmail.com");
        // Contraseña de aplicación generada en la seguridad de la cuenta de Google
        mailSender.setPassword("joesusrdibhoqnvz"); 
        
        // 3. Opciones de seguridad obligatorias requeridas por Google (TLS)
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        // Modo de depuración activado para monitorizar el flujo de envío en la consola
        props.put("mail.debug", "true"); 
        
        return mailSender;
    }
}