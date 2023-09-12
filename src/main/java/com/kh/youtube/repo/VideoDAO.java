package com.kh.youtube.repo;

import com.kh.youtube.domain.Category;
import com.kh.youtube.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoDAO extends JpaRepository<Video, Integer> {

    // 채널별 목록보기
    @Query(value = "SELECT * FROM video WHERE channel_code = :code", nativeQuery = true)
    List<Video> findByChannelCode(int code); // 이곳의 code가 쿼리문 안의 '?' 자리로 들어간다  ==> service로 메서드 추가하러 가기
}
