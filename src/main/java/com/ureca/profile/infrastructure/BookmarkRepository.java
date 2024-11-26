package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 고객과 디자이너로 즐겨찾기 찾기
    Bookmark findByCustomerCustomerIdAndDesignerDesignerId(Long customerId, Long designerId);
}
