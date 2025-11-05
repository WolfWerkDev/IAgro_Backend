package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.SuperUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperUsuarioRepository extends JpaRepository<SuperUsuario, Long> {
    SuperUsuario findByUsername(String username);
}
