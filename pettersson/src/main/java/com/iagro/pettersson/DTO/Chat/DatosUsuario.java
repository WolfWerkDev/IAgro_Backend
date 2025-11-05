package com.iagro.pettersson.DTO.Chat;

import com.iagro.pettersson.Enum.TipoDeCultivo;
import com.iagro.pettersson.Enum.TipoPlan;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Set;

public record DatosUsuario(
        @NotBlank(message = "El mensaje no puede estar vacío.")
        String mensaje,
        List<String> codigosAgrolink,
        String ubicacionFinca,
        Set<TipoDeCultivo> tiposDeCultivo,
        Long idChat,
        // info del plan
        Integer maxMensajesPorChat,
        TipoPlan tipoPlan
) {
}
