package com.iagro.pettersson.DTO.SuperUsuario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Optional;

public record SuperActualizarUsuario(
        @NotNull(message = "El ID del usuario es obligatorio.")
        @Positive(message = "El ID del usuario debe ser un número positivo.")
        Long idUsuario,

        Optional<Boolean> esActivo,

        Optional<Long> idPlan,

        Optional<LocalDateTime> fechaInicioPlan
) {
}
