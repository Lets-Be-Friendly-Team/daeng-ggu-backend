package com.ureca.monitoring.domain;

import com.ureca.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guardian")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guardian extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guardianId;

    @Column(nullable = false, length = 50)
    private String guardianName;

    @Column(nullable = false, length = 100)
    private String vehicle;

    @Column(nullable = false, length = 20)
    private String phone;
}
