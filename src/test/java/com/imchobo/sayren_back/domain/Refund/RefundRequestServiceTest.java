package com.imchobo.sayren_back.domain.Refund;


import com.imchobo.sayren_back.domain.common.en.ReasonCode;
import com.imchobo.sayren_back.domain.member.en.MemberStatus;
import com.imchobo.sayren_back.domain.member.en.Role;
import com.imchobo.sayren_back.domain.member.entity.Member;
import com.imchobo.sayren_back.domain.order.entity.OrderItem;
import com.imchobo.sayren_back.domain.payment.en.PaymentStatus;
import com.imchobo.sayren_back.domain.payment.entity.Payment;
import com.imchobo.sayren_back.domain.payment.exception.PaymentNotFoundException;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestDTO;
import com.imchobo.sayren_back.domain.payment.refund.dto.RefundRequestResponseDTO;
import com.imchobo.sayren_back.domain.payment.refund.en.RefundRequestStatus;
import com.imchobo.sayren_back.domain.payment.refund.repository.RefundRequestRepository;
import com.imchobo.sayren_back.domain.payment.refund.service.RefundRequestService;
import com.imchobo.sayren_back.domain.payment.repository.PaymentRepository;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.entity.SubscribeRound;
import com.imchobo.sayren_back.domain.subscribe.subscribe_round.repository.SubscribeRoundRepository;
import com.imchobo.sayren_back.security.dto.MemberAuthDTO;
import com.imchobo.sayren_back.security.util.SecurityUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@SpringBootTest
@Log4j2
public class RefundRequestServiceTest {

  @Autowired
  private RefundRequestService refundRequestService;

  @Autowired
  private RefundRequestRepository refundRequestRepository;

  @Autowired
  private PaymentRepository paymentRepository;

//  @BeforeEach
  void setupSecurityContext() {
    // 테스트용 사용자 정보 생성
    MemberAuthDTO testUser = MemberAuthDTO.builder()
            .id(1L) // DB에 존재하는 member_id
            .roles(Set.of(Role.ADMIN))
            .status(MemberStatus.ACTIVE)
            .build();

    UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                    testUser,
                    null,
                    testUser.getAuthorities()
            );
    SecurityContextHolder.getContext().setAuthentication(auth);
  }



  // 사용자 취소 요청
//  @Test
  void testCreateRefundRequest() {
    // given: 임의 OrderItem + Member + Payment 세팅
    Member member = Member.builder().id(6L).build();
    OrderItem orderItem = OrderItem.builder().id(3L).build();

    Payment payment = Payment.builder()
            .id(194L)
            .member(member)
            .orderItem(orderItem)
            .amount(340L)
            .paymentStatus(PaymentStatus.PAID)
            .merchantUid("pay_fa3cbcdfcc3543af91fa454fb42dcd83")
            .build();
    paymentRepository.save(payment);

    RefundRequestDTO dto = RefundRequestDTO.builder()
//            .orderItemId(orderItem.getId())
            .reasonCode(ReasonCode.USER_REQUEST)
            .build();

    // when
    RefundRequestResponseDTO response = refundRequestService.createRefundRequest(dto);

    // then
//    Assertions.assertNotNull(response.getOrderItemId());
    Assertions.assertEquals(RefundRequestStatus.PENDING, response.getStatus());
    Assertions.assertEquals(ReasonCode.USER_REQUEST, response.getReasonCode());
  }

  // 관리자 취소 요청

//  @Test
//  void testProcessRefundRequest_Approve() {
//    // given: 환불 요청 생성
//    Member member = Member.builder().id(1L).build();
//    OrderItem orderItem = OrderItem.builder().id(1L).build();
//
//    Payment payment = Payment.builder()
//            .id(200L)
//            .member(member)
//            .orderItem(orderItem)
//            .amount(100000L)
//            .paymentStatus(PaymentStatus.PAID)
//            .build();
//    paymentRepository.save(payment);
//
//    RefundRequestDTO dto = RefundRequestDTO.builder()
//            .orderItemId(orderItem.getId())
//            .reasonCode(ReasonCode.USER_REQUEST)
//            .build();
//
//    RefundRequestResponseDTO created = refundRequestService.createRefundRequest(dto);
//
//    // when: 관리자 승인 처리
//    RefundRequestResponseDTO processed =
//            refundRequestService.processRefundRequest(created.getRefundRequestId(),
//                    RefundRequestStatus.APPROVED,
//                    ReasonCode.USER_REQUEST);
//
//    // then
//    Assertions.assertEquals(RefundRequestStatus.APPROVED, processed.getStatus());
//
//    // Payment 상태도 REFUNDED로 바뀌었는지 확인
//    Payment updated = paymentRepository.findById(payment.getId())
//            .orElseThrow();
//    Assertions.assertEquals(PaymentStatus.REFUNDED, updated.getPaymentStatus());
//  }

  @Autowired
  private SubscribeRoundRepository subscribeRoundRepository;

//  @Test
  @Transactional
  void testAdminApproveRefund() {
//    Long refundRequestId = 6L; // DB 존재하는 refund_request_id
//
//    // order_item_id = 3 에 해당하는 결제 조회
//    List<Payment> payments = paymentRepository.findByOrderItemId(3L);
//    Payment payment = payments.stream()
//            .findFirst()
//            .orElseThrow(() -> new PaymentNotFoundException(3L));
//
//    // 실제 DB에 존재하는 회차 2288을 조회
//    SubscribeRound round = subscribeRoundRepository.findById(2288L)
//            .orElseThrow(() -> new RuntimeException("회차 2288 없음"));
//    payment.setSubscribeRound(round);
//
//    // 환불 승인 처리 실행
//    RefundRequestResponseDTO response = refundRequestService.processRefundRequest(
//            refundRequestId,
//            RefundRequestStatus.APPROVED,
//            ReasonCode.USER_REQUEST
//    );
//
//    Assertions.assertEquals(RefundRequestStatus.APPROVED, response.getStatus());
  }

}
