package com.iagro.pettersson.DTO.Mensaje;

import com.iagro.pettersson.Enum.EmisorMensaje;

public record ListaMensajes(
        EmisorMensaje emisorMensaje,
        String contenido
) {
}
