package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Mensaje;
import com.iagro.pettersson.Enum.EmisorMensaje;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.chat.idChat = :idChat AND m.emisorMensaje = :emisor")
    long contarMensajesPorChatYEmisor(@Param("idChat") Long idChat, @Param("emisor") EmisorMensaje emisor);

    @Query("SELECT m FROM Mensaje m " +
            "WHERE m.chat.id = :idChat " +
            "ORDER BY m.fechaHora DESC")
    List<Mensaje> findUltimos20MensajesPorChat(@Param("idChat") Long idChat, Pageable pageable);

}
