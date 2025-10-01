package com.imchobo.sayren_back.domain.board.controller;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeCreateRequestDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeDetailsResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeListResponseDTO;
import com.imchobo.sayren_back.domain.board.dto.notice.NoticeModifyRequestDTO;
import com.imchobo.sayren_back.domain.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;

  @PostMapping
  public ResponseEntity<Long> register(@RequestBody NoticeCreateRequestDTO dto) {
    return ResponseEntity.ok(noticeService.register(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody NoticeModifyRequestDTO dto) {
    noticeService.modify(id, dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    noticeService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<NoticeDetailsResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(noticeService.read(id));
  }

  @GetMapping
  public ResponseEntity<List<NoticeListResponseDTO>> list() {
    return ResponseEntity.ok(noticeService.list());
  }

  // 페이징
  @GetMapping("/list")
  public ResponseEntity<PageResponseDTO<NoticeListResponseDTO, Board>> getList(PageRequestDTO requestDTO) {
    return ResponseEntity.ok(noticeService.getList(requestDTO));
  }
}
