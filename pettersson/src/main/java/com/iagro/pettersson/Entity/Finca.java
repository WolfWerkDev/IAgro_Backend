package com.iagro.pettersson.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.iagro.pettersson.Enum.TipoDeCultivo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Random;

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

    @ElementCollection(targetClass = TipoDeCultivo.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "finca_tipos_de_cultivo",
            joinColumns = @JoinColumn(name = "finca_id")
    )
    @Column(name = "tipo_de_cultivo")
    private Set<TipoDeCultivo> tiposDeCultivo;

    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private String fotoFinca;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "finca")
    @JsonManagedReference
    private List<Agrolink> agrolinks;

}
