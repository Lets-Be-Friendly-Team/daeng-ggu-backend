package com.ureca.monitoring.infrastructure;

import com.ureca.monitoring.domain.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {}
