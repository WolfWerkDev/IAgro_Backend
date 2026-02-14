package com.iagro.pettersson.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iagro.pettersson.DTO.Reporte.ReporteIn;
import com.iagro.pettersson.Entity.Agrolink;
import com.iagro.pettersson.Entity.Reporte;
import com.iagro.pettersson.Repository.ReporteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgrolinkService agrolinkService;

    private final ConcurrentHashMap<String, Object> reportes = new ConcurrentHashMap<>();

    @Async("rtExecutor")
    public void agregarReporte(String codigo, Object data) {
        reportes.put(codigo, data);
    }

    public void limpiarReportes() {
        reportes.clear();
    }


    public List<Reporte> prepararReportes() {

        Set<String> codigos = new HashSet<>(reportes.keySet());
        List<Agrolink> agrolinks = reporteRepository.findByCodigoAgrolink(codigos);
        Map<String, Agrolink> agrolinkMap = agrolinks.stream()
                .collect(Collectors.toMap(Agrolink::getCodigo, Function.identity()));
        List<Reporte> lista = new ArrayList<>();

        reportes.forEach((codigo, data) -> {
            try {
                Agrolink agrolink = agrolinkMap.get(codigo);
                if (agrolink == null) return;
                ReporteIn reporteIn = objectMapper.convertValue(data, ReporteIn.class);

                Reporte reporte = Reporte.builder()
                        .codigoAgrolink(agrolink)
                        .temperaturaSuelo(reporteIn.temperaturaSuelo())
                        .humedadSuelo(reporteIn.humedadSuelo())
                        .temperaturaAmbiente(reporteIn.temperaturaAmbiente())
                        .pHSuelo(reporteIn.pHSuelo())
                        .conductividadSuelo(reporteIn.conductividadSuelo())
                        .build();

                lista.add(reporte);

            } catch (Exception e) {
                throw new RuntimeException("Error deserializando reporte. " + e.getMessage());
            }
        });

        return lista;
    }

    @Transactional
    @Async("reportExecutor")
    public void guardarReportes(List<Reporte> lista) {
        reporteRepository.saveAll(lista);
    }

    @Transactional(readOnly = true)
    public List<Reporte> obtenerUltimosReportes(List<String> codigosAgrolink) {
        return reporteRepository.findUltimosReportesPorAgrolinks(codigosAgrolink);
    }
}
