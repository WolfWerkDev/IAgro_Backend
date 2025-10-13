package com.iagro.pettersson.Entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "superusuarios")
public class SuperUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSuperUsuario;

    private String username;
    private String password;
    private String codigo; // Código para registro
}
