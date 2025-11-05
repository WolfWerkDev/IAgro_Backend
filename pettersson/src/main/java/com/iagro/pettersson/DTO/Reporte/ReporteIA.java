package com.iagro.pettersson.DTO.Reporte;

public record ReporteIA(
        String codigoAgrolink,
        Float temperaturaSuelo,
        Float humedadSuelo,
        Float temperaturaAmbiente,
        Float pHSuelo,
        Float conductividadSuelo
) {
}
