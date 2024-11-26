package com.ureca.profile.domain;

import jakarta.persistence.*;
import lombok.*;

// 즐겨찾기
@Entity
@Table(name = "bookmark")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"customer", "designer"})
public class Bookmark {

    // 즐겨찾기 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    // 보호자 아이디
    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    // 디자이너 아이디
    @ManyToOne
    @JoinColumn(name = "designerId", nullable = false)
    private Designer designer;

    @Builder
    public Bookmark(Long bookmarkId, Customer customer, Designer designer) {
        this.bookmarkId = bookmarkId;
        this.customer = customer;
        this.designer = designer;
    }
}
