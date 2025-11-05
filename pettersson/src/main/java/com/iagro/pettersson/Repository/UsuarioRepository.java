package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Finca;
import com.iagro.pettersson.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);

}
