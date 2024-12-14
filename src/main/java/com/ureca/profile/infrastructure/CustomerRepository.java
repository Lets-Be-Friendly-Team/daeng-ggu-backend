package com.ureca.profile.infrastructure;

import com.ureca.profile.domain.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerId(Long customerId);

    Optional<Customer> findByEmailAndCustomerLoginId(String email, String customerLoginId);

    // email로만 찾기
    Optional<Customer> findByEmail(String email);

    // Test CNT
    long count();

    @Query("SELECT MAX(c.customerId) FROM Customer c")
    Long findMaxCustomerId();
}
