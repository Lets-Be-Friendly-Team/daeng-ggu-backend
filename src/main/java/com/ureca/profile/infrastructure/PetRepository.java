package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Pet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // 고객 아이디로 반려견 목록 찾기
    List<Pet> findByCustomerCustomerId(Long customerId);

    // 반려견 이름으로 반려견 찾기
    List<Pet> findByPetName(String petName);

    // 고객 아이디와 반려견 아이디로 특정 반려견 찾기
    Pet findByCustomerCustomerIdAndPetId(Long customerId, Long petId);
}
