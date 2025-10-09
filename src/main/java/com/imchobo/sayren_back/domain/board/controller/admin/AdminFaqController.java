package com.imchobo.sayren_back.domain.board.controller.admin;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.service.admin.AdminFaqService;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.faq.FaqModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/faqs")
@RequiredArgsConstructor
public class AdminFaqController {

  private final AdminFaqService adminFaqService;

  @PostMapping
  public ResponseEntity<Long> register(@RequestBody FaqCreateRequestDTO dto) {
    return ResponseEntity.ok(adminFaqService.register(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody FaqModifyRequestDTO dto) {
    adminFaqService.modify(id, dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    adminFaqService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<FaqDetailsResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(adminFaqService.read(id));
  }

  // 전체목록 조회(페이징 전)
  @GetMapping
  public ResponseEntity<List<FaqListResponseDTO>> allFaqs() {
    return ResponseEntity.ok(adminFaqService.list());
  }

  // 페이징 처리 전체목록 조회
  @GetMapping("/list")
  public ResponseEntity<PageResponseDTO<FaqListResponseDTO, Board>> list(PageRequestDTO requestDTO) {
    return ResponseEntity.ok(adminFaqService.getList(requestDTO));
  }
}
