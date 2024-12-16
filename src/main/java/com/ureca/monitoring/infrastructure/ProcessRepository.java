package com.ureca.monitoring.infrastructure;

import com.ureca.monitoring.domain.Process;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepository extends JpaRepository<Process, Long> {}
