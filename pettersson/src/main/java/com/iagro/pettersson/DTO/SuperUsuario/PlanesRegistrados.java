package com.iagro.pettersson.DTO.SuperUsuario;

import com.iagro.pettersson.Entity.Plan;

import java.util.List;

public record PlanesRegistrados(
        List<Plan> listaPlanes
) {
}
