package com.iagro.pettersson.DTO.Reporte;

public record ReporteIn(
        float temperaturaSuelo,
        float humedadSuelo,
        // Opcionales
        float temperaturaAmbiente,
        float pHSuelo,
        float conductividadSuelo
) {
}
