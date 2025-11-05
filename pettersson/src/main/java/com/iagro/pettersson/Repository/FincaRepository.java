package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Finca;
import com.iagro.pettersson.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FincaRepository extends JpaRepository<Finca, Long> {
    List<Finca> findByUsuario(Usuario usuario);
}
