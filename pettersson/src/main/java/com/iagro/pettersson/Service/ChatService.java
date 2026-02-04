package com.iagro.pettersson.Service;

import com.iagro.pettersson.DTO.Chat.ActualizarNombreChat;
import com.iagro.pettersson.DTO.Chat.ChatCreado;
import com.iagro.pettersson.DTO.Chat.DatosUsuario;
import com.iagro.pettersson.DTO.Chat.NuevoNombreDTO;
import com.iagro.pettersson.Entity.Chat;
import com.iagro.pettersson.Entity.Mensaje;
import com.iagro.pettersson.Entity.Usuario;
import com.iagro.pettersson.Enum.EmisorMensaje;
import com.iagro.pettersson.Repository.ChatRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    @Lazy
    private MensajeService mensajeService;

    @PersistenceContext
    private EntityManager entityManager;

    public Chat crearPrimerChat(String mensaje, Long idUser) {
        Usuario usuario = usuarioService.getUsuarioReference(idUser);
        String nombreChat = generarNombreChat();

        Chat chat = Chat.builder()
                .usuario(usuario)
                .nombreChat(nombreChat)
                .build();
        return chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public Chat obtenerChatPorId(Long idChat) {
        return chatRepository.findById(idChat).orElseThrow(() -> new RuntimeException("No se encontró el chat con id: " + idChat));
    }

    // Solo usa la referencia FK al chat para guardar el mensaje cuando viene con id desde el front
    @Transactional(readOnly = true)
    public Chat getChatReference(Long idChat) {
        return entityManager.getReference(Chat.class, idChat);
    }


    @Transactional
    public ChatCreado crearChat(DatosUsuario dtoChat, Long idUser) {
        Chat chat = crearPrimerChat(dtoChat.mensaje(), idUser);
        mensajeService.primerMensaje(chat, dtoChat.mensaje(), EmisorMensaje.USUARIO);

        return new ChatCreado(chat.getIdChat(),null);
    }

    public String generarNombreChat() {
        return "Nuevo chat";
    }

    @Transactional
    public NuevoNombreDTO actualizarNombreChat(ActualizarNombreChat dto) {
        Chat chat = getChatReference(dto.idChat());
        chat.setNombreChat(dto.nuevoNombre());
        return new NuevoNombreDTO(chat.getNombreChat());
    }

    @Transactional
    public void eliminarChat(Long idChat) {
        Chat chat = getChatReference(idChat);
        chatRepository.delete(chat);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> listarChatsUsuario(Long idUser) {
        List<Map<String, Object>> chats = chatRepository.listarChatsPorUsuario(idUser);

        Map<Long, String> mapChats = chats.stream()
                .collect(Collectors.toMap(
                        m -> (Long) m.get("idChat"),
                        m -> (String) m.get("nombreChat"),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
        return mapChats;
    }

}
