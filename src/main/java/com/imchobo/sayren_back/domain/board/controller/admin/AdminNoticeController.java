package com.imchobo.sayren_back.domain.board.controller.admin;

import com.imchobo.sayren_back.domain.board.entity.Board;
import com.imchobo.sayren_back.domain.board.service.admin.AdminNoticeService;
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
public class AdminNoticeController {

  private final AdminNoticeService adminNoticeService;

  @PostMapping
  public ResponseEntity<Long> register(@RequestBody NoticeCreateRequestDTO dto) {
    return ResponseEntity.ok(adminNoticeService.register(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> modify(@PathVariable Long id, @RequestBody NoticeModifyRequestDTO dto) {
    adminNoticeService.modify(id, dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    adminNoticeService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<NoticeDetailsResponseDTO> read(@PathVariable Long id) {
    return ResponseEntity.ok(adminNoticeService.read(id));
  }

  @GetMapping
  public ResponseEntity<List<NoticeListResponseDTO>> list() {
    return ResponseEntity.ok(adminNoticeService.list());
  }

  // 페이징
  @GetMapping("/list")
  public ResponseEntity<PageResponseDTO<NoticeListResponseDTO, Board>> getList(PageRequestDTO requestDTO) {
    return ResponseEntity.ok(adminNoticeService.getList(requestDTO));
  }
}
