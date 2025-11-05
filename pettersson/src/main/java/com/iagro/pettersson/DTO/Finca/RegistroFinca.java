package com.iagro.pettersson.DTO.Finca;

import com.iagro.pettersson.Enum.TipoDeCultivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegistroFinca(
        @NotBlank(message = "El nombre de la finca no puede estar vacío")
        @Size(max = 100, message = "El nombre de la finca no puede superar 100 caracteres")
        String nombreFinca,

        @NotBlank(message = "La ubicación no puede estar vacía")
        @Size(max = 200, message = "La ubicación no puede superar 200 caracteres")
        String ubicacion,

        @NotNull(message = "Debe seleccionar al menos un tipo de cultivo")
        Set<TipoDeCultivo> tiposDeCultivo
) {
}
