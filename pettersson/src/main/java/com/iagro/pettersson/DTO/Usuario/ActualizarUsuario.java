package com.iagro.pettersson.DTO.Usuario;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record ActualizarUsuario(
        MultipartFile fotoPerfil,
        Optional<String> telefono,
        Optional<String> contraseñaAnterior,
        String nuevaContraseña
) {
}
