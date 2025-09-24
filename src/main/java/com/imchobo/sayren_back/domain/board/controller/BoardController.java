package com.imchobo.sayren_back.domain.board.controller;

import com.imchobo.sayren_back.domain.board.dto.BoardRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.BoardResponseDTO;
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

  // 게시글 등록
  @PostMapping
  public ResponseEntity<BoardResponseDTO> register(@RequestBody BoardRequestDTO dto) {
    Long id = boardService.register(dto);
    BoardResponseDTO response = boardService.read(id);
    return ResponseEntity.ok(response);
  }

  // 게시글 수정 (BoardRequestDTO 재사용)
  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody BoardRequestDTO dto) {
    boardService.modify(id, dto);  // 서비스에서 id와 dto 같이 처리
    return ResponseEntity.ok().build();
  }

  // 게시글 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boardService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // 게시글 목록 조회 (BoardResponseDTO 재사용)
  @GetMapping
  public ResponseEntity<List<BoardResponseDTO>> list() {
    return ResponseEntity.ok(boardService.list());
  }

  // 게시글 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<BoardResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(boardService.read(id));
  }
}
