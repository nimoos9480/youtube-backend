package com.kh.youtube.controller;

import com.kh.youtube.domain.*;
import com.kh.youtube.service.CommentLikeService;
import com.kh.youtube.service.VideoCommentService;
import com.kh.youtube.service.VideoLikeService;
import com.kh.youtube.service.VideoService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.events.Comment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/*")
@Log4j2 // 로그 찍는 거
@CrossOrigin(origins={"*"}, maxAge = 6000) // 리액트 연결 목적
public class VideoController {

    @Value("${youtube.upload.path}")  // application.properties에 있는 변수
    private String uploadPath;

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoCommentService comment;

    @Autowired
    private VideoLikeService vLike;

    @Autowired
    private CommentLikeService cLike;
    
    // 영상 전체 조회 : GET - http://localhost:8080/api/video
    @GetMapping("/video")
    public ResponseEntity<List<Video>> videoList(@RequestParam(name="page", defaultValue = "1") int page, @RequestParam(name="category", required = false) Integer category) {
        // Integer category : Integer로 지정하면 null 값도 받을 수 있음

        // 정렬 관련
        Sort sort = Sort.by("videoCode").descending(); // videoCode기준 오름차순

        // 한 페이지에 10개씩 세팅
        Pageable pageable = PageRequest.of(page-1, 20, sort); // (몇번째페이지?0부터 시작, 한페이지에 몇개씩?)

        // 동적 쿼리를 위한 QueryDSL을 사용한 코드들 추가

        // 1. Q도메인 클래스를 가져와야 한다.
            // Q도메인 == build.gradle에서 만들어야 함
        QVideo qVideo = QVideo.video;

        // 2. BooleandBuilder는 where문에 들어가는 조건들을 넣어주는 컨테이너
        BooleanBuilder builder = new BooleanBuilder();

        if(category != null) {
            // 3. 원하는 조건은 필드값과 같이 결합해서 생성한다.
            BooleanExpression expression = qVideo.category.categoryCode.eq(category);
                                                        //포함은 contains / 일치는 eq
            // 4. 만들어진 조건은 where문에 and나 or 같은 키워드와 결합한다.
            builder.and(expression);
        }

        Page<Video> result = videoService.showAll(pageable, builder); // videoService 변수값 수정하러 가기

        log.info("Total Pages : " + result.getTotalPages()); // 총 몇 페이지인지
        log.info("Total Count : " + result.getTotalElements()); // 전체 개수
        log.info("Page Number : " + result.getNumber()); // 현재 페이지 번호
        log.info("Page Size : " + result.getSize()); // 페이지당 데이터 개수
        log.info("Next Page : " + result.hasNext()); // 다음 페이지가 있는지 존재 여부 확인
        log.info("First Page : " + result.isFirst()); // 시작페이지 여부

//        return ResponseEntity.status(HttpStatus.OK).build();
//        return ResponseEntity.status(HttpStatus.OK).body(videoService.showAll(pageable)); // 리턴타입이 달라져서 문제
        return ResponseEntity.status(HttpStatus.OK).body(result.getContent());
    }
    

    // 영상 추가 : POST - http://localhost:8080/api/video
    @PostMapping("/video")
    public ResponseEntity<Video> createVideo(@RequestParam(name="video") MultipartFile video, @RequestParam(name="image") MultipartFile image, String title, @RequestParam(name="desc", required = false) String desc, String categoryCode) {
        // @RequestParam(name="desc", required = false) String desc ==> 필수 값이 아닌 것들에 required = false(선택적(optional)이라는 뜻) 처리해주면 좋다
        // 가져와야 하는 값 == video_title, video_desc, video_url, video_photo, category_code
        log.info("video : " + video );
        log.info("image : " + image );
        log.info("title : " + title );
        log.info("desc : " + desc );
        log.info("categoryCode : " + categoryCode );

        // 업로드 처리
        // 비디오의 실제 파일 이름이 필요
        String originalVideo = video.getOriginalFilename();
        log.info("original : " + originalVideo);
        String realVideo = originalVideo.substring(originalVideo.lastIndexOf("\\")+1);
        log.info("real : " + realVideo);

        // 이미지의 실제 파일 이름
        String originalImage = image.getOriginalFilename();
        String realImage = originalImage.substring(originalImage.lastIndexOf("\\")+1);

        // UUID(파일명 무작위)
        String uuid = UUID.randomUUID().toString();

        // 실제로 저장할 파일명 (위치 포함)
        String saveVideo = uploadPath + File.separator + uuid + "_" + realVideo;
        String saveImage = uploadPath + File.separator + uuid + "_" + realImage;

        Path pathVideo = Paths.get(saveVideo); // 경로 설정
        Path pathImage = Paths.get(saveImage);
        try {
            video.transferTo(pathVideo);
            image.transferTo(pathImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

      //  video_title, video_desc, video_url, video_photo, category_code
        Video vo = new Video();
        vo.setVideoTitle(title);
        vo.setVideoDesc(desc);

        // 카테고리 조인
        Category category = new Category();
        category.setCategoryCode(Integer.parseInt(categoryCode)); // categoryCode가 int라서 바꿔주기
        vo.setCategory(category);

//        vo.setVideoUrl(saveVideo);
//        vo.setVideoPhoto(saveImage);
        // 경로 ==> 상대경로로 수정
        vo.setVideoUrl(uuid + "_" + realVideo);
        vo.setVideoPhoto(uuid + "_" + realImage);

        // 채널 조인
        Channel channel = new Channel();
        channel.setChannelCode(25);
        vo.setChannel(channel);
        
        // 멤버 조인
        Member member = new Member();
        member.setId("user1");
        vo.setMember(member);
//        return ResponseEntity.status(HttpStatus.OK).build();
        return ResponseEntity.status(HttpStatus.OK).body(videoService.create(vo));
    }


    // 영상 수정 : PUT - http://localhost:8080/api/video
    @PutMapping("/video")
    public ResponseEntity<Video> updateVideo(@RequestBody Video vo) {
        return ResponseEntity.status(HttpStatus.OK).body(videoService.update(vo));
    }
    
    // 영상 삭제 : DELETE - http://localhost:8080/api/video/1
    @DeleteMapping("/video/{id}")
    public ResponseEntity<Video> deleteVideo(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(videoService.delete(id));
    }
    
    // 영상 1개 조회 : GET - http://localhost:8080/api/video/1
    @GetMapping("/video/{id}")
    public ResponseEntity<Video> showVideo(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(videoService.show(id));
    }
    
    // 영상 1개에 따른 댓글 전체 조회 : GET : http://localhost:8080/api/video/1/comment
    @GetMapping("/video/{id}/comment")
    public ResponseEntity<List<VideoComment>> videoCommentList(@PathVariable int id) {
        // SELECT * FROM comment WHERE commentCode=?
        return ResponseEntity.status(HttpStatus.OK).body(comment.findByVideoCode(id));
    }


    // 좋아요 추가 : POST - http://localhost:8080/api/video/like
    @PostMapping("/video/like")
    public ResponseEntity<VideoLike> createVLike(@RequestBody VideoLike vo) {
        return ResponseEntity.status(HttpStatus.OK).body(vLike.create(vo));
    }
    
    // 좋아요 취소 : DELETE - http://localhost:8080/api/video/like/1
    @DeleteMapping("/video/like/{id}")
    public ResponseEntity<VideoLike> deleteVLike(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(vLike.delete(id));
    }

    // 댓글 추가 : POST - http://localhost:8080/api/video/comment
    @PostMapping("/video/comment")
    public ResponseEntity<VideoComment> createComment(@RequestBody VideoComment vo) {
        return ResponseEntity.status(HttpStatus.OK).body(comment.create(vo));
    }
    
    // 댓글 수정 : PUT - http://localhost:8080/api/video/comment
    @PutMapping("/video/comment")
    public ResponseEntity<VideoComment> updateComment(@RequestBody VideoComment vo) {
        return ResponseEntity.status(HttpStatus.OK).body(comment.update(vo));
    }
    
    // 댓글 삭제 : DELETE - http://localhost:8080/api/video/comment/1
    @PostMapping("/video/comment/{id}")
    public ResponseEntity<VideoComment> deleteComment(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(comment.delete(id));
    }
    
    // 댓글 좋아요 추가 : POST - http://localhost:8080/api/video/comment/like
    @PostMapping("/video/comment/like")
    public ResponseEntity<CommentLike> createCLike(@RequestBody CommentLike vo) {
        return ResponseEntity.status(HttpStatus.OK).body(cLike.create(vo));
    }
    // 댓글 좋아요 취소 : PUT - http://localhost:8080/api/video/comment/like/1
    @DeleteMapping("/video/comment/like/{id}")
    public ResponseEntity<CommentLike> deleteCLike(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(cLike.delete(id));
    }
    
}
