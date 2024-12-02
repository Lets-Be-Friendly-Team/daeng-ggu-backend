package com.ureca.request.domain;

import com.ureca.common.entity.BaseEntity;
import com.ureca.profile.domain.Designer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "request_alarm")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RequestAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estimate_img_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request; // Review와의 연관 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designer_id")
    private Designer designer;
}
