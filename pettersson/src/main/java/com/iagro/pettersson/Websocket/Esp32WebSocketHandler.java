package com.iagro.pettersson.Websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iagro.pettersson.Service.ReporteService;
import com.iagro.pettersson.Service.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Esp32WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SseService sseService;

    @Autowired
    private ReporteService reporteService;

    private static final Logger log = LoggerFactory.getLogger(Esp32WebSocketHandler.class);

    // Mapeo: códigoAgrolink → sesión WebSocket
    private final ConcurrentHashMap<String, WebSocketSession> sesionesPorCodigo = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<WebSocketSession, Boolean> handshakePorSesion = new ConcurrentHashMap<>();


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("🔌 Nueva conexión WebSocket abierta: {}", session.getId());
        session.sendMessage(new TextMessage("{\"status\":\"connected\",\"message\":\"Conexión establecida correctamente 🚀\"}"));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("📩 Mensaje recibido: {}", payload);

        try {
            JsonNode json = objectMapper.readTree(payload);

            boolean handshakeRealizado = handshakePorSesion.getOrDefault(session, false);

            // 🔹 Mensaje de handshake
            if (!handshakeRealizado && json.has("codigoAgrolink")) {
                String codigo = json.get("codigoAgrolink").asText();
                sesionesPorCodigo.put(codigo, session);
                handshakePorSesion.put(session, true);
                log.info("✅ ESP32 registrado con códigoAgrolink: {}", codigo);
                session.sendMessage(new TextMessage("{\"ack\":\"handshake_ok\",\"codigoAgrolink\":\"" + codigo + "\"}"));
                return;
            }

            // 🔹 Mensaje de datos normales
            String codigo = obtenerCodigoPorSesion(session);
            if (codigo == null) {
                enviarMensajeSeguro(session, "{\"error\":\"No identificado. Envía primero el codigoAgrolink.\"}");
                return;
            }

            // 🔹 Aquí ya sabemos que la sesión está identificada
            // 1️⃣ Llamada al SSE para notificar al front
            sseService.reenviarEventoAgrolink(codigo, json);
            // 2️⃣ Llamada al servicio que valida la info antes de guardar en DB
            reporteService.agregarReporte(codigo, json);

            session.sendMessage(new TextMessage("{\"ack\":\"data_received\",\"codigoAgrolink\":\"" + codigo + "\"}"));

        } catch (Exception e) {
            log.error("❌ Error procesando mensaje: {}", e.getMessage());
            enviarMensajeSeguro(session, "{\"error\":\"Formato JSON inválido o error interno\"}");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String codigo = eliminarSesionPorSesion(session);
        log.warn("⚠️ Conexión cerrada: {} (Agrolink: {}) | Estado: {}", session.getId(), codigo, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("❌ Error de transporte en sesión {}: {}", session.getId(), exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }

    // 🔧 Método auxiliar para obtener el código por sesión
    private String obtenerCodigoPorSesion(WebSocketSession session) {
        return sesionesPorCodigo.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
    }

    // 🔧 Método auxiliar para eliminar sesión por instancia
    private String eliminarSesionPorSesion(WebSocketSession session) {
        String codigo = obtenerCodigoPorSesion(session);
        if (codigo != null) {
            sesionesPorCodigo.remove(codigo);
        }
        return codigo;
    }

    // 🔧 Enviar mensaje sin lanzar excepción
    private void enviarMensajeSeguro(WebSocketSession session, String mensaje) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(mensaje));
            }
        } catch (IOException e) {
            log.error("❌ No se pudo enviar mensaje a sesión {}: {}", session.getId(), e.getMessage());
        }
    }

    // 📡 Enviar mensaje a un ESP32 específico
    public void enviarMensajeA(String codigoAgrolink, String mensaje) {
        WebSocketSession session = sesionesPorCodigo.get(codigoAgrolink);
        if (session != null && session.isOpen()) {
            enviarMensajeSeguro(session, mensaje);
        } else {
            log.warn("⚠️ No hay sesión activa para el Agrolink {}", codigoAgrolink);
        }
    }

    // 📢 Broadcast a todos los ESP32 conectados
    public void broadcast(String mensaje) {
        sesionesPorCodigo.forEach((codigo, session) -> enviarMensajeSeguro(session, mensaje));
    }
}
