package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Agrolink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgrolinkRepository extends JpaRepository<Agrolink, String> {
    Optional<Agrolink> findByCodigo(String codigo);
}
