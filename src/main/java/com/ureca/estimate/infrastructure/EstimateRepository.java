package com.ureca.estimate.infrastructure;

import com.ureca.estimate.domain.Estimate;
import com.ureca.request.domain.Request;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, Long> {

    List<Estimate> findAllByDesignerDesignerId(Long designerId);

    List<Estimate> findAllByRequest(Request request);
}
