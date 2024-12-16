package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Breeds;
import com.ureca.profile.domain.Designer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BreedsRepository extends JpaRepository<Breeds, Long> {
    // 디자이너 ID로 가능 견종 찾기
    List<Breeds> findByDesignerDesignerId(Long designerId);

    Breeds findByDesignerAndPossibleMajorBreedCode(
            Designer designer, String possibleMajorBreedCode);

    // 대분류 견종 코드 조회 (디자이너 기준)
    @Query("SELECT DISTINCT b.possibleMajorBreedCode FROM Breeds b WHERE b.designer = :designer")
    List<String> findPossibleMajorBreedCodesByDesigner(Designer designer);

    @Modifying
    @Transactional
    @Query(
            "DELETE FROM Breeds b WHERE b.designer = :designer AND b.possibleMajorBreedCode = :possibleMajorBreedCode")
    void deleteByDesignerAndPossibleMajorBreedCode(
            Designer designer, String possibleMajorBreedCode);

    @Query(
            "SELECT b.designer FROM Breeds b WHERE b.possibleMajorBreedCode = :majorBreedCode AND b.designer IN :designers")
    List<Designer> findDesignerByPossibleMajorBreedCode(
            @Param("majorBreedCode") String majorBreedCode,
            @Param("designers") List<Designer> designers);
}
