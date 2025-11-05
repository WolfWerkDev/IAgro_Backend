package com.iagro.pettersson.Scheduler;

import com.iagro.pettersson.Service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TareasProgramadas {

    @Autowired
    private ReporteService reporteService;

    @Scheduled(fixedRate = 30000) // Cada 30 segundos
    public void procesarReportes() {
        var lista = reporteService.prepararReportes();
        if (!lista.isEmpty()) {
            reporteService.guardarReportes(lista);
            reporteService.limpiarReportes();
        }
    }
}
