package com.ureca.estimate.infrastructure;

import com.ureca.estimate.domain.Estimate;
import com.ureca.profile.domain.Pet;
import com.ureca.request.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, Long> {

    List<Estimate> findAllByDesignerDesignerId(Long designerId);

    List<Estimate> findAllByRequest(Request request);
}
