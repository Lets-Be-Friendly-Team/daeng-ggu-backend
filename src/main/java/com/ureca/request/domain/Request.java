package com.ureca.request.domain;

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
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petId", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(length = 20, nullable = false)
    private String desiredServiceCode;

    @Column(length = 20, nullable = false)
    private String lastGroomingDate;

    @Column(nullable = false)
    private LocalDateTime desiredDate1;

    @Column private LocalDateTime desiredDate2;

    @Column private LocalDateTime desiredDate3;

    @Column(length = 50, nullable = false)
    private String desiredRegion;

    @Column(nullable = false)
    private Boolean isDelivery;

    @Column(nullable = false)
    private Boolean isMonitoringIncluded;

    @Column(length = 100)
    private String additionalRequest;

    @Column(length = 20, nullable = false)
    private String requestStatus;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
