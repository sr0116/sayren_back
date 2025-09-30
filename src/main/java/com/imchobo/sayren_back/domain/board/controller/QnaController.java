package com.imchobo.sayren_back.domain.board.controller;

import com.imchobo.sayren_back.domain.board.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/qna")
@RequiredArgsConstructor
public class QnaController {

  private final QnaService qnaService;

  @PostMapping
  public ResponseEntity<Long> register(@RequestBody QnaCreateRequestDTO dto) {
    return ResponseEntity.ok(qnaService.register(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody QnaModifyRequestDTO dto) {
    qnaService.modify(id, dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    qnaService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<QnaDetailsResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(qnaService.read(id));
  }

  @GetMapping
  public ResponseEntity<List<QnaListResponseDTO>> list() {
    return ResponseEntity.ok(qnaService.list());
  }

  // 페이징
  @GetMapping("/list")
  public ResponseEntity<PageResponseDTO<QnaListResponseDTO>> getList(PageRequestDTO requestDTO) {
    return ResponseEntity.ok(qnaService.getList(requestDTO));
  }
}
