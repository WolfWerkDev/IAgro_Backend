package com.iagro.pettersson.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iagro.pettersson.DTO.Chat.ActualizarNombreChat;
import com.iagro.pettersson.DTO.Chat.DatosUsuario;
import com.iagro.pettersson.DTO.IAModel.ConsultaModelo;
import com.iagro.pettersson.DTO.Mensaje.ListaMensajes;
import com.iagro.pettersson.DTO.Reporte.ReporteIA;
import com.iagro.pettersson.Entity.Mensaje;
import com.iagro.pettersson.Entity.Reporte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class IAService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatService chatService;

    private final WebClient webClient;

    public IAService(WebClient webClient) {
        this.webClient = webClient;
    }


    public String promptGenerico() {
        return """
            Eres un asistente agrícola experto en todos los cultivos y sistemas. Responde según lo que pregunte el usuario sobre síntomas o procedimientos:
    
            1. Para problemas, indica causas concisas.
            2. Para procedimientos, explica pasos claros.
            3. Incluye acciones inmediatas seguras si aplican.
            4. Señala cuándo consultar a un experto.
            5. Usa lenguaje claro, directo, práctico para WhatsApp.
            6. No añadas información no indicada ni saludos automáticos.
            7. Haz un análisis de las variables enviadas, la ubicación y los tipos de cultivo para validar toda la información y su relación con la pregunta del usuario. Cita todas explícitamente. Solo cuando el usuario lo pida o en el primer chat
            8. Si una variable no llega o es null no la incluyas 
            9. Si lo ves necesario has preguntas para pedir mas contexto al usuario y ser más efectivo en las recomendaciones
        """;
    }

    public List<ReporteIA> listaReportesPrompt(List<String> codigosAgrolinkg) {
        List<Reporte> listaReportes = reporteService.obtenerUltimosReportes(codigosAgrolinkg);

        return listaReportes.stream()
                .map(r -> {
                    // Solo agregar los valores extras si no son todos 0
                    Float temperaturaAmbiente = r.getTemperaturaAmbiente() != 0 ? r.getTemperaturaAmbiente() : null;
                    Float phSuelo = r.getPHSuelo() != 0 ? r.getPHSuelo() : null;
                    Float conductividadSuelo = r.getConductividadSuelo() != 0 ? r.getConductividadSuelo() : null;

                    return new ReporteIA(
                            r.getCodigoAgrolink().getCodigo(),
                            r.getTemperaturaSuelo(),
                            r.getHumedadSuelo(),
                            temperaturaAmbiente,
                            phSuelo,
                            conductividadSuelo
                    );
                })
                .toList();
    }

    // Para contexto se traen los últimos 20 mensajes del chat
    public List<ListaMensajes> ultimosMensajes(Long idChat) {
        List<Mensaje> mensajes = mensajeService.listarUltimosMensajes(idChat, 0);
        return mensajes.stream()
                .map(m -> new ListaMensajes(
                        m.getEmisorMensaje(),
                        m.getContenido()
                ))
                .toList();
    }

    // Devuelve el prompt final en cada caso
    public ConsultaModelo construirPrompt(DatosUsuario dto, String caso) {
        // 2 casos Start -> primer prompt del chat, Normal -> chat continuo

        if ("Start".equals(caso)) {
            List<ReporteIA> reportes = listaReportesPrompt(dto.codigosAgrolink());
            String sugerenciaName = "En este primer mensaje sugiere un nombre para el chat y me lo das de primeras despues de una clave \"Nombre:\" ";
            return new ConsultaModelo(promptGenerico(), sugerenciaName, dto.mensaje(), reportes, dto.ubicacionFinca(), dto.tiposDeCultivo(), null);
        }
        else {
            return new ConsultaModelo(promptGenerico(), null, dto.mensaje(), null, null, null, ultimosMensajes(dto.idChat()));
        }
    }

    @Async("iaExecutor")
    public CompletableFuture<String> consultarModeloIA(ConsultaModelo consulta) {
        try {
            List<Map<String, Object>> userParts = new ArrayList<>();

            if (consulta.mensajeUsuario() != null) {
                userParts.add(Map.of("text", consulta.mensajeUsuario()));
            }

            if (consulta.nombreChat() != null) {
                userParts.add(Map.of("text", "Nombre del chat: " + consulta.nombreChat()));
            }

            if (consulta.ubicacionFinca() != null) {
                userParts.add(Map.of("text", "Ubicación de la finca: " + consulta.ubicacionFinca()));
            }

            if (consulta.tipoDeCultivos() != null && !consulta.tipoDeCultivos().isEmpty()) {
                userParts.add(Map.of("text", "Tipos de cultivo: " + consulta.tipoDeCultivos()));
            }

            if (consulta.reportes() != null && !consulta.reportes().isEmpty()) {
                userParts.add(Map.of("text", "Reportes: " + consulta.reportes()));
            }

            if (consulta.historialMensajes() != null) {
                userParts.add(Map.of("text", "Historial de mensajes: " + consulta.historialMensajes()));
            }

            // Request body
            Map<String, Object> requestBody = new HashMap<>();

            // System instruction (prompt base)
            if (consulta.programmingPrompt() != null) {
                requestBody.put(
                        "systemInstruction",
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", consulta.programmingPrompt())
                                )
                        )
                );
            }

            // User content (OBLIGATORIO role)
            requestBody.put(
                    "contents",
                    List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", userParts
                            )
                    )
            );

            String jsonToSend = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(requestBody);

            System.out.println("JSON enviado a Gemini:\n" + jsonToSend);

            String jsonResponse = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/gemini-2.5-flash-lite:generateContent")
                            .queryParam("key", apiKey)
                            .build()
                    )
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(jsonResponse);

            String respuesta = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return CompletableFuture.completedFuture(respuesta);

        } catch (Exception e) {
            System.err.println("Excepción capturada: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error consultando o parseando respuesta de Gemini", e);
        }
    }




    // Método para extraer nombre de chat y devolver contenido limpio
    /**
     * Procesa la respuesta de Gemini para extraer el nombre del chat si existe
     * y devuelve el mensaje limpio para el frontend.
     *
     * @param respuestaIA Texto completo recibido de Gemini
     * @param idChat      ID del chat para actualizar el nombre en DB
     * @return mensaje limpio para mostrar al usuario
     */
    public String procesarRespuestaIAConNombre(String respuestaIA, Long idChat) {
        if (respuestaIA == null || respuestaIA.isBlank()) {
            return respuestaIA;
        }

        // Regex robusto: busca "Nombre:" al inicio o tras saltos, y corta hasta el primer salto doble
        Pattern pattern = Pattern.compile("(?s)^\\s*Nombre:\\s*(.*?)\\s*(?:\\r?\\n){1,2}");
        Matcher matcher = pattern.matcher(respuestaIA);

        String mensajeLimpio = respuestaIA;

        if (matcher.find()) {
            String nombreChat = matcher.group(1).trim();

            if (!nombreChat.isEmpty()) {
                ActualizarNombreChat dtoNombre = new ActualizarNombreChat(idChat, nombreChat);
                chatService.actualizarNombreChat(dtoNombre);
            }

            // Elimina la parte inicial (Nombre: ...) y deja el resto limpio
            mensajeLimpio = respuestaIA.substring(matcher.end()).trim();
        }

        return mensajeLimpio;
    }

}
