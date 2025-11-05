package com.iagro.pettersson.security;

import com.iagro.pettersson.Entity.SuperUsuario;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Repository.SuperUsuarioRepository;
import com.iagro.pettersson.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SuperUsuarioRepository superUsuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // ✅ Inyección del componente JwtUtil

    public LoginResponse login(LoginRequest dto) {
        // Primero revisamos si es superUsuario
        SuperUsuario superUser = superUsuarioRepository.findByUsername(dto.correo()); // aquí dto.correo es username
        if (superUser != null) {
            if (!passwordEncoder.matches(dto.contraseña(), superUser.getPassword())) {
                throw new IllegalArgumentException("Contraseña incorrecta");
            }
            // ✅ Usamos el bean inyectado
            String token = jwtUtil.generarToken(superUser.getIdSuperUsuario(), true);
            return new LoginResponse(token, true);
        }

        // Si no es superUsuario, buscamos en usuarios normales
        Usuario usuario = usuarioRepository.findByCorreo(dto.correo());
        if (usuario == null) {
            throw new IllegalArgumentException("Correo no registrado");
        }
        if (!passwordEncoder.matches(dto.contraseña(), usuario.getContraseña())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        String token = jwtUtil.generarToken(usuario.getIdUsuario(), false);
        return new LoginResponse(token, false);
    }
}
