package com.iagro.pettersson.DTO.SuperUsuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroSuperUsuario(
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String contraseña,

        @NotBlank(message = "El código es obligatorio")
        @Size(min = 4, max = 20, message = "El código debe tener entre 4 y 20 caracteres")
        String codigo,

        String caso
) {
}
