package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Breeds;
import com.ureca.profile.domain.Designer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedsRepository extends JpaRepository<Breeds, Long> {
    // 디자이너 ID로 가능 견종 찾기
    List<Breeds> findByDesignerDesignerId(Long designerId);

    List<Designer> findDesignerByPossibleMajorBreedCode(
            String majorBreedCode, List<Designer> designers);
}
