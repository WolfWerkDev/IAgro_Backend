package com.iagro.pettersson.Repository;

import com.iagro.pettersson.Entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
