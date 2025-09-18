package com.imchobo.sayren_back.domain.member.recode;

import com.imchobo.sayren_back.domain.member.en.Provider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SocialUser(
        @NotNull(message = "Provider 정보가 필요합니다") Provider provider,
        @NotEmpty(message = "소셜 고유 아이디가 필요합니다") String providerUid,
        @NotEmpty(message = "이메일이 필요합니다") String email,
        @NotEmpty(message = "이름이 필요합니다") String name
) {}
