package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.dto.PageRequestDTO;
import com.imchobo.sayren_back.domain.common.dto.PageResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.*;
import com.imchobo.sayren_back.domain.member.entity.Member;

import java.util.Map;

public interface MemberService {
  void register(MemberSignupDTO memberSignupDTO);
  Member findByEmail(String email);
  Member findById(Long id);
  void emailVerify(String token);
  void modifyTel(MemberTelDTO memberTelDTO);
  void sendTel(String newTel);
  FindEmailResponseDTO findEmail(MemberTelDTO memberTelDTO);
  void telVerify(MemberTelDTO memberTelDTO);
  Map<?, ?> getTel();
  void findPassword(FindPasswordRequestDTO findPasswordRequestDTO);
  void changePassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
  void checkMail(EmailVerifyRequestDTO emailVerifyRequestDTO);
  String signupNext(String token);
  MemberLoginResponseDTO changeName(ChangeNameDTO changeNameDTO);
  void passwordCheck(PasswordCheckDTO passwordCheckDTO);
  void deleteMember();
  boolean hasPassword();
  void changeRole(Long memberId);
  PageResponseDTO<MemberListResponseDTO, Member> getMemberList(PageRequestDTO pageRequestDTO);
}
