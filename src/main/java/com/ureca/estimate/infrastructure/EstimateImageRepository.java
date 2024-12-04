package com.ureca.estimate.infrastructure;

import com.ureca.estimate.domain.Estimate;
import com.ureca.estimate.domain.EstimateImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstimateImageRepository extends JpaRepository<EstimateImage, Long> {
    List<EstimateImage> findAllByEstimate(Estimate estimate);
}
