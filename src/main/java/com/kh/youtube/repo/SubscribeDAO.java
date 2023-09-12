package com.kh.youtube.repo;

import com.kh.youtube.domain.Category;
import com.kh.youtube.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscribeDAO extends JpaRepository<Subscribe, Integer> {
    
    // 내가 구독한 채널 조회
    // SELECT * FROM subscribe WHERE id = ?
    @Query(value = "SELECT * FROM subscribe WHERE id = :id", nativeQuery = true)
    List<Subscribe> findByMemberId(String id); // id값 쿼리문의 ?자리에 넣어주기  ==> service로 가서 관련 메소드 추가해주기
}
