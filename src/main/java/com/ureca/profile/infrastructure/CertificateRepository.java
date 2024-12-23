package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Certificate;
import com.ureca.profile.domain.Designer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    // 디자이너 ID로 인증서 찾기
    List<Certificate> findByDesignerDesignerId(Long designerId);

    // 디자이너 인증서 URL 조회
    @Query("SELECT c.imgUrl FROM Certificate c WHERE c.designer.designerId = :designerId")
    List<String> findImgUrlsByDesignerId(Long designerId);

    // 디자이너 객체로 인증서 이미지 URL 목록 조회
    @Query("SELECT c.imgUrl FROM Certificate c WHERE c.designer = :designer")
    List<String> findImgUrlsByDesigner(Designer designer);

    // 디자이너 인증서 삭제
    void deleteByDesignerDesignerIdAndImgUrl(Long designerId, String imgUrl);

    void deleteByDesignerAndImgUrl(Designer designer, String imgUrl);
}
