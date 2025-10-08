package com.imchobo.sayren_back.domain.member.controller.admin;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.*;
import com.imchobo.sayren_back.domain.member.service.Member2FAService;
import com.imchobo.sayren_back.domain.member.service.MemberLoginHistoryService;
import com.imchobo.sayren_back.domain.member.service.MemberProviderService;
import com.imchobo.sayren_back.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/member")
@Log4j2
public class AdminMemberController {
  private final MemberService memberService;
  private final MemberLoginHistoryService memberLoginHistoryService;
  private final MemberProviderService memberProviderService;
  private final Member2FAService member2FAService;


  @GetMapping("get-list")
  public ResponseEntity<?> getMemberList(PageRequestDTO pageRequestDTO) {
    log.info(pageRequestDTO);
    return ResponseEntity.ok(memberService.getMemberList(pageRequestDTO));
  }

  @GetMapping("get-deletelist")
  public ResponseEntity<?> getDeleteMemberList(PageRequestDTO pageRequestDTO) {
    log.info(pageRequestDTO);
    return ResponseEntity.ok(memberService.getDeleteMemberList(pageRequestDTO));
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

  @PatchMapping("modify-name")
  public ResponseEntity<?> changeName(@RequestBody @Valid AdminChangeNameDTO adminChangeNameDTO){
    memberService.changeName(adminChangeNameDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @PatchMapping("modify-tel")
  public ResponseEntity<?> changeTel(@RequestBody @Valid AdminChangeTelDTO adminChangeTelDTO){
    memberService.modifyTel(adminChangeTelDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @PatchMapping("modify-role")
  public ResponseEntity<?> changeRole(@RequestBody @Valid AdminSelectMemberIdDTO adminSelectMemberIdDTO){
    memberService.changeRole(adminSelectMemberIdDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @DeleteMapping("delete-provider")
  public ResponseEntity<?> deleteProvider(@RequestBody @Valid AdminDisconnectProviderDTO adminDisconnectProviderDTO) {
    memberProviderService.disconnect(adminDisconnectProviderDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @DeleteMapping("delete-2fa")
  public ResponseEntity<?> delete2fa(@RequestBody @Valid AdminSelectMemberIdDTO adminSelectMemberIdDTO) {
    member2FAService.delete(adminSelectMemberIdDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }

  @PatchMapping("modify-status")
  public ResponseEntity<?> changeMemberStatus(@RequestBody @Valid AdminChangeMemberStatusDTO adminChangeMemberStatusDTO) {
    memberService.changeStatus(adminChangeMemberStatusDTO);
    return ResponseEntity.ok(Map.of("message", "success"));
  }
}
