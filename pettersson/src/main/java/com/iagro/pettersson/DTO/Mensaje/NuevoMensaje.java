package com.iagro.pettersson.DTO.Mensaje;

import com.iagro.pettersson.Entity.Chat;
import com.iagro.pettersson.Enum.EmisorMensaje;

public record NuevoMensaje(
        Chat chat,
        String contenido,
        EmisorMensaje emisorMensaje
) {
}
