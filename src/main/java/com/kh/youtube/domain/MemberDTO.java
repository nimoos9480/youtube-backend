package com.kh.youtube.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    // 정보만 옮기기 위해 생성
    private String token;
    private String id;
    private String password;
    private String name;
}
