package com.imchobo.sayren_back.security.service;

import com.imchobo.sayren_back.domain.common.util.RedisUtil;
import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import com.imchobo.sayren_back.domain.member.exception.EmailNotFoundException;
import com.imchobo.sayren_back.domain.member.exception.SocialLinkException;
import com.imchobo.sayren_back.domain.member.exception.SocialSignupException;
import com.imchobo.sayren_back.domain.member.mapper.MemberMapper;
import com.imchobo.sayren_back.domain.member.recode.SocialUser;
import com.imchobo.sayren_back.domain.member.repository.MemberProviderRepository;
import com.imchobo.sayren_back.domain.member.repository.MemberRepository;
import com.imchobo.sayren_back.domain.member.service.AuthService;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import com.imchobo.sayren_back.security.util.SocialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
  private final MemberRepository memberRepository;
  private final MemberProviderRepository memberProviderRepository;
  private final MemberMapper memberMapper;
  private final SocialUtil socialUtil;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

    Provider provider = Provider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
    SocialUser socialUser = socialUtil.getSocialUser(provider, oAuth2User);

    // 1. provider + providerUid 로 먼저 조회
    Optional<MemberProvider> providerOpt = memberProviderRepository.findByProviderAndProviderUid(socialUser.provider(), socialUser.providerUid());
    if (providerOpt.isPresent()) {
      Member member = providerOpt.get().getMember();
      return memberMapper.toAuthDTO(member);
    }

    // 2. 연동 안 되어있으면 이메일로 기존 멤버 조회
    Optional<Member> originMember = memberRepository.findByEmail(socialUser.email());
    if (originMember.isPresent()) {
      // 기존 멤버는 있는데 provider 연동 안 된 상태 → 연동 필요
      throw new SocialLinkException(socialUser);
    } else {
      // 기존 멤버도 없음 → 소셜 신규 가입 필요
      throw new SocialSignupException(socialUser);
    }
  }


}
