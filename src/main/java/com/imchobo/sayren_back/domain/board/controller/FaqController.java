package com.imchobo.sayren_back.domain.board.controller;

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
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
public class FaqController {

  private final FaqService faqService;

  @PostMapping
  public ResponseEntity<Long> register(@RequestBody FaqCreateRequestDTO dto) {
    return ResponseEntity.ok(faqService.register(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody FaqModifyRequestDTO dto) {
    faqService.modify(id, dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    faqService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<FaqDetailsResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(faqService.read(id));
  }

  @GetMapping
  public ResponseEntity<List<FaqListResponseDTO>> list() {
    return ResponseEntity.ok(faqService.list());
  }
}
