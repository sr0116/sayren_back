package com.imchobo.sayren_back.domain.board.controller;

import com.imchobo.sayren_back.domain.board.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.review.ReviewModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  // 등록
  @PostMapping
  public ResponseEntity<Long> register(@RequestBody ReviewCreateRequestDTO dto) {
    Long id = reviewService.register(dto);
    return ResponseEntity.ok(id);
  }

  // 수정
  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody ReviewModifyRequestDTO dto) {
    reviewService.modify(id, dto);
    return ResponseEntity.noContent().build();
  }

  // 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    reviewService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<ReviewDetailsResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(reviewService.read(id));
  }

  // 목록 조회
  @GetMapping
  public ResponseEntity<List<ReviewListResponseDTO>> list() {
    return ResponseEntity.ok(reviewService.list());
  }

  @GetMapping("/list")
  public ResponseEntity<PageResponseDTO<ReviewListResponseDTO>> getList(PageRequestDTO requestDTO) {
    return ResponseEntity.ok(reviewService.getList(requestDTO));
  }
}

