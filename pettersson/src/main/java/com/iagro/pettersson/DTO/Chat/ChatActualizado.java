package com.iagro.pettersson.DTO.Chat;

import com.iagro.pettersson.Entity.Mensaje;

import java.util.List;

public record ChatActualizado(
        List<Mensaje> mensajes
) {
}
