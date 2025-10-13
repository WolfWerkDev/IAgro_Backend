package com.iagro.pettersson.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;
    private LocalDate fechaNacimiento;

    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now(); // Por defecto la fecha en que se crea el user
    private String telefono;
    private String correo;
    private String contraseña;

    @Builder.Default
    private String fotoPerfil = "fotoPerfilDefault.png"; // Una foto por defecto
    private LocalDateTime fechaInicioPlan;
    private LocalDateTime fechaFinPlan;

    @Builder.Default
    private boolean esActivo = true;
    @Builder.Default
    private boolean esAdmin = false;

    // FK N:1 con Plan (Tabla Plan)
    @ManyToOne
    @JoinColumn(name = "id_plan")
    @Builder.Default
    private Plan plan = Plan.builder().idPlan(1L).build();

    @OneToMany(mappedBy = "usuario")
    private List<Finca> fincas;

    @OneToMany(mappedBy = "usuario")
    private List<Chat> chats;
}
