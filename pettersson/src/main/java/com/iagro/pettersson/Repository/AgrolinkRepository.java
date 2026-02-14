package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Agrolink;
import com.iagro.pettersson.Entity.Finca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AgrolinkRepository extends JpaRepository<Agrolink, String> {
    Optional<Agrolink> findByCodigo(String codigo);

    Optional<List<Agrolink>> findByFinca(Finca finca);
    List<Agrolink> findByCodigoIn(Set<String> codigos);
}
