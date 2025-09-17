package com.imchobo.sayren_back.domain.member.recode;

import com.imchobo.sayren_back.domain.member.en.Provider;

public record SocialUser(Provider provider, String providerUid, String email, String name) {
}
