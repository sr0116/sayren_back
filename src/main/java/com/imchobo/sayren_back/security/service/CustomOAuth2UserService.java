package com.imchobo.sayren_back.security.service;

import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.en.Role;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
  private final MemberRepository memberRepository;
  private final MemberProviderRepository memberProviderRepository;
  private final MemberMapper memberMapper;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

    Provider provider = Provider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
    String email = oAuth2User.getAttribute("email");
    String providerUid = oAuth2User.getName();
    Member member;

    Optional<MemberProvider> providerOpt = memberProviderRepository.findByProviderAndProviderUid(provider, providerUid);
    // 이미 연동된 계정이면 해당 멤버로 반환
    if (providerOpt.isPresent()) {

      member = providerOpt.get().getMember();

    }
    // 연동되지않은 계정이면
    else {
      // 기존 멤버에서 이메일로 찾음
      Optional<Member> originMember = memberRepository.findByEmail(email);

      // 기존멤버 테이블 이메일과 일치하는게 있을 때
      if (originMember.isPresent()) {
        throw new OAuth2AuthenticationException("연동 여부 묻기");
      }
      // 기존 멤버 테이블에 이메일이 없을때 멤버 생성
      else {
        member = Member.builder()
                .email(email)
                .name(oAuth2User.getAttribute("name"))
                .roles(Set.of(Role.USER))
                .status(MemberStatus.READY)
                .build();
        memberRepository.save(member);

        memberProviderRepository.save(MemberProvider.builder().member(member).providerUid(oAuth2User.getName()).provider(provider).build());
      }
    }

    return memberMapper.toAuthDTO(member);
  }
}
