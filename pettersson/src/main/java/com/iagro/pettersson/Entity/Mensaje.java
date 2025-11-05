package com.iagro.pettersson.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.iagro.pettersson.Enum.EmisorMensaje;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensaje;

    @ManyToOne
    @JoinColumn(name = "id_chat")
    @JsonBackReference
    private Chat chat;

    private String contenido;
    @Builder.Default
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private EmisorMensaje emisorMensaje;
}
