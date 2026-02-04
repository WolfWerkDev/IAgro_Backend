package com.iagro.pettersson.Controller;

import com.iagro.pettersson.DTO.Chat.DatosUsuario;
import com.iagro.pettersson.DTO.IAModel.ConsultaModelo;
import com.iagro.pettersson.DTO.Mensaje.RespuestaIA;
import com.iagro.pettersson.Entity.Mensaje;
import com.iagro.pettersson.Enum.EmisorMensaje;
import com.iagro.pettersson.Service.IAService;
import com.iagro.pettersson.Service.MensajeService;
import com.iagro.pettersson.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iagro-mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private IAService iaService;

    @PostMapping("/nuevo-mensaje")
    public ResponseEntity<?> nuevoMensaje(@Valid @RequestBody DatosUsuario dto) {
        try {
            Long idUser = usuarioService.obtenerIdUsuario();

            // Guardar mensaje del usuario
            mensajeService.gestorMensajes(dto, EmisorMensaje.USUARIO);

            // Construir prompt
            ConsultaModelo prompt = iaService.construirPrompt(dto, "Normal");

            // Llamada a la IA (usa thread pool dedicado, pero espera la respuesta)
            String respuestaIA = iaService.consultarModeloIA(prompt).get();

            // Guardar respuesta de la IA
            mensajeService.guardarNuevoMensaje(dto.idChat(), respuestaIA, EmisorMensaje.IA);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RespuestaIA(respuestaIA));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }


    @GetMapping("/mensajes-chat/{idChat}/{page}")
    public ResponseEntity<?> obtenerMensajesPorChat(@PathVariable Long idChat, @PathVariable int page) {
        try {
            List<Mensaje> listaMensajes = mensajeService.listarUltimosMensajes(idChat, page);
            return ResponseEntity.status(HttpStatus.OK).body(listaMensajes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}