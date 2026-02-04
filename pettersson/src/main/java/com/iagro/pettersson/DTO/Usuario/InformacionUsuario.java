package com.iagro.pettersson.DTO.Usuario;

import com.iagro.pettersson.DTO.Finca.InfoFinca;
import com.iagro.pettersson.DTO.Plan.InfoPlan;
import com.iagro.pettersson.Entity.Plan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InformacionUsuario(
        String fotoPerfilUrl,
        String nombre,
        LocalDate fechaNacimiento,
        String telefono,
        String correo,
        InfoPlan planUsuario,
        LocalDateTime fechaInicioPlan,
        LocalDateTime fechaFinPlan
) {
}
