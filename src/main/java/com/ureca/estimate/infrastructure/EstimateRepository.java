package com.ureca.estimate.infrastructure;

import com.ureca.request.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateRepository extends JpaRepository<Request, Long> {
}
