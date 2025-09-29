package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.util.HashUtil;
import com.imchobo.sayren_back.domain.member.entity.DeletedMember;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.DeletedMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletedMemberServiceImpl implements DeletedMemberService{
  private final DeletedMemberRepository deletedMemberRepository;
  private final HashUtil hashUtil;

  @Override
  public void deleteMember(Member member) {
    deletedMemberRepository.save(DeletedMember.builder()
        .email(hashUtil.encode(member.getEmail()))
        .member(member)
      .build());
  }

}