package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.CommonCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {
    // 코드 아이디로 공통 코드 찾기
    CommonCode findByCodeId(String codeId);

    // 코드명으로 공통 코드 찾기
    List<CommonCode> findByCodeNm(String codeNm);
}