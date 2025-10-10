package com.imchobo.sayren_back.domain.board.controller.admin;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.service.admin.AdminQnaService;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
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
@RequestMapping("/api/admin/qna")
@RequiredArgsConstructor
public class AdminQnaController {

  private final AdminQnaService adminQnaService;

  @PostMapping
  public ResponseEntity<Long> register(@RequestBody QnaCreateRequestDTO dto) {
    return ResponseEntity.ok(adminQnaService.register(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody QnaModifyRequestDTO dto) {
    adminQnaService.modify(id, dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    adminQnaService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<QnaDetailsResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(adminQnaService.read(id));
  }

  @GetMapping
  public ResponseEntity<List<QnaListResponseDTO>> list() {
    return ResponseEntity.ok(adminQnaService.list());
  }

  // 페이징
  @GetMapping("/list")
  public ResponseEntity<PageResponseDTO<QnaListResponseDTO, Board>> getList(PageRequestDTO requestDTO) {
    return ResponseEntity.ok(adminQnaService.getList(requestDTO));
  }
}
