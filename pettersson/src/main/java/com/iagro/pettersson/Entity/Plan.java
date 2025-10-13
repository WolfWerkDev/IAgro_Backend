package com.iagro.pettersson.Entity;

import com.iagro.pettersson.Enum.TipoPlan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "planes")
public class Plan {

    @Id
    private Long idPlan;
    @Enumerated(EnumType.STRING)
    private TipoPlan tipoPlan;
    private String descripcion;
    private float precioMensual;
    private int maxFincas;
    private int maxAgrolinksPorFinca;
    private int maxMensajesChat;
    private String variablesDisponibles;
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @Builder.Default
    private boolean esActivo = true;

    @OneToMany(mappedBy = "plan")
    private List<Usuario> usuarios;
}
