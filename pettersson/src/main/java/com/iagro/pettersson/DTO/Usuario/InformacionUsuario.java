package com.iagro.pettersson.DTO.Usuario;

import com.iagro.pettersson.Entity.Plan;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InformacionUsuario(
        String fotoPerfilUrl,
        String nombre,
        LocalDate fechaNacimiento,
        String telefono,
        String correo,
        Plan planUsuario,
        LocalDateTime fechaInicioPlan,
        LocalDateTime fechaFinPlan
) {
}
