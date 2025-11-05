package com.iagro.pettersson.Controller;

import com.iagro.pettersson.DTO.Chat.ActualizarNombreChat;
import com.iagro.pettersson.DTO.Chat.ChatCreado;
import com.iagro.pettersson.DTO.Chat.DatosUsuario;
import com.iagro.pettersson.DTO.IAModel.ConsultaModelo;
import com.iagro.pettersson.Enum.EmisorMensaje;
import com.iagro.pettersson.Service.ChatService;
import com.iagro.pettersson.Service.IAService;
import com.iagro.pettersson.Service.MensajeService;
import com.iagro.pettersson.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/iagro")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Se debe obtener el id desde el contextHolder
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private IAService iaService;

    @Autowired
    private MensajeService mensajeService;

    @GetMapping("/listar-chats")
    public ResponseEntity<?> listarChatsUsuario() {
        Long idUser = usuarioService.obtenerIdUsuario();
        try {
            Map<Long, String> listaChats = chatService.listarChatsUsuario(idUser);
            return ResponseEntity.status(HttpStatus.OK).body(listaChats);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/nuevo-chat")
    public ResponseEntity<?> nuevoChat(@Valid @RequestBody DatosUsuario dto) {
        Long idUser = usuarioService.obtenerIdUsuario();
        try {
            ChatCreado chatCreado = chatService.crearChat(dto, idUser);
            ConsultaModelo prompt = iaService.construirPrompt(dto, "Start");

            // Ejecuta la tarea asíncrona pero espera el resultado
            String respuestaIA = iaService.consultarModeloIA(prompt).get();

            String mensajeIALimpio = iaService.procesarRespuestaIAConNombre(respuestaIA, chatCreado.idChat());
            mensajeService.guardarNuevoMensaje(chatCreado.idChat(), mensajeIALimpio, EmisorMensaje.IA);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ChatCreado(chatCreado.idChat(), mensajeIALimpio));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el chat: " + e.getMessage());
        }
    }


    @PutMapping("/cambiar-nombre-chat")
    public ResponseEntity<?> modificarNombreChat(@RequestBody ActualizarNombreChat dto) {
        try {
            String nameChat = chatService.actualizarNombreChat(dto);
            return ResponseEntity.status(HttpStatus.OK).body(nameChat);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/borrar-chat")
    public ResponseEntity<?> eliminarChat(@RequestParam Long idChat) {
        try {
            chatService.eliminarChat(idChat);
            return ResponseEntity.status(HttpStatus.OK).body("Chat eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
