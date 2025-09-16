package com.imchobo.sayren_back.security.service;

import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.en.Role;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import com.imchobo.sayren_back.domain.member.exception.SocialLinkException;
import com.imchobo.sayren_back.domain.member.exception.SocialSignupException;
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

    // 1. provider + providerUid 로 먼저 조회
    Optional<MemberProvider> providerOpt = memberProviderRepository.findByProviderAndProviderUid(provider, providerUid);
    if (providerOpt.isPresent()) {
      Member member = providerOpt.get().getMember();
      return memberMapper.toAuthDTO(member);
    }

    // 2. 연동 안 되어있으면 이메일로 기존 멤버 조회
    Optional<Member> originMember = memberRepository.findByEmail(email);
    if (originMember.isPresent()) {
      // 기존 멤버는 있는데 provider 연동 안 된 상태 → 연동 필요
      throw new SocialLinkException(oAuth2User.getAttributes(), provider);
    } else {
      // 기존 멤버도 없음 → 소셜 신규 가입 필요
      throw new SocialSignupException(oAuth2User.getAttributes(), provider);
    }
  }
}
