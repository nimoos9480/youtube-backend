package com.kh.youtube.repo;

import com.kh.youtube.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDAO extends JpaRepository<Category, Integer> { // <entity 객체, 카테고리 클래스의 프라이머리키의 데이터타입=int>



}
