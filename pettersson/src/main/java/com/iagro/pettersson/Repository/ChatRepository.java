package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT new map(c.id as idChat, c.nombreChat as nombreChat) " +
            "FROM Chat c WHERE c.usuario.id = :idUser " +
            "ORDER BY c.fechaInicio ASC")
    List<Map<String, Object>> listarChatsPorUsuario(@Param("idUser") Long idUser);

}
