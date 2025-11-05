package com.iagro.pettersson.DTO.SuperUsuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SuperRegistroAgrolink(
        @NotBlank(message = "El código es obligatorio")
        @Size(min = 15, max = 15, message = "El código debe tener 15 caracteres.")
        @Pattern(regexp = "[A-Z]{3}-\\d{4}-[A-Z]-\\d{4}",
                message = "Código debe tener formato AGL-2025-B-0001")
        String codigo
) {
}
