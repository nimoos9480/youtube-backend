package com.kh.youtube.controller;

import com.kh.youtube.domain.Channel;
import com.kh.youtube.domain.Member;
import com.kh.youtube.domain.MemberDTO;
import com.kh.youtube.security.TokenProvider;
import com.kh.youtube.service.ChannelService;
import com.kh.youtube.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
public class MemberController {

    @Autowired
    private MemberService service;

    @Autowired
    private TokenProvider tokenProvider;


    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity register(@RequestBody MemberDTO dto) {
        // 비밀번호 -> 암호화 처리 + 저장할 유저 만들기
        Member member = Member.builder()
                                .id(dto.getId())
                                .password(passwordEncoder.encode(dto.getPassword())) // 비밀번호 암호화 처리해서 가져오기
                                .name(dto.getName())
                                .build();

        // 서비스를 이용해 레포지토리에 유저 저장
        Member registermember = service.create(member);

        // 비밀번호 노출 막기 위함
        MemberDTO responseDTO = dto.builder()
                                        .id(registermember.getId())
                                        .name(registermember.getName())
                                        .build();

        return ResponseEntity.ok().body(responseDTO);
    }

    // 로그인 -> token
    @PostMapping("/signin")
    public ResponseEntity authenticate(@RequestBody MemberDTO dto) {
        Member member = service.getByCredentials(dto.getId(), dto.getPassword(), passwordEncoder);
        if(member != null) { // -> 토큰 생성
             String token = tokenProvider.create(member);
             MemberDTO responseDTO = MemberDTO.builder()
                     .id(member.getId())
                     .name(member.getName())
                     .token(token) // (tokenProvider.create(member)을 의미)
                     .build();
             return ResponseEntity.ok().body(responseDTO);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}

//@Slf4j
//@RestController
//@RequestMapping("/api/*")
//public class MemberController {

//    @Autowired
//    private MemberService service;   // controller에 service 주입
//
//    @Autowired
//    private ChannelService channelService;
//
//    @GetMapping("/user")
//    public ResponseEntity<List<Member>> showAll() {
//        return ResponseEntity.status(HttpStatus.OK).body(service.showAll());
//    }
//
//    @GetMapping("/user/{id}")
//    public ResponseEntity<Member> show(@PathVariable String id) {
//        return ResponseEntity.status(HttpStatus.OK).body(service.show(id));
//    }
//
//    @PostMapping("/user")
//    public ResponseEntity<Member> create(@RequestBody Member member) {
//        return ResponseEntity.status(HttpStatus.OK).body(service.create(member));
//    }
//
//    @PutMapping("/user")
//    public ResponseEntity<Member> update(@RequestBody Member member) {
//        Member result = service.update(member);
//        if(result != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(result);
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  // build 는 null 보내는 것과 동일
//    }
//
//    @DeleteMapping("/user/{id}")
//    public ResponseEntity<Member> delete(@PathVariable String id) {
//        log.info(id + "삭제~");
//        return ResponseEntity.status(HttpStatus.OK).body(service.delete(id));
//    }
//
//    // SELECT * FROM channel WHERE id=?
//    // http://localhost:8080/user/channel?id=user1
//    @GetMapping("/user/channel")
//    public ResponseEntity<List<Channel>> showMember(@RequestParam String id) {
//            return ResponseEntity.status(HttpStatus.OK).body(channelService.showMember(id));
//
//    }


//}
