package com.imchobo.sayren_back.security.util;

import com.imchobo.sayren_back.domain.member.en.Provider;
import com.imchobo.sayren_back.domain.member.recode.SocialUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SocialUtil {
  public SocialUser getSocialUser(Provider provider, OAuth2User oAuth2User) {
    SocialUser socialUser = null;
    switch(provider) {
      case GOOGLE -> socialUser = getGoogleUser(provider, oAuth2User);
      case NAVER ->  socialUser = getNaverUser(provider, oAuth2User);
      case KAKAO ->  socialUser = getKakaoUser(provider, oAuth2User);
    }
    return socialUser;
  }

  private SocialUser getGoogleUser(Provider provider, OAuth2User oAuth2User) {
    return new SocialUser(
            provider,
            oAuth2User.getAttribute("sub"),
            oAuth2User.getAttribute("email"),
            oAuth2User.getAttribute("name")
    );
  }

  private SocialUser getNaverUser(Provider provider, OAuth2User oAuth2User) {
    Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");

    return new SocialUser(
            provider,
            (String) response.get("id"),
            (String) response.get("email"),
            (String) response.get("name")
    );
  }

  private SocialUser getKakaoUser(Provider provider, OAuth2User oAuth2User) {
    Map<String, Object> attributes = oAuth2User.getAttributes();

    // id → providerUid
    String providerUid = String.valueOf(attributes.get("id"));

    // nickname → kakao_account.profile.nickname 기준 (properties.nickname도 가능)
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    String nickname = (String) profile.get("nickname");
    String email = (String) kakaoAccount.get("email");


    return new SocialUser(provider, providerUid, email, nickname);
  }
}
