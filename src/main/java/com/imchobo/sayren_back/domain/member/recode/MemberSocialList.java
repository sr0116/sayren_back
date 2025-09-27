package com.imchobo.sayren_back.domain.member.recode;

import com.imchobo.sayren_back.domain.member.dto.SocialResponseDTO;

public record MemberSocialList(SocialResponseDTO google, SocialResponseDTO naver, SocialResponseDTO kakao) {
}
