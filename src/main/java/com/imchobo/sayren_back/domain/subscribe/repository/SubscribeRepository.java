package com.imchobo.sayren_back.domain.subscribe.repository;


import com.imchobo.sayren_back.domain.subscribe.en.SubscribeStatus;
import com.imchobo.sayren_back.domain.subscribe.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
  // 멤버 아이디 조회(마이페이지)
  List<Subscribe> findByMember_Id(Long memberId);

  // 특정 회원의 구독 상태별 조회
  List<Subscribe> findByMember_IdAndStatus(Long memberId, SubscribeStatus status);

  // 구독 아이템 기준으로 구독 조회(1:1 관계로 확인시에)
  Optional<Subscribe> findByOrderItem_Id(Long orderItemId);

}
