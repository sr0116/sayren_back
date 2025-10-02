package com.imchobo.sayren_back.domain.member.controller.admin;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.member.service.MemberLoginHistoryService;
import com.imchobo.sayren_back.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/member")
@Log4j2
public class AdminMemberController {
  private final MemberService memberService;
  private final MemberLoginHistoryService memberLoginHistoryService;


  @GetMapping("get-list")
  public ResponseEntity<?> getMemberList(PageRequestDTO pageRequestDTO) {
    log.info(pageRequestDTO);
    return ResponseEntity.ok(memberService.getMemberList(pageRequestDTO));
  }


  @GetMapping("get-info")
  public ResponseEntity<?> getMemberInfo(@RequestParam("memberId") Long memberId) {
    return ResponseEntity.ok(memberService.getMemberInfo(memberId));
  }

  @GetMapping("get-login")
  public ResponseEntity<?> getMemberLogin(@RequestParam("memberId") Long memberId, PageRequestDTO pageRequestDTO) {

    log.info(memberId);
    log.info(pageRequestDTO);
    return ResponseEntity.ok(memberLoginHistoryService.getLoginHistory(memberId, pageRequestDTO));
  }

}
