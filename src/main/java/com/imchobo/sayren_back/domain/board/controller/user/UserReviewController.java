package com.imchobo.sayren_back.domain.board.controller.user;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/reviews")
@RequiredArgsConstructor
public class UserReviewController {

  private final ReviewService reviewService;

  // 등록
  @PostMapping
  public ResponseEntity<?> register(@RequestBody ReviewCreateRequestDTO dto) {
    return ResponseEntity.ok(reviewService.register(dto));
  }

  // 수정
  @PutMapping("/{id}")
  public ResponseEntity<?> modify(@PathVariable Long id, @RequestBody ReviewModifyRequestDTO dto) {
    reviewService.modify(id, dto);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  // 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    reviewService.delete(id);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  // 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<?> read(@PathVariable Long id) {
    return ResponseEntity.ok(reviewService.read(id));
  }

  // 목록 조회
  @GetMapping
  public ResponseEntity<?> list() {
    return ResponseEntity.ok(reviewService.list());
  }

  @GetMapping("/list")
  public ResponseEntity<?> getList(PageRequestDTO requestDTO) {
    return ResponseEntity.ok(reviewService.getList(requestDTO));
  }
}

