package com.kh.youtube.service;

import com.kh.youtube.domain.Channel;
import com.kh.youtube.domain.Member;
import com.kh.youtube.repo.ChannelDAO;
import com.kh.youtube.repo.MemberDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j // 롬복에서 제공하는 로그 찍는 것
public class MemberService {

    @Autowired
    private MemberDAO dao;  // service에 dao 주입

    public List<Member> showAll() {
        return dao.findAll(); // SELECT * FROM MEMBER
    }

    public Member show(String id) {
        return dao.findById(id).orElse(null);  // SELECT * FROM MEMBER WHERE id=?
    }

    public Member create(Member member) {
        log.info("member : " + member);
        return dao.save(member);  // INSERT INTO MEMBER(ID, PASSWORD, NAME, AUTHORITY) VALUES(?, ?, ?, 'ROLE_USER')
    }

    public Member update(Member member) {
        Member target = dao.findById(member.getId()).orElse(null);
        if(target != null) {
            return dao.save(member);  // UPDATE MEMBER SET ID=? PASSWORD=?, NAME=?, AUTHORITY=? WHERE ID=?
        }
        return null;
    }

    public Member delete(String id) {
        Member target = dao.findById(id).orElse(null);
        dao.delete(target);  // 리턴타입이 void라서 member값을 가져와야 함
        return target;    // DELETE FROM MEMBER WHERE ID=?


    }


}
