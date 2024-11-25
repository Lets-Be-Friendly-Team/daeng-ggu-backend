package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Breeds;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedsRepository extends JpaRepository<Breeds, Long> {
    // 디자이너 ID로 가능 견종 찾기
    List<Breeds> findByDesignerDesignerId(Long designerId);
}
