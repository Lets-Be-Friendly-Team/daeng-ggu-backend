package com.ureca.request.infrastructure;

import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Pet;
import com.ureca.request.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT COUNT(r) > 0 FROM Request r WHERE r.pet = :pet AND r.request_status = :status")
    boolean existsByPetAndRequestStatus(@Param("pet") Pet pet, @Param("status") String status);

    List<Request> findAllByCustomer(Customer customer);

    @Query("SELECT r FROM Request r WHERE r.pet = :pet AND r.request_status = :request_status")
    Request findByPetAndRequest_status(@Param("pet") Pet pet, @Param("request_status") String request_status);


}
