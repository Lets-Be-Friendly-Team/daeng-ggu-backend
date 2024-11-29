package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Designer;
import com.ureca.profile.presentation.dto.Breed;
import com.ureca.profile.presentation.dto.DesignerDetail;
import com.ureca.profile.presentation.dto.DesignerProfile;
import com.ureca.profile.presentation.dto.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignerRepository extends JpaRepository<Designer, Long> {

    Optional<Designer> findByDesignerId(Long designerId);

    // 디자이너 정보 조회
    @Query(
            "SELECT new com.ureca.profile.presentation.dto.DesignerProfile("
                    + "d.designerId, "
                    + "d.designerName, "
                    + "d.officialName, "
                    + "d.designerImgUrl, "
                    + "d.designerImgName, "
                    + "d.address1, "
                    + "d.address2, "
                    + "d.detailAddress, "
                    + "d.introduction, "
                    + "d.workExperience) "
                    + "FROM Designer d WHERE d.designerId = :designerId")
    DesignerProfile findDesignerProfileByDesignerId(Long designerId);

    // 디자이너 상세 정보 조회
    @Query(
            "SELECT new com.ureca.profile.presentation.dto.DesignerDetail("
                    + "d.designerId, "
                    + "d.designerName, "
                    + "d.officialName, "
                    + "d.designerImgUrl, "
                    + "d.designerImgName, "
                    + "d.address1, "
                    + "d.address2, "
                    + "d.detailAddress, "
                    + "d.introduction, "
                    + "d.phone, "
                    + "d.email, "
                    + "d.businessNumber, "
                    + "d.workExperience) "
                    + "FROM Designer d WHERE d.designerId = :designerId")
    DesignerDetail findDesignerDetailByDesignerId(Long designerId);

    // 디자이너 평균 별점 조회 (소수점 2자리까지 반올림)
    @Query(
            value =
                    "SELECT ROUND(AVG(r.reviewStar), 2) AS reviewStarAvg "
                            + "FROM review r "
                            + "JOIN designer d ON d.designerId = r.designerId "
                            + "WHERE d.designerId = :designerId",
            nativeQuery = true)
    Double findAverageReviewStarByDesignerId(Long designerId);

    // 디자이너의 전체 리뷰 좋아요 수 조회
    @Query(
            value =
                    "SELECT SUM(r.reviewLikeCnt) AS reviewLikeCntAll "
                            + "FROM review r "
                            + "JOIN designer d ON d.designerId = r.designerId "
                            + "WHERE d.designerId = :designerId",
            nativeQuery = true)
    Integer findTotalReviewLikeCountByDesignerId(Long designerId);

    // 디자이너 제공 서비스 조회
    @Query(
            "SELECT new com.ureca.profile.presentation.dto.Service(s.providedServicesCode, c.codeDesc) "
                    + "FROM Services s "
                    + "JOIN Designer d ON d.designerId = s.designer.designerId "
                    + "JOIN CommonCode c ON c.codeId = s.providedServicesCode "
                    + "WHERE d.designerId = :designerId")
    List<Service> findDesignerProvidedServices(Long designerId);

    // 디자이너 미용 가능 견종 조회
    @Query(
            "SELECT new com.ureca.profile.presentation.dto.Breed(b.possibleMajorBreedCode, c.codeDesc) "
                    + "FROM Breeds b "
                    + "JOIN CommonCode c ON c.codeId = b.possibleMajorBreedCode "
                    + "WHERE b.designer.designerId = :designerId")
    List<Breed> findDesignerMajorBreeds(Long designerId);
}
