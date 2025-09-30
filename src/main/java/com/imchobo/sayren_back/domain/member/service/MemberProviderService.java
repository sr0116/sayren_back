package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.SocialDisconnectDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialResponseDTO;
import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.recode.MemberSocialList;

public interface MemberProviderService {
  SocialResponseDTO getSocialResponseDTO(Provider provider);
  MemberSocialList getMemberSocialList();
  void disconnect(SocialDisconnectDTO socialDisconnectDTO);
  void deleteMemberProvider(Long memberId);
}
