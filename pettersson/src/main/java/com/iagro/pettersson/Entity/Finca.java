package com.iagro.pettersson.Entity;

import com.iagro.pettersson.Enum.TipoDeCultivo;
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
@Table(name = "fincas")
public class Finca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFinca;

    @Builder.Default
    private String nombreFinca = "Mi finca";
    private String ubicacion;
    @Enumerated(EnumType.STRING)
    private TipoDeCultivo tipoDeCultivo;

    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    @Builder.Default
    private String fotoFinca = "FotoFincaDefault.png";

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "finca")
    private List<Agrolink> agrolinks;
}
