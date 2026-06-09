package com.chernobyl.explorer.servicio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.chernobyl.explorer.entidades.Usuario;
import com.chernobyl.explorer.repositorio.UsuarioRepository;

/**
 * Servicio de autenticación principal que conecta nuestra base de datos
 * con el ecosistema de seguridad de Spring Security.
 * Implementa la interfaz UserDetailsService requerida para el proceso de Login.
 */
@Service
public class AutenticacionService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca a un usuario por su nombre de cuenta durante el proceso de inicio de sesión
     * y empaqueta sus credenciales para que Spring Security las valide internamente.
     * * @param nombreUsuario El 'username' o cuenta introducida en el formulario de login.
     * @return Un objeto {@link UserDetails} con la cuenta, la contraseña encriptada y los roles.
     * @throws UsernameNotFoundException Si el usuario no existe en la base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        
        // 1. Traemos todos los usuarios usando el findAll heredado por defecto
        List<Usuario> todosLosUsuarios = usuarioRepository.findAll();
        
        // 2. Buscamos mediante un stream el que coincida con la cuenta introducida en el login
        Usuario usuarioEncontrado = todosLosUsuarios.stream()
                .filter(u -> u.getCuenta().equals(nombreUsuario))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + nombreUsuario + " no existe en el sistema."));

        // 3. Le empaquetamos los datos a Spring Security en el formato que él necesita
        return User.builder()
                .username(usuarioEncontrado.getCuenta())
                .password(usuarioEncontrado.getClave()) // Esta contraseña ya estará encriptada en la BD
                .roles(usuarioEncontrado.getRol().replace("ROLE_", "")) // Quitamos el prefijo si lo lleva, Spring lo gestiona por dentro
                .build();
    }
}