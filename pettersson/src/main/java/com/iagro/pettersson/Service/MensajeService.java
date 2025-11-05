package com.iagro.pettersson.Service;

import com.iagro.pettersson.DTO.Chat.DatosUsuario;
import com.iagro.pettersson.DTO.Mensaje.NuevoMensaje;
import com.iagro.pettersson.Entity.Chat;
import com.iagro.pettersson.Entity.Mensaje;
import com.iagro.pettersson.Enum.EmisorMensaje;
import com.iagro.pettersson.Enum.TipoPlan;
import com.iagro.pettersson.Repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private ChatService chatService;

    public void primerMensaje(Chat chat, String contenido, EmisorMensaje emisorMensaje) {
        Mensaje mensaje = Mensaje.builder()
                .chat(chat)
                .contenido(contenido)
                .emisorMensaje(emisorMensaje)
                .build();
        mensajeRepository.save(mensaje);
    }

    @Transactional(readOnly = true)
    public int cantidadMensajesPorChat(Long idChat) {
        return ((int) mensajeRepository.contarMensajesPorChatYEmisor(idChat, EmisorMensaje.USUARIO));
    }

    // Se usa cuando el user envía mensaje o el modelo envía respuesta
    @Async("rtExecutor")
    public void guardarNuevoMensaje(Long idChat, String mensaje, EmisorMensaje emisorMensaje) {
        Chat chat = chatService.getChatReference(idChat);
        Mensaje nuevoMensaje = Mensaje.builder()
                .chat(chat)
                .contenido(mensaje)
                .emisorMensaje(emisorMensaje)
                .build();

        mensajeRepository.save(nuevoMensaje);
    }

    // Solo para mensajes que envía el user
    @Transactional
    public void gestorMensajes(DatosUsuario dto, EmisorMensaje emisorMensaje) {
        int cantidadMensajes = cantidadMensajesPorChat(dto.idChat());
        if (cantidadMensajes == dto.maxMensajesPorChat()) {
            throw new RuntimeException("Ha llegado al tope de mensajes por chat.");
        }
        guardarNuevoMensaje(dto.idChat(), dto.mensaje(), emisorMensaje);
    }

    @Transactional(readOnly = true)
    public List<Mensaje> listarUltimosMensajes(Long idChat, int pageNumber) {
        Pageable limit = PageRequest.of(pageNumber, 20);
        return mensajeRepository.findUltimos20MensajesPorChat(idChat, limit);
    }
}
