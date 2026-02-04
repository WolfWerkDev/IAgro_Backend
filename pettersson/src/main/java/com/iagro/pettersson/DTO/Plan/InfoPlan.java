package com.iagro.pettersson.DTO.Plan;

import com.iagro.pettersson.Enum.TipoPlan;

public record InfoPlan(
        Long idPlan,
        TipoPlan tipoPlan,
        String descripcion,
        Integer maxFincas,
        Integer maxAgrolinksPorFinca,
        Integer maxMensajesPorChat,
        String variablesDisponibles
) {
}
