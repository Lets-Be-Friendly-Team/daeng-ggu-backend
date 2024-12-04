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
import org.springframework.data.repository.query.Param;
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
                    "SELECT ROUND(AVG(r.review_star), 2) AS reviewStarAvg "
                            + "FROM review r "
                            + "JOIN designer d ON d.designer_id = r.designer_id "
                            + "WHERE d.designer_id = :designerId",
            nativeQuery = true)
    Double findAverageReviewStarByDesignerId(Long designerId);

    // 디자이너의 전체 리뷰 좋아요 수 조회
    @Query(
            value =
                    "SELECT SUM(r.review_like_cnt) AS reviewLikeCntAll "
                            + "FROM review r "
                            + "JOIN designer d ON d.designer_id = r.designer_id "
                            + "WHERE d.designer_id = :designerId",
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

    // 디자이너 미용 가능 견종 대분류 코드 조회
    @Query(
            "SELECT DISTINCT b.possibleMajorBreedCode "
                    + "FROM Designer d "
                    + "JOIN d.breeds b "
                    + "WHERE d.designerId = :designerId")
    List<String> findPossibleMajorBreedCodesByDesignerId(@Param("designerId") Long designerId);

    // 전체 디자이너 목록 조회 - 검색어: 닉네임(업체명)/디자이너명/견종명
    @Query(
            "SELECT "
                    + "d.designerId AS designerId, "
                    + "d.designerName AS designerName, "
                    + "d.officialName AS nickname, "
                    + "d.designerImgUrl AS designerImgUrl, "
                    + "ROUND(AVG(r.reviewStar), 2) AS reviewStarAvg, "
                    + "COUNT(DISTINCT bk.bookmarkId) AS bookmarkCnt, "
                    + "d.address1 AS address1, "
                    + "d.address2 AS address2, "
                    + "d.detailAddress AS detailAddress, "
                    + "d.xPosition AS xPosition, "
                    + "d.yPosition AS yPosition "
                    + "FROM Designer d "
                    + "LEFT JOIN Review r ON d.designerId = r.designer.designerId "
                    + "LEFT JOIN Bookmark bk ON d.designerId = bk.designer.designerId "
                    + "LEFT JOIN Services s ON d.designerId = s.designer.designerId "
                    + "LEFT JOIN CommonCode c ON c.codeId = s.providedServicesCode "
                    + "LEFT JOIN Breeds b ON d.designerId = b.designer.designerId "
                    + "WHERE "
                    + "(d.officialName LIKE CONCAT('%', :searchKeyword, '%') "
                    + "OR d.designerName LIKE CONCAT('%', :searchKeyword, '%') "
                    + "OR c.codeDesc LIKE CONCAT('%', :searchKeyword, '%')) "
                    + "GROUP BY "
                    + "d.designerId, d.designerName, d.officialName, d.designerImgUrl, "
                    + "d.address1, d.address2, d.detailAddress, d.xPosition, d.yPosition")
    List<Object[]> searchDesignersByKeyword(@Param("searchKeyword") String searchKeyword);

    // 프리미엄 서비스 제공 디자이너 목록 조회 (providedServicesCode : S2-스파, S3-풀케어, S4-스트리밍)
    @Query(
            "SELECT "
                    + "d.designerId AS designerId, "
                    + "d.designerName AS designerName, "
                    + "d.officialName AS nickname, "
                    + "d.designerImgUrl AS designerImgUrl, "
                    + "ROUND(AVG(r.reviewStar), 2) AS reviewStarAvg, "
                    + "COUNT(DISTINCT bk.bookmarkId) AS bookmarkCnt, "
                    + "d.address1 AS address1, "
                    + "d.address2 AS address2, "
                    + "d.detailAddress AS detailAddress, "
                    + "d.xPosition AS xPosition, "
                    + "d.yPosition AS yPosition "
                    + "FROM Designer d "
                    + "LEFT JOIN Review r ON d.designerId = r.designer.designerId "
                    + "LEFT JOIN Bookmark bk ON d.designerId = bk.designer.designerId "
                    + "LEFT JOIN Services s ON d.designerId = s.designer.designerId "
                    + "LEFT JOIN CommonCode c ON c.codeId = s.providedServicesCode "
                    + "LEFT JOIN Breeds b ON d.designerId = b.designer.designerId "
                    + "WHERE "
                    + "(d.officialName LIKE CONCAT('%', :searchKeyword, '%') "
                    + "OR d.designerName LIKE CONCAT('%', :searchKeyword, '%') "
                    + "OR c.codeDesc LIKE CONCAT('%', :searchKeyword, '%')) "
                    + "AND s.providedServicesCode = :providedServicesCode "
                    + "GROUP BY "
                    + "d.designerId, d.designerName, d.officialName, d.designerImgUrl, "
                    + "d.address1, d.address2, d.detailAddress, d.xPosition, d.yPosition")
    List<Object[]> findDesignersByProvidedServiceCode(
            @Param("providedServicesCode") String providedServicesCode,
            @Param("searchKeyword") String searchKeyword);
}
