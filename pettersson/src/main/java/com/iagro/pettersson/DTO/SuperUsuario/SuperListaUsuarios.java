package com.iagro.pettersson.DTO.SuperUsuario;

import com.iagro.pettersson.Entity.Usuario;

import java.util.List;

public record SuperListaUsuarios(
        List<Usuario> listaUsuarios
) {
}
