package com.imchobo.sayren_back.domain.payment.refund.repository;


import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.payment.refund.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
  // 로그인 사용자 기준 정렬 조회
  List<RefundRequest> findByMemberOrderByRegDateDesc(Member member);

  // 관리자용
  List<RefundRequest> findByMember_Id(Long memberId);
}
