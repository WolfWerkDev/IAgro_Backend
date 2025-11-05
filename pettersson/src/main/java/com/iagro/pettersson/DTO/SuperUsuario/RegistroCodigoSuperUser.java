package com.iagro.pettersson.DTO.SuperUsuario;

import jakarta.validation.constraints.NotBlank;

public record RegistroCodigoSuperUser(
        @NotBlank(message = "El código es obligatorio")
        String codigo
) {
}
