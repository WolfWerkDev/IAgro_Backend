package com.iagro.pettersson.DTO.Chat;

import com.iagro.pettersson.Entity.Chat;
import com.iagro.pettersson.Entity.Mensaje;
import com.iagro.pettersson.Service.MensajeService;

public record ChatCreado(
        Long idChat,
        String respuestaIA
) {
}
