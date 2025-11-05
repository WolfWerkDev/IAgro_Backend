package com.iagro.pettersson.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChat;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @JsonBackReference
    private Usuario usuario;

    private String nombreChat;
    @Builder.Default
    private LocalDateTime fechaInicio = LocalDateTime.now();

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Mensaje> mensajes;

}
