package com.iagro.pettersson.DTO.SuperUsuario;

import com.iagro.pettersson.Entity.Agrolink;

import java.util.List;

public record SuperListaAgrolink(
        List<Agrolink> listaAgrolinks
) {
}
