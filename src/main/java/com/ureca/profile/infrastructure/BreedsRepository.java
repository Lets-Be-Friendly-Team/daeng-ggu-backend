package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Breeds;
import com.ureca.profile.domain.Designer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedsRepository extends JpaRepository<Breeds, Long> {
    // 디자이너 ID로 가능 견종 찾기
    List<Breeds> findByDesignerDesignerId(Long designerId);

    @Query(
            "SELECT b.designer FROM Breeds b WHERE b.possibleMajorBreedCode = :majorBreedCode AND b.designer IN :designers")
    List<Designer> findDesignerByPossibleMajorBreedCode(
            @Param("majorBreedCode") String majorBreedCode,
            @Param("designers") List<Designer> designers);
}
