package com.kh.youtube.controller;

import com.kh.youtube.domain.*;
import com.kh.youtube.service.CommentLikeService;
import com.kh.youtube.service.VideoCommentService;
import com.kh.youtube.service.VideoLikeService;
import com.kh.youtube.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.events.Comment;
import java.util.List;

@RestController
@RequestMapping("/api/*")
public class VideoController {
    
    @Autowired
    private VideoService video;

    @Autowired
    private VideoCommentService comment;

    @Autowired
    private VideoLikeService vLike;

    @Autowired
    private CommentLikeService cLike;
    
    // 영상 전체 조회 : GET - http://localhost:8080/api/video
    @GetMapping("/video")
    public ResponseEntity<List<Video>> videoList() {
        return ResponseEntity.status(HttpStatus.OK).body(video.showAll());
    }
    // 영상 추가 : POST - http://localhost:8080/api/video
    @PostMapping("/video")
    public ResponseEntity<Video> createVideo(@RequestBody Video vo) {
        return ResponseEntity.status(HttpStatus.OK).body(video.create(vo));
    }
    
    // 영상 수정 : PUT - http://localhost:8080/api/video
    @PutMapping("/video")
    public ResponseEntity<Video> updateVideo(@RequestBody Video vo) {
        return ResponseEntity.status(HttpStatus.OK).body(video.update(vo));
    }
    
    // 영상 삭제 : DELETE - http://localhost:8080/api/video/1
    @DeleteMapping("/video/{id}")
    public ResponseEntity<Video> deleteVideo(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(video.delete(id));
    }
    
    // 영상 1개 조회 : GET - http://localhost:8080/api/video/1
    @GetMapping("/video/{id}")
    public ResponseEntity<Video> showVideo(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(video.show(id));
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
