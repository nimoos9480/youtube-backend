package com.kh.youtube.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
@Entity
@Data
@Table(name = "COMMENT_LIKE")  // 혹시 오류날 때 테이블명 명시해서 일치시켜주는 것
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class CommentLike {

    @Id
    @Column(name = "comm_like_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "commLikeSequence")
    @SequenceGenerator(name = "commLikeSequence", sequenceName = "SEQ_COMMENT_LIKE", allocationSize = 1)
    private int commLikeCode;

    @Column(name = "comm_like_date")
    private Date commLikeDate;

    @ManyToOne
    @JoinColumn(name = "comment_code")
    private VideoComment comment;

    @ManyToOne
    @JoinColumn(name = "id")
    private Member member;
}
