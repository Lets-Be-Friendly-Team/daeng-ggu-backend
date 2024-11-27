package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Bookmark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 특정 고객 아이디로 즐겨찾기한 디자이너 정보 리스트 조회
    List<Bookmark> findByCustomerCustomerId(Long customerId);

    // 고객과 디자이너로 즐겨찾기 찾기
    Bookmark findByCustomerCustomerIdAndDesignerDesignerId(Long customerId, Long designerId);
}
