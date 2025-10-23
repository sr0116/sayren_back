package com.imchobo.sayren_back.domain.member.service;

import com.imchobo.sayren_back.domain.common.exception.SayrenException;
import com.imchobo.sayren_back.domain.common.util.HashUtil;
import com.imchobo.sayren_back.domain.member.entity.DeletedMember;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.member.repository.DeletedMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

  @Override
  public void emailVaildate(String email) {
    List<DeletedMember> deletedMember = deletedMemberRepository.findByEmail(hashUtil.encode(email));
    LocalDateTime now = LocalDateTime.now();
    if(!deletedMember.isEmpty()){
      deletedMember.forEach(d -> {
        if(ChronoUnit.DAYS.between(d.getRegDate(), now) <= 30){
          throw new SayrenException("DELETED_MEMBER", "삭제된지 30일이 지나지 않아 가입이 불가합니다.");
        }
      });
    }
  }
}