package com.iagro.pettersson.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReporte;

    @ManyToOne
    @JoinColumn(name = "codigo_agrolink")
    @JsonBackReference
    private Agrolink codigoAgrolink;

    private float temperaturaAmbiente;
    private float temperaturaSuelo;
    private float humedadSuelo;
    @Column(name = "ph_suelo")
    private float pHSuelo;
    private float conductividadSuelo;

    @Builder.Default
    private LocalDateTime fechaReporte = LocalDateTime.now();
}
