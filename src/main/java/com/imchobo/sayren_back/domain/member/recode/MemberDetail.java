package com.imchobo.sayren_back.domain.member.recode;

import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.entity.Member2FA;
import com.imchobo.sayren_back.domain.member.entity.MemberProvider;
import com.imchobo.sayren_back.domain.member.entity.MemberTerm;


public record MemberDetail(Member member, MemberTerm memberTerm, MemberProvider memberProvider, Member2FA member2FA) {
}
