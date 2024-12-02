package com.ureca.estimate.infrastructure;

import com.ureca.estimate.domain.Estimate;
import com.ureca.estimate.domain.EstimateImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateImageRepository extends JpaRepository<EstimateImage, Long> {
    List<EstimateImage> findAllByEstimate(Estimate estimate);
}
