package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Agrolink;
import com.iagro.pettersson.Entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    @Query("SELECT r FROM Reporte r " +
            "WHERE r.codigoAgrolink.codigo IN :codigos " +
            "AND r.fechaReporte = (" +
            "   SELECT MAX(r2.fechaReporte) FROM Reporte r2 WHERE r2.codigoAgrolink = r.codigoAgrolink" +
            ")")
    List<Reporte> findUltimosReportesPorAgrolinks(@Param("codigos") List<String> codigos);

}
