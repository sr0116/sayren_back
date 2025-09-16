package com.imchobo.sayren_back.domain.subscribe.repository;


import com.imchobo.sayren_back.domain.exentity.MemberEx;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository <Subscribe, Long> {
  // 멤버 아이디 조회

//  List<Subscribe> findByMember(MemberEx memberEx);
  
//  List<Subscribe> findByMember_MemberId(Long memberId);     // PK(Long)으로 조회
}
