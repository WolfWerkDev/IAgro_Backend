package com.iagro.pettersson.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    @Autowired
    private FincaService fincaService;

    // Almacena los clientes conectados (ID -> SseEmitter)
    private final Map<String, SseEmitter> emisores = new ConcurrentHashMap<>();
    private final Map<String, List<String>> clienteAgrolinks = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Registra un nuevo cliente SSE.
     */
    public SseEmitter registrarCliente(String clientId) {
        SseEmitter emisor = new SseEmitter(0L); // Sin timeout
        emisores.put(clientId, emisor);

        emisor.onCompletion(() -> {
            emisores.remove(clientId);
            clienteAgrolinks.remove(clientId);
        });
        emisor.onTimeout(() ->  {
            emisores.remove(clientId);
            clienteAgrolinks.remove(clientId);
        });
        emisor.onError((ex) -> {
            emisores.remove(clientId);
            clienteAgrolinks.remove(clientId);
        });

        enviarEvento(clientId, "connected", "Conexión SSE establecida correctamente 🚀");
        System.out.println("✅ Cliente SSE conectado: " + clientId);

        return emisor;
    }

    public void vincularUsuarioAgrolinks(String clientId, List<String> codigosAgrolinks) {
        clienteAgrolinks.put(clientId, codigosAgrolinks);
    }

    /**
     * Envía un evento SSE a un cliente específico.
     */
    public void enviarEvento(String clientId, String evento, Object data) {
        SseEmitter emisor = emisores.get(clientId);
        if (emisor != null) {
            try {
                emisor.send(SseEmitter.event()
                        .name(evento)
                        .data(data));
            } catch (IOException e) {
                emisores.remove(clientId);
                System.out.println("⚠️ Error enviando evento SSE a " + clientId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Envía un evento SSE a todos los clientes conectados.
     */
    public void enviarEventoATodos(String evento, Object data) {
        for (Map.Entry<String, SseEmitter> entry : emisores.entrySet()) {
            try {
                entry.getValue().send(SseEmitter.event()
                        .name(evento)
                        .data(data));
            } catch (IOException e) {
                emisores.remove(entry.getKey());
                System.out.println("⚠️ Error enviando evento SSE a " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Cierra todas las conexiones SSE activas.
     */
    public void cerrarTodasLasConexiones() {
        emisores.forEach((id, emisor) -> emisor.complete());
        emisores.clear();
        System.out.println("🧹 Todas las conexiones SSE fueron cerradas.");
    }

    @Async("rtExecutor")
    public void reenviarEventoAgrolink(String codigoAgrolink, Object data) {
        String clienteId = clienteAgrolinks.entrySet().stream()
                .filter(entry -> entry.getValue().contains(codigoAgrolink))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (clienteId == null || !emisores.containsKey(clienteId)) {
            return;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("codigoAgrolink", codigoAgrolink);

        // Si data es un Map ya, lo fusionamos
        if (data instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            payload.putAll(map);
        } else if (data instanceof String) {
            // Intentamos parsear JSON string a Map
            String s = (String) data;
            try {
                Map<String, Object> map = objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {});
                payload.putAll(map);
            } catch (Exception e) {
                // No es JSON válido: lo añadimos como campo "data" crudo
                payload.put("data", s);
            }
        } else {
            // Cualquier otro tipo (POJO...), lo colocamos en "data"
            payload.put("data", data);
        }

        enviarEvento(clienteId, "Actualización", payload);
    }

}
