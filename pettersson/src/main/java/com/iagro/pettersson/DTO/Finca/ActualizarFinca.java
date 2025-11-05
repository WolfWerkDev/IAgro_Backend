package com.iagro.pettersson.DTO.Finca;

import com.iagro.pettersson.Enum.TipoDeCultivo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

public record ActualizarFinca(
        Long id,
        Optional<String> nombre,
        Optional<Set<TipoDeCultivo>> tipoDeCultivo,
        MultipartFile fotoFinca
) {
}
