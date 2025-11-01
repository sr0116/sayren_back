package com.imchobo.sayren_back.domain.board.controller.user;

import com.imchobo.sayren_back.domain.board.dto.qna.QnaCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.qna.QnaModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.service.QnaService;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/qna")
@RequiredArgsConstructor
public class UserQnaController {

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
  public ResponseEntity<PageResponseDTO<QnaListResponseDTO, Board>> getList(PageRequestDTO requestDTO) {
    return ResponseEntity.ok(qnaService.getList(requestDTO));
  }
}
