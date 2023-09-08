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
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert // default값이 있을 때 사용
@DynamicUpdate  
public class Channel {

    @Id
    @Column(name="channel_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "channelSequence")
    @SequenceGenerator(name = "channelSequence", sequenceName = "SEQ_CHANNEL", allocationSize = 1)  // allocationSize = 1은 각 값이 1씩 증가하는 시퀀스를 생성
    private int channelCode;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_photo")
    private String channelPhoto;

    @Column(name = "channel_desc")
    private String channelDesc;

    @Column(name = "channel_date")
    private Date channelDate;

    @ManyToOne  // Channel 엔티티와 Member 엔티티를 다대일 관계로 설정 (한 멤버가 여러 채널을 가지고 있음)
    @JoinColumn(name = "id") // 외래키 생성 or Member 엔티티의 기본키와 매핑
    private Member member;
}
