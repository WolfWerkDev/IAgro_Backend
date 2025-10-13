package com.iagro.pettersson.Entity;

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
@Table(name = "agrolinks")
public class Agrolink {

    @Id
    private String codigo;

    @Builder.Default
    private String nombreAgrolink = "Mi Agrolink";
    @Builder.Default
    private boolean esVinculado = false;
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    private LocalDateTime fechaVinculacion;

    @ManyToOne
    @JoinColumn(name = "id_finca")
    private Finca finca;

    @OneToMany(mappedBy = "codigoAgrolink")
    private List<Reporte> reportes;

}
