package com.imchobo.sayren_back.domain.board.controller;

import com.imchobo.sayren_back.domain.board.dto.BoardCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
  private final BoardService boardService;

  @PostMapping
  public ResponseEntity<BoardDetailsResponseDTO> register(@RequestBody BoardCreateRequestDTO dto) {
    Long id = boardService.register(dto);
    BoardDetailsResponseDTO response = boardService.read(id);
    return ResponseEntity.ok(response);
  }

  // 게시글 수정
  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody BoardModifyRequestDTO dto) {
    dto.setId(id); // path 변수랑 dto 묶어주기
    boardService.modify(dto);
    return ResponseEntity.ok().build();
  }

  // 게시글 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boardService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // 게시글 목록 조회
  @GetMapping
  public ResponseEntity<List<BoardListResponseDTO>> list() {
    return ResponseEntity.ok(boardService.list());
  }

  // 게시글 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<BoardDetailsResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(boardService.read(id));
  }

//  // 카테고리 필터(enum 카테고리)
//  @GetMapping
//  public ResponseEntity<List<BoardListResponseDTO>> list(
//          @RequestParam(required = false) String category) {
//    if (category != null) {
//      return ResponseEntity.ok(boardService.listByCategory(category));
//    }
//    return ResponseEntity.ok(boardService.list());
//  }

}
