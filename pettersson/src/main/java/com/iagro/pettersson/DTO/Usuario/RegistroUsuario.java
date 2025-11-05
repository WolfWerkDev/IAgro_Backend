package com.iagro.pettersson.DTO.Usuario;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record RegistroUsuario(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser pasada")
        LocalDate fechaNacimiento,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "\\+?\\d{7,15}", message = "Número de teléfono inválido")
        String telefono,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Correo inválido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String contraseña
) {}
