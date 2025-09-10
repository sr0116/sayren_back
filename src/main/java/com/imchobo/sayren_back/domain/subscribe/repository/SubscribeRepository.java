package com.imchobo.sayren_back.domain.subscribe.repository;


import com.imchobo.sayren_back.domain.exentity.Member;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository <Subscribe, Long> {
  // 멤버 아이디 조회

  List<Subscribe> findByMember(Member member);
  
  List<Subscribe> findByMember_MemberId(Long memberId);     // PK(Long)으로 조회
}
