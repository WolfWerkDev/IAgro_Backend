package com.iagro.pettersson.DTO.IAModel;

import com.iagro.pettersson.DTO.Mensaje.ListaMensajes;
import com.iagro.pettersson.DTO.Reporte.ReporteIA;
import com.iagro.pettersson.Enum.TipoDeCultivo;

import java.util.List;
import java.util.Set;

public record ConsultaModelo(
        String programmingPrompt,
        String nombreChat,
        String mensajeUsuario,
        List<ReporteIA> reportes,
        String ubicacionFinca,
        Set<TipoDeCultivo> tipoDeCultivos,
        List<ListaMensajes> historialMensajes
) {
}
