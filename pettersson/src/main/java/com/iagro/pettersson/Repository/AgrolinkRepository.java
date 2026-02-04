package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Agrolink;
import com.iagro.pettersson.Entity.Finca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgrolinkRepository extends JpaRepository<Agrolink, String> {
    Optional<Agrolink> findByCodigo(String codigo);

    Optional<List<Agrolink>> findByFinca(Finca finca);
}
