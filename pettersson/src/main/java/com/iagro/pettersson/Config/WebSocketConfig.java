package com.iagro.pettersson.Config;

import com.iagro.pettersson.Websocket.Esp32WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final Esp32WebSocketHandler esp32WebSocketHandler;

    public WebSocketConfig(Esp32WebSocketHandler esp32WebSocketHandler) {
        this.esp32WebSocketHandler = esp32WebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(esp32WebSocketHandler, "/ws/esp32")
                .setAllowedOrigins("*"); // Puedes reemplazar "*" por tus dominios frontend autorizados
                //.withSockJS(); // Opcional: permite compatibilidad con navegadores que no soportan WebSocket nativo
    }
}
