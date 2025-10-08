package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.member.dto.SocialDisconnectDTO;
import com.imchobo.sayren_back.domain.member.dto.SocialResponseDTO;
import com.imchobo.sayren_back.domain.member.dto.admin.AdminDisconnectProviderDTO;
import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import com.imchobo.sayren_back.domain.member.exception.SocialDisconnectException;
import com.imchobo.sayren_back.domain.member.mapper.MemberProviderMapper;
import com.imchobo.sayren_back.domain.member.recode.MemberSocialList;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberProviderServiceImpl implements MemberProviderService {
  private final MemberProviderMapper memberProviderMapper;
  private final MemberProviderRepository memberProviderRepository;
  private final MemberRepository memberRepository;

  private MemberProvider getMemberProvider(Member member, Provider provider) {
    return memberProviderRepository.findByMemberAndProvider(member, provider).orElse(null);
  }

  @Override
  public SocialResponseDTO getSocialResponseDTO(Provider provider) {
    return memberProviderMapper.toDTO(getMemberProvider(SecurityUtil.getMemberEntity(), provider));
  }

  @Override
  public MemberSocialList getMemberSocialList() {
    SocialResponseDTO google = getSocialResponseDTO(Provider.GOOGLE);
    SocialResponseDTO naver = getSocialResponseDTO(Provider.NAVER);
    SocialResponseDTO kakao = getSocialResponseDTO(Provider.KAKAO);

    return new MemberSocialList(google, naver, kakao);
  }

  // 유저용
  @Override
  public void disconnect(SocialDisconnectDTO  socialDisconnectDTO) {
    disconnect(socialDisconnectDTO.getProvider(), SecurityUtil.getMemberAuthDTO().getId());
  }

  // 어드민용
  @Override
  public void disconnect(AdminDisconnectProviderDTO adminDisconnectProviderDTO) {
    disconnect(adminDisconnectProviderDTO.getProvider(), adminDisconnectProviderDTO.getMemberId());
  }

  // 구현체
  public void disconnect(Provider provider, Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
    List<MemberProvider> memberProviderList = memberProviderRepository.findByMember(member);
    if((member.getPassword() == null || member.getPassword().isEmpty()) && memberProviderList.size() == 1){
      throw new SocialDisconnectException();
    }
    memberProviderList.forEach((mp) -> {
      if(mp.getProvider().equals(provider)){
        memberProviderRepository.delete(mp);
      }
    });
  }

  // 멤버 삭제시 같이 삭제
  @Override
  public void deleteMemberProvider(Long memberId) {
    memberProviderRepository.deleteByMember_Id(memberId);
  }
}
