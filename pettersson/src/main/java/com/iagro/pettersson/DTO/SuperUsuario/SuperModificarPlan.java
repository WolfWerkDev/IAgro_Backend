package com.iagro.pettersson.DTO.SuperUsuario;

import java.util.Optional;

public record SuperModificarPlan(
        Long idPlan,
        Optional<String> descripcion,
        Optional<Float> precioMensual,
        Optional<Integer> maxFincas,
        Optional<Integer> maxAgrolinksPorFinca,
        Optional<Integer> maxMensajesPorChat,
        Optional<String> variablesDisponibles,
        Optional<Boolean> esActivo
) {
}
