package com.iagro.pettersson.DTO.Finca;

import com.iagro.pettersson.Entity.Finca;

import java.util.List;

public record FincasPorUsuario(
        List<Finca> listaFincas
) {
}
