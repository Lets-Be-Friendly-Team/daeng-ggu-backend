package com.ureca.request.domain;

import com.ureca.common.entity.BaseEntity;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Pet;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "request")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long request_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(length = 20, nullable = false)
    private String desired_service_code;

    @Column(length = 20, nullable = false)
    private String last_grooming_date;

    @Column(nullable = false)
    private LocalDateTime desired_date1;

    @Column private LocalDateTime desired_date2;

    @Column private LocalDateTime desired_date3;

    @Column(length = 50, nullable = false)
    private String desired_region;

    @Column(nullable = false)
    private Boolean is_delivery;

    @Column(nullable = false)
    private Boolean is_monitoringIncluded;

    @Column(length = 100)
    private String additional_request;

    @Column(length = 20, nullable = false)
    private String request_status;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updated_at;
}
