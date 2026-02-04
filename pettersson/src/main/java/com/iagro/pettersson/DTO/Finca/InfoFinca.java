package com.iagro.pettersson.DTO.Finca;

import com.iagro.pettersson.Enum.TipoDeCultivo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record InfoFinca(
        Long idFinca,
        String nombreFinca,
        String ubicacion,
        Set<TipoDeCultivo> tiposDeCultivo,
        LocalDateTime fechaRegistro,
        String fotoFinca
) {
}
