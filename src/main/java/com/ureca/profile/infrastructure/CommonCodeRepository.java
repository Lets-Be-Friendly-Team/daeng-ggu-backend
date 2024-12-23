package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.CommonCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {
    // 코드 아이디로 공통 코드 찾기
    CommonCode findByCodeId(String codeId);

    // 코드명으로 공통 코드 찾기
    List<CommonCode> findByCodeNm(String codeNm);

    // 코드 아이디로 코드명 찾기
    @Query("SELECT c.codeNm FROM CommonCode c WHERE c.codeId = ?1")
    String findCodeNmByCodeId(String codeId);

    // 코드 아이디로 코드 설명 찾기
    @Query("SELECT c.codeDesc FROM CommonCode c WHERE c.codeId = ?1")
    String findCodeDescByCodeId(String codeId);

    // 코드 실명으로 코드 아이디 찾기
    @Query("SELECT c.codeId FROM CommonCode c WHERE c.codeDesc = ?1")
    String findCodeIdByCodeDesc(String codeDesc);
}
